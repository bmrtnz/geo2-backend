-- f_submit_envoi_detail_seccom

-- soumet une demande d'impression de document détail station à maddog via fichier standard exp
-- le document sera envoyé sur l'imprimante de la secrétaire commerciale détentrice de l'ordre
-- AR 26/01/05 création
-- AR 19/10/10 utilisation de f_genere_ordre_exp_via_file au lieu de f_genere_ordre_exp_art (abandon utilisation de clipboard!)
-- AR 19/04/11 reprise du code var l'impression auto ne se fait plus (ls_imp_full vide sans que j'en trouve la raison)
--						remise en oeuvre de SetTransObjet
--						la clé du mystère il y avait une erreur de syntaxe dans le premier select (amanque virgule séparant 2 chanps)
-- AR 26/04/11 passe flag_archive à O pour pouvoir consulter le PDF a posteriori
-- AR 26/04/11 impref au lieu de imp_id dans geo_envois
-- AR 07/09/11 ajout du cas envoi par mail (pour VDL et sites eloignés du LAN MTB)
-- AR 09/11/11 récupère imp_ref depuis user plutot que depuis geo_person (unification des traitements)
-- AR 15/12/11 implémente f_pk_geo_envois et f_pk_geoenvdem (PK hexadecimale)
-- SL 13/05/13	Supression de l'impression des détails d'expédition. Un mail de notification est envoyé à l'assistante commercial
--					lorsque toutes les lignes de l'ordre sont cloturérees par les stations. Un autre mail peut être envoyé au bureau d'achat
--					en modifiant les contacts de la station : 

CREATE OR REPLACE PROCEDURE F_SUBMIT_ENVOI_DETAIL_SECCOM (
    arg_ord_ref IN geo_ordre.ORD_REF%TYPE,
    arg_fou_code IN geo_envois.tie_code%TYPE,
    log_name IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ll_row number;
    ll_env_code number;
    ll_end_code number;
    ll_rc number;
    li_rc number;
    ll_nb_flux_clodet number;
    ls_rc varchar2(50);
    ls_soc_code varchar2(50);
    ls_cam_code varchar2(50);
    ls_nordre varchar2(50);
    ls_per_codeass varchar2(50);
    ls_per_codecom varchar2(50);
    ls_sco_code varchar2(50);
    ls_imp_full varchar2(50);
    ls_env_code varchar2(50);
    ls_end_code varchar2(50);
    ls_imp_ref varchar2(50);
    ls_email varchar2(50);
    ls_noti_detail_mail varchar2(50);
    ls_nom_utilisateur varchar2(50);
    ls_nb_lignes varchar2(50);
    ls_nb_lignes_exp varchar2(50);
    ls_MailSubject varchar2(50);
    ls_MailBody varchar2(50);
    ls_cmd varchar2(50);
    -- ls_mail_ass varchar2(50)[]
    ls_client varchar2(50);
    ls_entrep varchar2(50);
    is_crlf varchar2(50);
    is_crlf varchar2(50) := '~r~n';

    -- OleObject wsh
    ldt_now timestamp;

    -- datastore lds_x
    -- lds_x = create datastore
    -- lds_x.dataobject = "d_import_docenv"
    -- lds_x.SetTransObject(SQLCA)

    -- On recupère les adresses des personnes tiers qui recoivent la notification a partir de tous les arg_fou_codesseur impliqués dans l'ordre
    cursor CMAIL is
    select con_acces1 from geo_contac where valide = 'O' and FLU_CODE = 'CLODET' AND MOC_CODE = 'MAI' AND con_tiers IN (SELECT FOU_CODE FROM GEO_ORDLOG WHERE ORD_REF = arg_ord_ref);
BEGIN
    msg := '';
    res := 0;

    -- on récupère dans geo_ordre les codes nécessaires à l'envoi 
    -- plus les info sur le client et l'entropôt pour les notifications de clôture des details d'expé
    select soc_code, cam_code, nordre, per_codecom, per_codeass, sco_code, cli_code, cen_code
    into ls_soc_code, ls_cam_code, ls_nordre, ls_per_codeass, ls_per_codecom, ls_sco_code, ls_client, ls_entrep
    from geo_ordre where ord_ref = arg_ord_ref;

    BEGIN
        -- faut il envoyer un mail plutôt qu'un impression ? (07/09/11)
        -- ajout ls_nom_utilisateur 09/11/11
        select geo_user.email, geo_user.noti_detail_mail, geo_user.nom_utilisateur
        into  ls_email, ls_noti_detail_mail, ls_nom_utilisateur
        from geo_person, geo_user
        where geo_user.nom_utilisateur = geo_person.per_username
        and geo_person.per_code = ls_per_codeass;
    exception when others then
        ls_noti_detail_mail	:= 'N';
    end;

    -- on récupère le nom UNC de l'imprimante de la secrétaire
    -- modif ci-dessous du 09/11/11 on récupère directement dans geo_user (imprimante gérée dans les préférences par l'utilisateur de geo)
    select geo_imprim.imp_id, geo_imprim.imp_ref
    into ls_imp_full, ls_imp_ref
    from geo_imprim, geo_user
    where geo_user.nom_utilisateur = ls_nom_utilisateur
    and geo_imprim.imp_ref = geo_user.imp_ref;
        
    -- on demande un numéro d'envoi (n° de document)
    -- f_ecrit_log(log_name,'ls_imp_full=' + ls_imp_full + ' ls_per_codeass=' + ls_per_codeass + is_crlf)

    select f_seq_env_num() into ls_env_code from dual;
    select f_seq_end_num() into ls_end_code from dual;

    -- on crée une occurence dans la datastore adhoc
    ldt_now	:= current_timestamp;

    -- lds_x.SetItem(ll_row,'docenv_cle',ls_env_code)	-- old=number new=hexa
    -- lds_x.SetItem(ll_row,'soc_code', ls_soc_code)
    -- lds_x.SetItem(ll_row,'cam_code', ls_cam_code)
    -- lds_x.SetItem(ll_row,'nordre', ls_nordre)
    -- lds_x.SetItem(ll_row,'version','001')
    -- lds_x.SetItem(ll_row,'tyt_code','F')	-- commercial
    -- lds_x.SetItem(ll_row,'tie_code', arg_fou_code) 		-- per_code
    -- lds_x.SetItem(ll_row,'flu_code' ,'DETAIL')
    -- lds_x.SetItem(ll_row,'ctx_code','') 
    -- if ls_noti_detail_mail = 'N' then
    --     lds_x.SetItem(ll_row,'moc_code','DOC' ) -- replace IMP par DOC pour que le PDF soit généré mais non imprimé par geo4_docserver
    --     lds_x.SetItem(ll_row,'ads1', ls_email)
    -- else
    --     lds_x.SetItem(ll_row,'moc_code','MAI' )
    --     lds_x.SetItem(ll_row,'ads1', ls_email)
    -- end if
    -- lds_x.SetItem(ll_row,'map_symb','FLUVAR=,TIERS=' + arg_fou_code + ',SOCIETE=' + ls_soc_code + ',LAN=FR' + ',ID=' + ls_env_code) -- symboles
    -- --lds_x.SetItem(ll_row,'map_file', 'detail_station_v1.map')
    -- lds_x.SetItem(ll_row,'map_file', 'detail_station_v2.map') --new champs logistiques
    -- lds_x.SetItem(ll_row,'dot_file', 'detail_station.dot')
    -- lds_x.SetItem(ll_row,'imp_id','')
    -- lds_x.SetItem(ll_row,'flag_archive','O')
    -- lds_x.SetItem(ll_row,'contact', ls_per_codeass)

    if ls_noti_detail_mail = 'N' then
        insert into geo_envois (
            soc_code, cam_code, nordre, 
            end_code, env_code, flu_code, 
            flu_code_compl, fluvar, moc_code, 
            acces1, tyt_code, 
            tie_code, per_codeass, per_codecom, 
            demdat, soudat, envdat, 
            ackdat, nbtent, env_desc, 
            sco_code, lan_code, 
            imp_id, refmoc, 
            ord_ref, doc_filename, contact )
        values (
            ls_soc_code, ls_cam_code, ls_nordre,
            ls_end_code, ls_env_code, 'DETAIL',
            '', '', 'MAI',
            ls_email, 'F',
            arg_fou_code, ls_per_codecom, ls_per_codeass,
            ldt_now, ldt_now, ldt_now,
            ldt_now, 1, 'OK',
            ls_sco_code, 'FR',
            '', ls_env_code,
            arg_ord_ref, ls_env_code, ls_per_codeass);	
    else
        insert into geo_envois (
            soc_code, cam_code, nordre, 
            end_code, env_code, flu_code, 
            flu_code_compl, fluvar, moc_code, 
            acces1, tyt_code, 
            tie_code, per_codeass, per_codecom, 
            demdat, soudat, envdat, 
            ackdat, nbtent, env_desc, 
            sco_code, lan_code, 
            imp_id, refmoc, 
            ord_ref, doc_filename, contact )
        values (
            ls_soc_code, ls_cam_code, ls_nordre,
            ls_end_code, ls_env_code, 'DETAIL',
            '', '', 'MAI',
            ls_email, 'F',
            arg_fou_code, ls_per_codecom, ls_per_codeass,
            ldt_now, ldt_now, ldt_now,
            ldt_now, 1, 'OK',
            ls_sco_code, 'FR',
            '', ls_env_code,
            arg_ord_ref, ls_env_code, ls_per_codeass);	
    end if;
    commit;

    if ls_noti_detail_mail = 'O' then
        null;
        -- Ceux qui demandent les envois par mail continuent de les recevoir
        -- ls_rc	= f_genere_ordre_exp_via_file(ordref, gs_dir_geo + ls_env_code + '.exp', false, lds_x)
        -- f_ecrit_log(log_name,'f_genere_ordre_exp_art_' + arg_fou_code + '_DETAIL_' + ordref + '_' + ls_env_code + '.exp --> ls_rc=' +ls_rc + is_crlf)
    else
        -- On ne génère plus le fichier exp on attend que la totalité des expe soient cloturées 
        -- et on envoie juste un mail d'info global a la fin.
        select count(*) into ls_nb_lignes from geo_ordlog L where L.ord_ref = arg_ord_ref;
        select count(*) into ls_nb_lignes_exp from geo_ordlog L where L.ord_ref = arg_ord_ref and L.flag_exped_fournni = 'O';
        
        if ls_nb_lignes = ls_nb_lignes_exp then
            -- Tous les détails sont clôturés, envoi d'un mail de notification à l'assistante commerciale
            -- wsh = CREATE OleObject
            -- li_rc = wsh.ConnectToNewObject( "WScript.Shell" )
            if li_rc < 0 then
                null;
                -- f_ecrit_log(log_name,"%%ERREUR - dans clôture détails d'expedition : exécution du client mail : " + ls_email + ", Ordre Ref : " + ordref + is_crlf)
            else
                ls_MailSubject := 'Clôture des détails d''expédition. Ordre : ' || ls_nordre || ', client : ' || ls_client || ', entrepôt : ' || ls_entrep;
                if ls_email is null or ls_email = '' then 
                    ls_email := 'stephane@blue-whale.com';
                    ls_MailSubject := '%%ERREUR 	DEST envoi clôture des détails d''expédition. Ordre : ' || ls_nordre || ', dest : ' || ls_email;
                end if;

                -- if gs_test = 'O' then
                --     ls_email := 'stephane@blue-whale.com';
                -- end if;
                -- li_rc	= f_send_mail_ext('admin@blue-whale.com', ls_email, "", ls_MailSubject, ls_MailSubject, "", gs_Smtp_Server, gs_SmtpPort, gs_SmtpLogin, gs_SmtpPasswd, gs_mail_exe, log_name, false)
                
                -- On traite les personnes du flux CLODET
                -- for ll_row = 1 to ll_nb_flux_clodet
                --     ls_email = ls_mail_ass[ll_row]
                --     ls_MailSubject = "Clôture des détails d'expédition. Ordre : " + ls_nordre + ", client : " + ls_client + ", entrepôt : " + ls_entrep
                --     if IsNull(ls_email) or ls_email = "" then 
                --         ls_MailSubject = "%%ERREUR 	DEST flux CLODET envoi clôture des détails d'expédition. Ordre : " + ls_nordre + ", dest : " + ls_email
                --         ls_email = "stephane@blue-whale.com"
                --     end if
        
                --     if gs_test = 'O' then ls_email = "stephane@blue-whale.com"
                --     li_rc	= f_send_mail_ext('admin@blue-whale.com', ls_email, "", ls_MailSubject, ls_MailSubject, "", gs_Smtp_Server, gs_SmtpPort, gs_SmtpLogin, gs_SmtpPasswd, gs_mail_exe, log_name, false)
                -- next	
                -- DESTROY wsh
            end if;
        end if;
    end if;

    -- ls_rc	= f_genere_ordre_exp_via_file(ordref, gs_dir_geo + ls_env_code + '.exp', false, lds_x)
    -- f_ecrit_log(log_name,'f_genere_ordre_exp_art_' + arg_fou_code + '_DETAIL_' + ordref + '_' + gs_dir_geo + ls_env_code + '.exp --> ls_rc=' +ls_rc + is_crlf)
    -- on nettoye
    -- destroy lds_x

    -- ls_rc	= f_move_files(gs_dir_geo + '*.exp', gs_dir_doc_out)
    -- if ls_rc <> '' then
    --     f_ecrit_log(log_name, 'of_submit_envoi_detail_seccom ' + ordref + ' - ' + ls_env_code + '.exp --> ls_rc=' +ls_rc + is_crlf)
    -- end if

    res := 1;
    msg := 'OK';
END;
/
