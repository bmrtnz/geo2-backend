-- f_cloture_log_grp
CREATE OR REPLACE PROCEDURE F_CLOTURE_LOG_GRP (
    arg_ordref IN geo_gest_regroup.ORD_REF_ORIG%TYPE,
    arg_fou_code IN geo_gest_regroup.fou_code_orig%TYPE,
    -- ls_msg ???
    -- TRUE ???
    arg_fou_ref_doc IN varchar2,
    -- ls_file_log ???
    arg_immatriculation    IN geo_ordlog.FOU_CODE%TYPE,
    arg_container  IN geo_ordlog.FOU_CODE%TYPE,
    arg_plomb  IN geo_ordlog.FOU_CODE%TYPE,
    arg_detecteur_temp  IN geo_ordlog.FOU_CODE%TYPE,
    arg_certif_controle  IN geo_ordlog.FOU_CODE%TYPE,
    arg_certif_phyto  IN geo_ordlog.FOU_CODE%TYPE,
    arg_num_detecteur  IN geo_ordlog.FOU_CODE%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_err varchar2(50);
    ls_msg varchar2(50);
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

    mode varchar2(50);
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
    
    ls_err_msg := '';

    /*
    s_detail_entete		lst_detail_entete
    s_detail_ligne		lst_detail_ligne
    s_detail_total_exp	lst_detail_total_exp

    lds_x = create datastore
    lds_x.dataobject = "d_input_status"
    */

    de_ordref = arg_ordref

    -- On va chercher le fournisseur
    de_fou_code = arg_fou_code;

    --lst_detail_entete.s_ref_doc	= arg_ref_doc
    --lst_detail_entete.s_ref_log	= arg_ref_log
    de_immatriculation = arg_immatriculation  ;
    de_container = arg_container;
    de_plomb = arg_plomb;
    de_detecteur_temp = arg_detecteur_temp;
    de_certif_controle  = arg_certif_controle;
    de_certif_phyto = arg_certif_phyto;
    de_num_detecteur = arg_num_detecteur;

    de_fou_ref_doc = arg_fou_ref_doc;

    -- On compte les palettes au sol et palettes bleues
    l_pal_nb_sol				:= 0;
    l_pal_nb_pb100x120	:= 0;
    l_pal_nb_pb80x120	:= 0;
    l_pal_nb_pb60x80		:= 0;
    l_pal_nb					:= 0;
BEGIN
    msg := '';
    res := 0;

    -- Nonbre total de palettes
    select count(SSCC) into l_pal_nb from geo_traca_detail_pal where ord_ref = arg_ordref AND fou_code = arg_fou_code AND VALIDE = 'O' AND PAL_CODE <> 'DEPAL';

    -- Nonbre total de palettes sol
    select count(SSCC) into l_pal_nb_sol from geo_traca_detail_pal where ord_ref = arg_ordref AND fou_code = arg_fou_code and PAL_SOL = '1' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 100x120
    select count(SSCC) into l_pal_nb_pb100x120 from geo_traca_detail_pal where ord_ref = arg_ordref AND fou_code = arg_fou_code and PAL_CODE = 'PB' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 80x120
    select count(SSCC) into l_pal_nb_pb80x120 from geo_traca_detail_pal where ord_ref = arg_ordref AND fou_code = arg_fou_code and PAL_CODE = 'PB812' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 60x80
    select count(SSCC) into l_pal_nb_pb60x80 from geo_traca_detail_pal where ord_ref = arg_ordref AND fou_code = arg_fou_code and PAL_CODE = 'PB68' AND VALIDE = 'O';


    lst_detail_entete.s_pal_nb_sol				= string(l_pal_nb_sol)
    lst_detail_entete.s_pal_nb_pb100x120	= string(l_pal_nb_pb100x120)
    lst_detail_entete.s_pal_nb_pb80x120		= string(l_pal_nb_pb80x120)
    lst_detail_entete.s_pal_nb_pb60x80		= string(l_pal_nb_pb60x80)
    lst_detail_entete.s_pal_nb					= string(l_pal_nb)



    mode = ''
    if arg_cloturer = true then mode = 'B_CLOTURER'

    ls_msg = ""
    ll_rc = f_detail_entete_sauve(lst_detail_entete, mode, ls_msg, lds_x, true)
    if ls_msg <> '' then
        ls_err_msg += "Erreur entete detail : " + ls_msg
    end if
    
    -- Cloture des lignes
    lst_detail_total_exp.d_tot_exp_nb_pal		= 0
    lst_detail_total_exp.d_tot_exp_nb_col		= 0
    lst_detail_total_exp.d_tot_exp_pds_brut	= 0
    lst_detail_total_exp.d_tot_exp_pds_net	= 0

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
    where 	ord_ref = :arg_ordref and 
                fou_code = :arg_fou_code;


    declare C1 cursor for
    SELECT DISTINCT
        L.ORL_REF
    FROM
        GEO_ORDRE O,
        GEO_ORDLIG L,
        GEO_TRACA_LIGNE TL
    WHERE 		  
        O.ORD_REF = :arg_ordref AND
        L.ORD_REF = O.ORD_REF AND
        L.FOU_CODE = :arg_fou_code AND
        TL.ORL_REF = L.ORL_REF AND
        TL.VALIDE = 'O';
        
    open C1;
    fetch C1 into :ls_ORL_REF;



    ll_ligne = 1
    do while sqlca.SQLcode = 0


        
        l_exp_nb_pal = 0
        l_exp_nb_col = 0
        l_exp_nb_pal_col = 0
        d_pds_net = 0
        d_pds_brut = 0
        
        ll_poids_palettes = 0

        -- Il faut compter le nombre de palettes pour la ligne, en écartant les palettes où elle est pas seules et la première
        if l_pal_nb <> 0 then
            -- On compte le nombre de pallete ou est prensente la ligne
            SELECT COUNT(DISTINCT(TL.SSCC)) INTO :l_exp_nb_pal FROM GEO_TRACA_LIGNE L, GEO_TRACA_DETAIL_PAL TL WHERE 	L.ORL_REF = :ls_ORL_REF AND L.VALIDE = 'O' AND TL.FOU_CODE = :arg_fou_code AND L.REF_TRACA = TL.REF_TRACA;

            declare C2 cursor for
            SELECT L.REF_TRACA FROM GEO_TRACA_LIGNE L, GEO_TRACA_DETAIL_PAL TL WHERE L.ORL_REF = :ls_ORL_REF AND L.VALIDE = 'O' AND TL.FOU_CODE = :arg_fou_code AND L.REF_TRACA = TL.REF_TRACA;   
            

            ll_exclure_pal = 0
            
            open C2;


            
            fetch C2 into :ls_ref_traca;
            do while sqlca.SQLcode = 0
                -- Test si la ligne est la première sur la palette; cas où plusieur ligne d'ordre sur même palette il faudra compter qu'un palette
                SELECT MIN(ORL_REF) INTO :ls_min_orl_ref FROM GEO_TRACA_LIGNE WHERE REF_TRACA = :ls_ref_traca AND VALIDE = 'O';
                
                -- On compte aussi le nombre de code arbo pour cette ligne parce que la ligne sera compter autant de fois avec le poids de la palette
                ll_nb_arbo = 0
                select  count(ref_traca_ligne) into :ll_nb_arbo from geo_traca_ligne where  REF_TRACA = :ls_ref_traca and orl_ref = :ls_ORL_REF  AND VALIDE = 'O';
                
                if ls_min_orl_ref <> ls_ORL_REF then
                    ll_exclure_pal++
                else
                    ll_poids_palette = 0
                    -- On calcul le poids de la palette pour l'ajouter au poids brut de la ligne
                    select TL.PDS_BRUT - sum(L.PDS_BRUT) into :ll_poids_palette from GEO_TRACA_LIGNE L, GEO_TRACA_DETAIL_PAL TL
                    where  TL.REF_TRACA = L.REF_TRACA AND TL.REF_TRACA = :ls_ref_traca AND L.VALIDE = 'O'
                    group by TL.PDS_BRUT;
                    
                    if ll_nb_arbo <> 0 then ll_poids_palettes += (ll_poids_palette / ll_nb_arbo )
                    
                end if
                fetch C2 into :ls_ref_traca;
            loop
        
            close C2;
            
            -- On enleve les lignes en trop
            l_exp_nb_pal -= ll_exclure_pal
            if l_exp_nb_pal < 0 then l_exp_nb_pal = 0
            
        else
            l_exp_nb_pal = 0
        end if
        
        -- Nombre de colis
        --select sum(NB_COLIS) into :l_exp_nb_col from GEO_TRACA_LIGNE where ORL_REF = :ls_ORL_REF AND VALIDE = 'O';

        -- Nombre de colis par palette
        --select MAX(NB_COLIS) into :l_exp_nb_pal_col from GEO_TRACA_LIGNE where ORL_REF = :ls_ORL_REF AND VALIDE = 'O';

        -- Poids net 
        --select sum(PDS_NET) into :d_pds_net from GEO_TRACA_LIGNE where ORL_REF = :ls_ORL_REF AND VALIDE = 'O';

        -- Poids brut
        --select sum(PDS_BRUT) into :d_pds_brut from GEO_TRACA_LIGNE where ORL_REF = :ls_ORL_REF AND VALIDE = 'O';

        select
            sum(L.NB_COLIS),
            MAX(L.NB_COLIS),
            sum(L.PDS_NET),
            sum(L.PDS_BRUT)
        into
            :l_exp_nb_col,
            :l_exp_nb_pal_col,
            :d_pds_net,
            :d_pds_brut
        from
            GEO_TRACA_LIGNE L,
            GEO_TRACA_DETAIL_PAL TL
        where 
            L.ORL_REF = :ls_ORL_REF AND L.VALIDE = 'O' AND
            TL.FOU_CODE = :arg_fou_code AND
            TL.REF_TRACA = L.REF_TRACA;

        lst_detail_ligne.s_orl_ref = ls_ORL_REF
        lst_detail_ligne.s_exp_nb_col 		= string(l_exp_nb_col)		-- N
        lst_detail_ligne.s_pal_nb_col 		= string(l_exp_nb_pal_col)	-- O
        lst_detail_ligne.s_exp_pds_brut		= string(d_pds_brut + ll_poids_palettes)	-- P
        lst_detail_ligne.s_exp_pds_net		= string(d_pds_net)	-- Q
        lst_detail_ligne.s_cq_ref				= ''	-- T
        lst_detail_ligne.s_exp_nb_pal		= string(l_exp_nb_pal) -- M
        

        SELECT OL.ART_REF, OL.ACH_BTA_CODE, OL.VTE_BTA_CODE INTO :ls_art_ref, :s_bta_ach, :s_bta_vte FROM GEO_ORDLIG OL WHERE OL.ORL_REF = :ls_ORL_REF;

        lst_detail_ligne.s_ach_qte			= string(f_get_qtt_per_bta(ls_art_ref, s_bta_ach, l_exp_nb_pal, l_exp_nb_col, d_pds_net)	)-- R
        lst_detail_ligne.s_vte_qte			= string(f_get_qtt_per_bta(ls_art_ref, s_bta_vte, l_exp_nb_pal, l_exp_nb_col, d_pds_net)	) -- S
        
        ls_msg = ""
        ll_rc = f_detail_ligne_sauve(lst_detail_ligne, string(ll_ligne), lst_detail_total_exp, ls_msg, lds_x, true)
        if not isnull(ls_msg) and ls_msg <> '' then
            ls_err_msg += "Erreur ligne detail : " + ls_msg
        end if
        
        ll_ligne++
        fetch C1 into :ls_ORL_REF;
    loop

    close C1;
    /*
    for ll_ligne = 1 to  UpperBound(arg_ligne)
    next 
    */	

    --DEBUT LLEF AUTOM. IND. TRANSP. + tri et emballage retrait
    select soc_code, sco_code into :ls_soc_code, :ls_sco_code from geo_ordre where ord_ref = :arg_ordref;
    if ls_soc_code = 'UDC' and ls_sco_code = 'RET' then
        f_chgt_qte_art_ret(arg_ordref)
    end if
    --FIN LLEF

    -- Finalement on actualise le nombre de palettes au sol pour le trp
    f_actualise_nb_palettes_sol(arg_ordref, arg_fou_code)

    -- Traitement des KIT article
    f_set_detail_kit_article(arg_ordref, arg_fou_code)

    if arg_cloturer = true then

        if ls_err_msg = '' then
        
            destroy lds_x
            commit;
        
            insert into geo_ordre_save_log  (ord_ref, geo_user) values(:de_ordref, :de_fou_code);
            commit;
        

            
            if arg_cloturer = true then
                ls_rc	= f_submit_envoi_detail_seccom(de_ordref, de_fou_code, arg_log_name)
            end if
        

            ls_total	= 'total : ' + &
                        string(lst_detail_total_exp.d_tot_exp_nb_pal) + ' pal  -  ' + &
                        string(lst_detail_total_exp.d_tot_exp_nb_col) + ' colis  -  ' + &
                        string(lst_detail_total_exp.d_tot_exp_pds_brut) + ' brut  -  ' + &
                        string(lst_detail_total_exp.d_tot_exp_pds_net) + ' net  -'
            
            --of_ecrit_log(is_current_log_file, "Total detail : " + ls_total)
        
            return 0
        else
            rollback;
            -- on renvoie la liste des ID de tous les champs avec leur status
            ls_xml	= ls_err_msg + '~r~n<br>'
            /*
            for ll_ligne = 1 to lds_x.RowCount()
                ls_xml	= ls_xml + lds_x.GetItemString(ll_ligne, 'champ') + '" ok="' + lds_x.GetItemString(ll_ligne, 'status') + '"/>' + '~r~n<br>'
            next
            */
            /*
            string	ls_MailSubject
            string ls_email
            
            ls_MailSubject = "ERREUR envoie détails d'expédition : " + de_fou_code + " - Ordre : " + arg_ordref
            ls_email = "stephane@blue-whale.com"
            f_send_mail_ext('admin@blue-whale.com', ls_email, "", ls_MailSubject, ls_xml, "", gs_Smtp_Server, gs_SmtpPort, gs_SmtpLogin, gs_SmtpPasswd, gs_mail_exe, is_log_name, false)
            */
                        
            destroy lds_x
            arg_err_msg += "Erreur entete detail : " +  ls_xml
            --arg_err_msg += "Erreur entete detail : " +  ls_xml

            return -2
        end if
    else
        arg_err_msg = ''
        commit;
        return 0
    end if

    res := 1;
    msg := 'OK';
END;
/
