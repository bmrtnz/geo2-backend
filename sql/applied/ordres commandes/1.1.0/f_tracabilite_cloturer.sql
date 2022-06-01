CREATE OR REPLACE PROCEDURE F_TRACABILITE_CLOTURER (
    arg_ord_ref IN geo_ordre.ORD_REF%TYPE,
    arg_cloturer IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_err varchar2(50);
    ls_ok varchar2(50);
    ls_xml varchar2(50);
    -- datastore	lds_x
    ll_rc number;
    ll_count number;
    ls_err_msg varchar2(50);

    l_pal_nb_sol number				:= 0;
    l_pal_nb_pb100x120 number	:= 0;
    l_pal_nb_pb80x120 number		:= 0;
    l_pal_nb_pb60x80 number		:= 0;
    l_pal_nb number					:= 0;
    ll_poids_palettes number;
    ll_poids_palette number;
    ll_nb_arbo number;

    ls_art_ref varchar2(50);
    s_bta_vte varchar2(50);
    s_bta_ach varchar2(50);

    ll_exclure_pal number;

    ls_mode varchar2(50);
    ls_ORL_REF varchar2(50);
    ls_min_orl_ref varchar2(50);
    ls_ref_traca varchar2(50);

    l_exp_nb_pal number;
    l_exp_nb_pal_col number;
    l_exp_nb_col number;
    d_pds_net number;
    d_pds_brut number;
    ll_ligne number;

    ls_rc varchar2(50);
    ls_total varchar2(50);

    ll_sum_ach_qte number := 0;      --LLEF
    ll_sum_vte_qte  number := 0;      --LLEF
    ls_soc_code varchar2(50);
    ls_sco_code varchar2(50);  --LLEF
    
    /*
    s_detail_entete		lst_detail_entete
    s_detail_ligne		lst_detail_ligne
    s_detail_total_exp	lst_detail_total_exp

    lds_x = create datastore
    lds_x.dataobject = "d_input_status"
    */

    -- de_ordref = arg_ord_ref

    -- On va chercher le fournisseur
    -- de_fou_code = l_fou_code;

    --lst_detail_entete.s_ref_doc	= arg_ref_doc
    --lst_detail_entete.s_ref_log	= arg_ref_log
    -- de_immatriculation = arg_immatriculation  ;
    -- de_container = arg_container;
    -- de_plomb = arg_plomb;
    -- de_detecteur_temp = arg_detecteur_temp;
    -- de_certif_controle  = arg_certif_controle;
    -- de_certif_phyto = arg_certif_phyto;
    -- de_num_detecteur = arg_num_detecteur;
    -- de_fou_ref_doc = arg_fou_ref_doc;

    l_imm_num varchar2(50);
    l_container varchar2(50);
    l_plomb varchar2(50);
    l_detecteur_temp varchar2(50);
    l_certificat_controle varchar2(50);
    l_certificat_phyto varchar2(50);
    l_numero_detecteur varchar2(50);
    l_fou_ref_doc varchar2(50);
    l_ref_log varchar2(50);
    l_fou_code varchar2(50);
    l_ref_doc varchar2(50);


    s_pal_nb varchar2(50);
    s_pal_nb_sol varchar2(50);
    s_pal_nb_pb100x120 varchar2(50);
    s_pal_nb_pb80x120 varchar2(50);
    s_pal_nb_pb60x80 varchar2(50);
    s_pal_nb_pr100x120 varchar2(50);
    s_pal_nb_pr80x120 varchar2(50);
    s_pal_nb_pr60x80 varchar2(50);

    -- Cloture des lignes
    d_tot_exp_nb_pal	number := 0;
    d_tot_exp_nb_col	number := 0;
    d_tot_exp_pds_brut	number := 0;
    d_tot_exp_pds_net	number := 0;

    s_orl_ref varchar2(50);
    s_exp_nb_col varchar2(50);
    s_pal_nb_col varchar2(50);
    s_exp_pds_brut varchar2(50);
    s_exp_pds_net varchar2(50);
    s_cq_ref varchar2(50);
    s_exp_nb_pal varchar2(50);
    s_ach_qte varchar2(50);
    s_vte_qte varchar2(50);
BEGIN
    msg := '';
    res := 0;

    SELECT
        fou_code,
        immatriculation,
        container,
        plomb,
        detecteur_temp,
        certif_controle,
        certif_phyto,
        locus_trace,
        fou_ref_doc,
        orx_ref,
        fou_code,
        fou_ref_doc
    INTO
        l_fou_code,
        l_imm_num,
        l_container,
        l_plomb,
        l_detecteur_temp,
        l_certificat_controle,
        l_certificat_phyto,
        l_numero_detecteur,
        l_fou_ref_doc,
        l_ref_log,
        l_fou_code,
        l_ref_doc
    from geo_ordlog
    where ord_ref = arg_ord_ref;

    -- Nonbre total de palettes
    select count(SSCC) into l_pal_nb from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code AND VALIDE = 'O' AND PAL_CODE <> 'DEPAL';

    -- Nonbre total de palettes sol
    select count(SSCC) into l_pal_nb_sol from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code and PAL_SOL = '1' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 100x120
    select count(SSCC) into l_pal_nb_pb100x120 from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code and PAL_CODE = 'PB' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 80x120
    select count(SSCC) into l_pal_nb_pb80x120 from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code and PAL_CODE = 'PB812' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 60x80
    select count(SSCC) into l_pal_nb_pb60x80 from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code and PAL_CODE = 'PB68' AND VALIDE = 'O';

    -- ONDOING
    s_pal_nb_sol				:= to_char(l_pal_nb_sol);
    s_pal_nb_pb100x120	:= to_char(l_pal_nb_pb100x120);
    s_pal_nb_pb80x120		:= to_char(l_pal_nb_pb80x120);
    s_pal_nb_pb60x80		:= to_char(l_pal_nb_pb60x80);
    s_pal_nb					:= to_char(l_pal_nb);

    ls_mode := '';
    if arg_cloturer = 'O' then ls_mode := 'B_CLOTURER'; end if;

    msg := '';
    f_detail_entete_sauve(arg_ord_ref, ls_mode, 'O', res, msg);
    if msg <> '' then
        ls_err_msg := ls_err_msg || 'Erreur entete detail : ' || msg;
    end if;

    -- Pour chaque ligne d'ordre
    -- On fait le cumule des colies et des pds

    -- Reinitialisation des lignes d'ordre pour ce fournisseur
    -- Pour tenir compte des lignes supprimées dans les détails qui doivent repasser à 0

    update geo_ordlig set
        exp_nb_pal = 0,
        exp_nb_col = 0,
        exp_pal_nb_col = 0,
        exp_pds_brut = 0,
        exp_pds_net = 0,
        vte_qte = 0,
        ach_qte = 0
    where 	ord_ref = arg_ord_ref and 
                fou_code = l_fou_code;


    declare
        cursor C1 is
        SELECT DISTINCT
            L.ORL_REF
        FROM
            GEO_ORDRE O,
            GEO_ORDLIG L,
            GEO_TRACA_LIGNE TL
        WHERE 		  
            O.ORD_REF = arg_ord_ref AND
            L.ORD_REF = O.ORD_REF AND
            L.FOU_CODE = l_fou_code AND
            TL.ORL_REF = L.ORL_REF AND
            TL.VALIDE = 'O';
        ll_ligne number := 1;
    begin
        for r in C1
        loop

            declare
                l_exp_nb_pal number := 0;
                l_exp_nb_col number := 0;
                l_exp_nb_pal_col number := 0;
                d_pds_net number := 0;
                d_pds_brut number := 0;
                
                ll_poids_palettes number := 0;
            begin
                -- Il faut compter le nombre de palettes pour la ligne, en écartant les palettes où elle est pas seules et la première
                if l_pal_nb <> 0 then
                    -- On compte le nombre de pallete ou est prensente la ligne
                    SELECT COUNT(DISTINCT(TL.SSCC)) INTO l_exp_nb_pal FROM GEO_TRACA_LIGNE L, GEO_TRACA_DETAIL_PAL TL WHERE 	L.ORL_REF = r.orl_ref AND L.VALIDE = 'O' AND TL.FOU_CODE = l_fou_code AND L.REF_TRACA = TL.REF_TRACA;

                    declare
                        cursor C2 is
                            SELECT L.REF_TRACA FROM GEO_TRACA_LIGNE L, GEO_TRACA_DETAIL_PAL TL WHERE L.ORL_REF = r.orl_ref AND L.VALIDE = 'O' AND TL.FOU_CODE = l_fou_code AND L.REF_TRACA = TL.REF_TRACA;   
                        ll_exclure_pal number := 0;
                    begin
                        for t in C2
                        loop
                            -- Test si la ligne est la première sur la palette; cas où plusieur ligne d'ordre sur même palette il faudra compter qu'un palette
                            SELECT MIN(ORL_REF) INTO ls_min_orl_ref FROM GEO_TRACA_LIGNE WHERE REF_TRACA = t.ref_traca AND VALIDE = 'O';
                            
                            -- On compte aussi le nombre de code arbo pour cette ligne parce que la ligne sera compter autant de fois avec le poids de la palette
                            ll_nb_arbo := 0;
                            select  count(ref_traca_ligne) into ll_nb_arbo from geo_traca_ligne where  REF_TRACA = t.ref_traca and orl_ref = r.orl_ref  AND VALIDE = 'O';
                            
                            if ls_min_orl_ref <> ls_ORL_REF then
                                 ll_exclure_pal := ll_exclure_pal + 1;
                            else
                                ll_poids_palette := 0;
                                -- On calcul le poids de la palette pour l'ajouter au poids brut de la ligne
                                select TL.PDS_BRUT - sum(L.PDS_BRUT) into ll_poids_palette from GEO_TRACA_LIGNE L, GEO_TRACA_DETAIL_PAL TL
                                where  TL.REF_TRACA = L.REF_TRACA AND TL.REF_TRACA = t.ref_traca AND L.VALIDE = 'O'
                                group by TL.PDS_BRUT;
                                
                                if ll_nb_arbo <> 0 then
                                    ll_poids_palettes := ll_poids_palettes + (ll_poids_palette / ll_nb_arbo );
                                end if;
                                
                            end if;
                        end loop;
                        -- On enleve les lignes en trop
                        l_exp_nb_pal := l_exp_nb_pal - ll_exclure_pal;
                        if l_exp_nb_pal < 0 then
                            l_exp_nb_pal := 0;
                        end if;
                    end;                    
                    
                else
                    l_exp_nb_pal := 0;
                end if;
                
                -- Nombre de colis
                --select sum(NB_COLIS) into :l_exp_nb_col from GEO_TRACA_LIGNE where ORL_REF = r.orl_ref AND VALIDE = 'O';

                -- Nombre de colis par palette
                --select MAX(NB_COLIS) into :l_exp_nb_pal_col from GEO_TRACA_LIGNE where ORL_REF = r.orl_ref AND VALIDE = 'O';

                -- Poids net 
                --select sum(PDS_NET) into :d_pds_net from GEO_TRACA_LIGNE where ORL_REF = r.orl_ref AND VALIDE = 'O';

                -- Poids brut
                --select sum(PDS_BRUT) into :d_pds_brut from GEO_TRACA_LIGNE where ORL_REF = r.orl_ref AND VALIDE = 'O';

                select
                    sum(L.NB_COLIS),
                    MAX(L.NB_COLIS),
                    sum(L.PDS_NET),
                    sum(L.PDS_BRUT)
                into
                    l_exp_nb_col,
                    l_exp_nb_pal_col,
                    d_pds_net,
                    d_pds_brut
                from
                    GEO_TRACA_LIGNE L,
                    GEO_TRACA_DETAIL_PAL TL
                where 
                    L.ORL_REF = r.orl_ref AND L.VALIDE = 'O' AND
                    TL.FOU_CODE = l_fou_code AND
                    TL.REF_TRACA = L.REF_TRACA;

                s_orl_ref := ls_ORL_REF;
                s_exp_nb_col 		:= to_char(l_exp_nb_col)	;	-- N
                s_pal_nb_col 		:= to_char(l_exp_nb_pal_col);	-- O
                s_exp_pds_brut		:= to_char(d_pds_brut + ll_poids_palettes);	-- P
                s_exp_pds_net		:= to_char(d_pds_net);	-- Q
                s_cq_ref				:= '';	-- T
                s_exp_nb_pal		:= to_char(l_exp_nb_pal); -- M
                

                SELECT OL.ART_REF, OL.ACH_BTA_CODE, OL.VTE_BTA_CODE INTO ls_art_ref, s_bta_ach, s_bta_vte FROM GEO_ORDLIG OL WHERE OL.ORL_REF = r.orl_ref;

                f_get_qtt_per_bta(ls_art_ref, s_bta_ach, l_exp_nb_pal, l_exp_nb_col, d_pds_net, res, msg, s_ach_qte);-- R
                f_get_qtt_per_bta(ls_art_ref, s_bta_vte, l_exp_nb_pal, l_exp_nb_col, d_pds_net, res, msg, s_vte_qte); -- S
                
                msg := '';
                f_detail_ligne_sauve(
                    s_orl_ref,
                    s_exp_nb_col,
                    s_pal_nb_col,
                    s_exp_pds_brut,
                    s_exp_pds_net,
                    s_cq_ref,
                    s_exp_nb_pal,
                    s_ach_qte,
                    s_vte_qte,
                    to_char(ll_ligne),
                    d_tot_exp_nb_pal,
                    d_tot_exp_nb_col,
                    d_tot_exp_pds_brut,
                    d_tot_exp_pds_net,
                    'O',
                    res,
                    msg
                );
                if msg is not null then
                    ls_err_msg := ls_err_msg || 'Erreur ligne detail : ' || msg;
                end if;
                
                ll_ligne := ll_ligne + 1;
            end;

        end loop;
    end;

    /*
    for ll_ligne = 1 to  UpperBound(arg_ligne)
    next 
    */	

    --DEBUT LLEF AUTOM. IND. TRANSP. + tri et emballage retrait
    select soc_code, sco_code into ls_soc_code, ls_sco_code from geo_ordre where ord_ref = arg_ord_ref;
    if ls_soc_code = 'UDC' and ls_sco_code = 'RET' then
        f_chgt_qte_art_ret(arg_ord_ref, res, msg);
    end if;
    --FIN LLEF

    -- Finalement on actualise le nombre de palettes au sol pour le trp
    f_actualise_nb_palettes_sol(arg_ord_ref, l_fou_code, res, msg);

    -- Traitement des KIT article
    f_set_detail_kit_article(arg_ord_ref, l_fou_code, res, msg);

    if arg_cloturer = 'O' then

        if ls_err_msg is null then
        
            commit;
        
            insert into geo_ordre_save_log  (ord_ref, geo_user) values(arg_ord_ref, l_fou_code);
            commit;
        

            
            if arg_cloturer = 'O' then
                f_submit_envoi_detail_seccom(arg_ord_ref, l_fou_code, '', res, msg);
            end if;
        

            ls_total	:= 'total : ' ||
                        to_char(d_tot_exp_nb_pal) || ' pal  -  ' ||
                        to_char(d_tot_exp_nb_col) || ' colis  -  ' ||
                        to_char(d_tot_exp_pds_brut) || ' brut  -  ' ||
                        to_char(d_tot_exp_pds_net) || ' net  -';
            
            --of_ecrit_log(is_current_log_file, "Total detail : " + ls_total)
        
            res := 1;
            return;
        else
            rollback;
            -- on renvoie la liste des ID de tous les champs avec leur status
            ls_xml	:= ls_err_msg || '~r~n<br>';
            /*
            for ll_ligne = 1 to lds_x.RowCount()
                ls_xml	= ls_xml + lds_x.GetItemto_char(ll_ligne, 'champ') + '" ok="' + lds_x.GetItemto_char(ll_ligne, 'status') + '"/>' + '~r~n<br>'
            next
            */
            /*
            to_char	ls_MailSubject
            to_char ls_email
            
            ls_MailSubject = "ERREUR envoie détails d'expédition : " + de_fou_code + " - Ordre : " + arg_ord_ref
            ls_email = "stephane@blue-whale.com"
            f_send_mail_ext('admin@blue-whale.com', ls_email, "", ls_MailSubject, ls_xml, "", gs_Smtp_Server, gs_SmtpPort, gs_SmtpLogin, gs_SmtpPasswd, gs_mail_exe, is_log_name, false)
            */
                        
            -- destroy lds_x
            msg := 'Erreur entete detail : ' ||  ls_xml;
            --arg_err_msg += "Erreur entete detail : " +  ls_xml

            res := 0;
            return;
        end if;
    else
        commit;
        res := 1;
        msg := 'OK';
    end if;

END;
/
