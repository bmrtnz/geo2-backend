CREATE OR REPLACE PROCEDURE FN_MAJ_ORDRE_REGROUPEMENT_V2(
    arg_ord_ref_origine varchar2,
    arg_soc_code varchar2,
    arg_entrepot_generic char,
    arg_username varchar2,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_code_chargement varchar2(500);
    ldt_depdatp date;
    ldt_livdatp date;
    ldt_etd_date date;
    ldt_eta_date date;
    ls_depdatp varchar2(50);
    ls_ord_ref_regroup varchar2(50);
    ls_cli_code varchar2(50);
    ls_cen_code varchar2(50);
    ls_transp varchar2(50);
    li_orl_lig number := 1;
    ls_orl_lig varchar2(50) := '01';
    ls_per_codecom varchar2(50);
    ls_per_codeass varchar2(50);
    ls_orl_lig_tmp varchar2(50);
    ls_orl_ref_tmp varchar2(50);
    li_cde_nb_col_tmp number;
    li_pal_nb_col_tmp number;
    li_pal_nb_col_sav number;
    ls_dev_code_orig varchar2(50);
    ldc_dev_taux_rgp number;


    ls_ord_ref_orig_tmp varchar2(50);
    ls_orl_ref varchar2(50);
    ls_art_ref_tmp varchar2(50);
    ls_fou_code_tmp varchar2(50);

    ls_nordre_regroup varchar2(50);
    li_grp_ori number := 0;
    li_grp_rgp number;
    li_grp_orig_tmp number;
    li_grp_rgp_tmp number;
    li_nb_lig number;
    ls_orl_lig_rgp_tmp varchar2(50);

    type p_vcr_tab_type is table of varchar2(64);
    ls_tab_ord_ref_orig p_vcr_tab_type := p_vcr_tab_type();
    ls_tab_orl_ref_orig p_vcr_tab_type := p_vcr_tab_type();
    ls_tab_art_ref_orig p_vcr_tab_type := p_vcr_tab_type();
    ls_tab_fou_code_orig p_vcr_tab_type := p_vcr_tab_type();

    type p_nbr_tab_type is table of number;
    ls_tab_orl_ref_rgp p_vcr_tab_type := p_vcr_tab_type();
    li_tab_grp_orig p_nbr_tab_type := p_nbr_tab_type();
    li_tab_grp_rgp p_nbr_tab_type := p_nbr_tab_type();
    li_tab_pal_nb_col_orig p_nbr_tab_type := p_nbr_tab_type();

    lb_nouv boolean := False;

    li_indice number := 0;
    li_i number;
    li_j number;
    li_k number;
    li_max_grp_rgp number := 0;

    ls_pal_code_tmp varchar2(50);
    ls_tab_pal_code p_vcr_tab_type := p_vcr_tab_type();
    ld_vte_pu_tmp number;
    ld_tab_vte_pu p_nbr_tab_type := p_nbr_tab_type();
    ls_vte_bta_code_tmp varchar2(50);
    ls_tab_vte_bta_code p_vcr_tab_type := p_vcr_tab_type();
    ld_ach_pu_tmp number;
    ld_tab_ach_pu p_nbr_tab_type := p_nbr_tab_type();
    ls_ach_bta_code_tmp varchar2(50);
    ls_tab_ach_bta_code p_vcr_tab_type := p_vcr_tab_type();
    ls_ach_dev_pu_tmp number;
    ls_tab_ach_dev_pu p_nbr_tab_type := p_nbr_tab_type();
    ls_ach_dev_code_tmp varchar2(50);
    ls_tab_ach_dev_code p_vcr_tab_type := p_vcr_tab_type();
    ls_ach_dev_taux_tmp number;
    ls_tab_ach_dev_taux p_nbr_tab_type := p_nbr_tab_type();

    ldc_trp_pu_tmp number;
    ldc_trp_dev_pu_tmp number;
    ldc_trp_dev_taux_tmp number;
    ls_trp_dev_code_tmp varchar2(50);
    ls_trp_bta_code_tmp varchar2(50);

    ls_lib_dlv_tmp varchar2(50);
    ls_tab_lib_dlv p_vcr_tab_type := p_vcr_tab_type();
    li_cde_nb_pal_tmp number;
    li_cde_nb_pal_orig number;
    li_orl_lig_sav number;
    ls_orl_lig_sav GEO_ORDLIG.ORL_LIG%type;
    li_cde_nb_col_sav number;
    ls_bac_code_tmp varchar2(50);
    ls_tab_bac_code p_vcr_tab_type := p_vcr_tab_type();

    ldc_cde_pds_brut_tmp number;
    ldc_cde_pds_nett_tmp number;
    ls_var_ristourne_tmp varchar2(50);
    ls_tab_var_ristourne p_vcr_tab_type := p_vcr_tab_type();
    ldc_frais_pu_tmp number;
    ldc_tab_frais_pu p_nbr_tab_type := p_nbr_tab_type();
    ls_esp_code_tmp varchar2(50);
    ls_tab_esp_code p_vcr_tab_type := p_vcr_tab_type();
    ls_propr_code_tmp varchar2(50);
    ls_tab_propr_code p_vcr_tab_type := p_vcr_tab_type();
    ls_frais_desc_tmp varchar2(50);
    ls_tab_frais_desc p_vcr_tab_type := p_vcr_tab_type();
    ls_orl_ref_rgp_tmp varchar2(50);
    li_pal_nb_inter_tmp number;
    li_tab_pal_nb_inter p_nbr_tab_type := p_nbr_tab_type();

    li_ret number;
    ls_cen_ref_rgp varchar2(50);
    ls_soc_code_old varchar2(50);

    ls_ttr_code varchar2(50);
    ls_instructions_logistique varchar2(500);
    ls_list_nordre_orig varchar2(500);
    ls_code_chargement_complet varchar2(500);

    ls_nordre_orig varchar2(50);
    ls_etd_location varchar2(50);
    ls_eta_location varchar2(50);

    ls_col_code varchar2(50);
    ll_article_mode_culture number;
    ls_ori_code varchar2(50);
    ld_prix_mini number;
    ls_ind_exp varchar2(50);

    ls_list_propr_code clob;
    ls_tab_propr_code_2 p_str_tab_type := p_str_tab_type();
    ls_tab_null p_vcr_tab_type := p_vcr_tab_type();
    ll_nb_prop number;

    ls_frais_unite varchar2(50);

    ld_marg_pu number;
    ld_rist_pu_sf number;
    ld_rist_pu_hf number;
    ld_rist_pu number;
    ld_trp_pu number;
    ld_pds_pu number;
    ld_pdnet_colis number;
    ld_trp_pu_entrep number;
    ld_frais_plat_pu number;
    ld_frais_plat_client number;
    ld_mark_pu number;
    ld_mark_client number;
    ld_doua_client number;
    ld_doua_pu number;
    ld_doua_pu_tot number;
    ld_crt_client number;
    ld_crt_pu number;
    ld_ach_pu number;
    ld_vte_pu number;
    ld_dev_tx_ordre_orig number;
    ls_crt_bta_client varchar2(50);
    ld_fra_pu number;
    ld_fra_tot number;



    ls_version varchar2(50);

    ls_decl_doua varchar2(50) := '';


    ll_nb_cde_nb_pal_tot_rgp number;
    ld_doua_pu_tot_rgp number;

    ls_cen_code_rgp varchar2(50);

    ld_frais_plateforme number;

    ls_tvt_code varchar2(50);
    ls_sco_code varchar2(50);
    ls_cat_code varchar2(50);
    ls_var_code varchar2(50);

    ls_perequation varchar2(50);
    ld_accompte number;
    ld_frais_pu_mark number;
    ll_k_frais number;
    ll_mode_culture_mark number;
    ls_orl_ref_mark varchar2(50);
    ls_cat_code_mark varchar2(50);
    ls_ori_code_mark varchar2(50);
    ls_var_code_mark varchar2(50);
    ls_tvt_code_mark varchar2(50);
    ls_sco_code_mark varchar2(50);
    ls_col_code_mark varchar2(50);
    ls_frais_unite_mark varchar2(50);
    ls_ind_grossiste varchar2(50) :='N';
    ls_ccw_code varchar2(50);
    ls_ccw_code_mark varchar2(50);

    ls_soc_code_detail varchar2(50);

    ls_fou_code_old varchar2(50);
    li_grp_rgp_old number;

    ldc_ctl_tx_marge number;
    ldc_rem_sf_tx number;
    ldc_rem_hf_tx number;


    li_cde_nb_pal_tot number := 0;
    li_cde_nb_col_tot number := 0;

    li_cde_pdnet_tot_orig number := 0;
    li_cde_nb_col_tot_orig number := 0;


    ib_buk boolean := FAlSE;

    ll_max_nb_col_orig number;
    ll_count_nb_col_orig number;
    ll_tab_max_nb_col_orig p_nbr_tab_type := p_nbr_tab_type();
    ll_tab_null p_nbr_tab_type := p_nbr_tab_type();

    li_num_version_uk number;
    li_num_version_uk_old number;

    ld_doua_pu_tot_new number;
    ld_doua_pu_tot_new_dev number;
    ls_doua_dev_code  varchar2(3);
    ld_doua_dev_tx number;

    ld_doua_pu_tot_new_dedexp number;
    ld_doua_pu_tot_new_dev_dedexp number;

    ld_doua_pu_tot_new_dev_gmvs number;
    ld_doua_pu_tot_new_gmvs number;


    cursor load_geo_societe(a_soc_code varchar2) is
        select
            soc_code, raisoc, ads1,
            ads2, ads3, zip,
            ville, pay_code, lan_code,
            dev_code, tvaid, tvr_code,
            tel_num, fax_num, email_ads,
            siret, codape, nature,
            soc_rcs, deb_contact, cpte_tva_col,
            cpte_ctifl, taux_ctifl, cpte_tva_1,
            cpte_tva_2, cam_code, cam_code_old,
            cnuf, lf_ean,
            deb_niveau_obligation, cpte_interfel_seul,
            taux_interfel_seul, cpte_ctifl_seul, taux_ctifl_seul,
            deb_id, valide, art_soc
        from geo_societe
        where soc_code = a_soc_code;

BEGIN
    res := 0;
    msg := '';

    begin
        select C.CEN_REF_RGP into ls_cen_ref_rgp
        from GEO_CLIENT C, GEO_ORDRE O
        where O.ORD_REF =arg_ord_ref_origine and
                O.CLI_REF =    C.CLI_REf;
    exception when no_data_found then
        ls_cen_ref_rgp := null;
    end;

    /* Frais marge + ristourne */
    select O_CL.TAUX_MARGE,O_CL.rem_sf_tx,O_CL.rem_hf_tx into ldc_ctl_tx_marge, ldc_rem_sf_tx,ldc_rem_hf_tx
    from GEO_ORDRE O_OR, GEO_CLIENT O_CL
    where   O_OR.ORD_REF =arg_ord_ref_origine and
           O_OR.CLI_REF =  O_CL.CLI_REf;

    IF ldc_ctl_tx_marge IS NULL THEN
            res := 0;
            msg := 'Aucun taux marge défini pour le client (TAUX_MARGE), arrêt';
            return;
    END IF;

    IF ldc_rem_sf_tx IS NULL THEN
            res := 0;
            msg := 'Aucune remise sur facture définie pour le client (rem_sf_tx), arrêt';
            return;
    END IF;

    IF ldc_rem_hf_tx IS NULL THEN
            res := 0;
            msg := 'Aucune remise hors facture définie pour le client (rem_hf_tx), arrêt';
            return;
    END IF;

    If ls_cen_ref_rgp is null or ls_cen_ref_rgp ='' Then
        -- li_ret = messagebox("Question","Aucun entrepot associé au client BW UK, voulez-vous prendre l'entrepôt générique BWUK ?",Question!,YesNo!)
        If arg_entrepot_generic = 'N' Then
            res := 0;
            msg := 'Aucun entrepot associé au client BW UK, arrêt';
            return;
        ELSe
            select distinct E.CEN_REF into ls_cen_ref_rgp
            from GEO_CLIENT C, GEO_ENTREP E
            where C.SOC_CODE  ='SA' and
                    C.CLI_CODE ='BWUK' and
                    C.CLI_REF = E.CLI_REF and
                    E.CEN_CODE ='BWUK';

        End If;
    ELSE
        select  GEO_ENTREP.CEN_CODE into  ls_cen_code
        FROM  GEO_ENTREP
        where GEO_ENTREP.CEN_REF =ls_cen_ref_rgp;

        if ls_cen_code = 'GROSSISTE' Then
            ls_ind_grossiste := 'O';
        End IF;

    End If;

    select substr(code_chargement,1,6),depdatp,to_char(GEO_ORDRE.DEPDATP,'dd/mm/yy hh24:mi'),trp_code,per_codecom,per_codeass,ttr_code,instructions_logistique,code_chargement,livdatp,etd_location,eta_location,etd_date,etd_date,dev_code,dev_tx
    into ls_code_chargement,ldt_depdatp,ls_depdatp,ls_transp,ls_per_codecom,ls_per_codeass,ls_ttr_code,ls_instructions_logistique,ls_code_chargement_complet,ldt_livdatp,ls_etd_location,ls_eta_location,ldt_etd_date,ldt_eta_date,ls_dev_code_orig,ld_dev_tx_ordre_orig
    from GEO_ORDRE
    where ORD_REF =arg_ord_ref_origine;

    If ls_code_chargement is null OR ls_code_chargement='' Then
        msg := msg || ' Aucun code chargement saisi, le transfert est impossible';
        res := 0;
        return;
    ELSE
        ls_code_chargement := ls_code_chargement || '%';
    End If;

    begin
        select TRS_CODE into ls_decl_doua  from GEO_TRANSI T
        where  ls_transp like T.TRS_CODE||'%'  and
                    T.IND_DECL_DOUANIER ='O';
        --Demande de SQ le 31/10/2022
        --Si transporteur PELLIET alors déclaration de douane effectué par LGLCUSTOMS
        if ls_transp = 'PELLIET' OR ls_transp ='LGLINTER' then ls_decl_doua := 'LGLCUSTOMS'; end if;

    exception when no_data_found then
     if ls_transp = 'PELLIET' OR ls_transp ='LGLINTER' then
        ls_decl_doua := 'LGLCUSTOMS';
     else
        ls_decl_doua := 'BOLLORE';
     end if;
    end;

    ls_ord_ref_regroup := null;

    begin
        select ORD_REF into ls_ord_ref_regroup
        from  GEO_ORDRE
        where     CODE_CHARGEMENT         like ls_code_chargement and
                to_date(DEPDATP)                          =to_date(ldt_depdatp) and
                VALIDE ='O'                         and
                CEN_REF  = ls_cen_ref_rgp and
                rownum = 1;
    exception when no_data_found then
        ls_ord_ref_regroup := null;
    end;

    ls_soc_code_old := arg_soc_code;
    -- arg_soc_code := 'SA';
    for geo_soc in load_geo_societe('SA') loop

        If ls_ord_ref_regroup is null Then

            select   GEO_CLIENT.CLI_CODE, GEO_ENTREP.CEN_CODE,TRP_PU,'EUR',TRP_BTA_CODE
            into ls_cli_code, ls_cen_code,ldc_trp_dev_pu_tmp,ls_trp_dev_code_tmp,ls_trp_bta_code_tmp
            FROM GEO_CLIENT, GEO_ENTREP
            where  GEO_CLIENT.SOC_CODE  ='SA' and
                    GEO_CLIENT.CLI_REF = GEO_ENTREP.CLI_REF  and
                    GEO_ENTREP.CEN_REF =ls_cen_ref_rgp;


            If ls_cen_code = 'GROSSISTE' Then
                ld_frais_plateforme :=0.02;
                ls_ind_grossiste := 'O';
            Else
                ld_frais_plateforme :=0;
                ls_ind_grossiste :='N';
            End  IF;

            /* creer un ordre de regroupement*/
            f_create_ordre_v2('SA', ls_cli_code, ls_cen_code, ls_transp,'' , false, false, ls_depdatp,'RGP', res, msg, ls_ord_ref_regroup);
            if res <> 1 then
                return;
            end if;


            If ls_trp_dev_code_tmp = geo_soc.dev_code  Then
                    ldc_trp_dev_taux_tmp := 1;
                    ldc_trp_pu_tmp := ldc_trp_dev_pu_tmp;
            Else
                select dev_tx into ldc_trp_dev_taux_tmp
                from geo_devise_ref
                where dev_code = ls_trp_dev_code_tmp and
                        dev_code_ref =geo_soc.dev_code;

                ldc_trp_pu_tmp := ldc_trp_dev_taux_tmp * ldc_trp_dev_pu_tmp;
            End IF;

            If ls_dev_code_orig = geo_soc.dev_code  Then
                ldc_dev_taux_rgp := 1;
            ELSe
                select dev_tx into ldc_dev_taux_rgp
                from geo_devise_ref
                where dev_code = ls_dev_code_orig and
                        dev_code_ref =geo_soc.dev_code;
            END IF;





            update GEO_ORDRE
            SET TRP_DEV_PU =ldc_trp_dev_pu_tmp,
                TRP_PU =ldc_trp_pu_tmp,
                TRP_DEV_CODE=ls_trp_dev_code_tmp,
                TRP_DEV_TAUX=ldc_trp_dev_taux_tmp,
                TRP_BTA_CODE=ls_trp_bta_code_tmp,
                TTR_CODE = ls_ttr_code,
                INSTRUCTIONS_LOGISTIQUE =ls_instructions_logistique,
                PER_CODECOM =ls_per_codecom,
                PER_CODEASS =ls_per_codeass,
                CODE_CHARGEMENT =ls_code_chargement_complet,
                LIVDATP =ldt_livdatp,
                etd_location =ls_etd_location,
                eta_location =ls_eta_location,
                etd_date =ldt_etd_date,
                eta_date =ldt_eta_date,
                dev_code =ls_dev_code_orig,
                dev_tx =ldc_dev_taux_rgp,
                frais_plateforme  =ld_frais_plateforme
            where ORD_REF =ls_ord_ref_regroup;

            lb_nouv := TRUE;

        Else
            begin
                select  distinct 'KO',O.LIST_NORDRE_ORIG into ls_version,ls_list_nordre_orig
                from GEO_GEST_REGROUP R , GEO_ORDRE O
                where R.ORD_REF_RGP =ls_ord_ref_regroup and
                        R.NUM_VERSION IS NULL and
                        O.ORD_REF =ls_ord_ref_regroup;
            exception when no_data_found then
                null;
            end;

            If ls_version ='KO' Then
                msg := msg || ' Rénitialisation de l''ordre de REGROUPEMENT. Veuillez redupliquer le(s) ordre(s) ' || ls_list_nordre_orig || ' car il(s) n''étai(t)(ent) pas compatible(s)';
                Delete from GEO_ORDLIG where ORD_REF=ls_ord_ref_regroup;

            End If;

        End If;

        delete GEO_GEST_REGROUP
        where ORD_REF_RGP  <> ls_ord_ref_regroup and
                ORD_REF_ORIG = arg_ord_ref_origine;

        insert into GEO_GEST_REGROUP( ORD_REF_RGP,ORD_REF_ORIG,ORL_REF_ORIG,ART_REF_ORIG)
        select ls_ord_ref_regroup,arg_ord_ref_origine,ORL_REF,ART_REF
        from GEO_ORDLIG
        where GEO_ORDLIG.ORD_REF =arg_ord_ref_origine and
                not exists (select 1
                                from GEO_GEST_REGROUP
                                where     GEO_GEST_REGROUP.ORD_REF_RGP =ls_ord_ref_regroup and
                                            ORD_REF_ORIG = GEO_ORDLIG.ORD_REF and
                                            ORL_REF_ORIG =  GEO_ORDLIG.ORL_REF );


        IF     lb_nouv = FALSE Then

            delete GEO_GEST_REGROUP
            where exists (select 1 from GEO_ORDLIG_delete OD
                                    where  OD.ORD_REF = arg_ord_ref_origine and
                                            GEO_GEST_REGROUP.ORD_REF_RGP = ls_ord_ref_regroup and
                                            GEO_GEST_REGROUP.ORD_REF_ORIG = OD.ORD_REF and
                                            GEO_GEST_REGROUP.ORL_REF_ORIG = OD.ORL_REF);
           begin
            UPDATE GEO_GEST_REGROUP
            SET SOC_CODE_DETAIL ='BUK'
            where ORD_REF_RGP = ls_ord_ref_regroup and
                    exists(select 1
                         FROM GEO_GEST_REGROUP G1
                         where GEO_GEST_REGROUP.ORD_REF_RGP = G1.ORD_REF_RGP  and
                               GEO_GEST_REGROUP.FOU_CODE_ORIG = G1.FOU_CODE_ORIG and
                                exists (
                                select 1
                                from GEO_ORDLIG  L
                                where  G1.ORL_REF_ORIG = L.ORL_REF and
                                           G1.FOU_CODE_ORIG = L.FOU_CODE and
                                            L.CDE_NB_COL <  L.PAL_NB_COL));
              exception when others then
          null;
            end;

        End IF;

        declare
            cursor C_ordlig is
            SELECT
                GEO_ORDLIG.ORL_LIG,GEO_ORDLIG.ORL_REF,GEO_ORDLIG.CDE_NB_COL,GEO_ORDLIG.ART_REF,GEO_ORDLIG.FOU_CODE,GEO_ORDLIG.PAL_NB_COL,GET_FLUX(GEO_ORDLIG.ORD_REF, GEO_ORDLIG.FOU_CODE) "SOC_CODE_DETAIL",GEO_FOURNI.NUM_VERSION_UK
            FROM
                GEO_ORDLIG, GEO_FOURNI
            WHERE
                GEO_ORDLIG.ORD_REF =arg_ord_ref_origine and
                GEO_ORDLIG.FOU_CODE = GEO_FOURNI.FOU_CODE
            ORDER BY
                ORL_LIG ASC   ;
        begin

            for l in C_ordlig loop

                If l.SOC_CODE_DETAIL ='BUK' then
                    UPDATE GEO_GEST_REGROUP
                    SET SOC_CODE_DETAIL ='BUK'
                    where ORD_REF_RGP = ls_ord_ref_regroup and
                          FOU_CODE_ORIG= l.fou_code;

                ENd If;
                li_num_version_uk := l.NUM_VERSION_UK;
                If l.CDE_NB_COL is not null and l.CDE_NB_COL >0 Then
                    li_grp_ori := li_grp_ori + 1;
                    li_pal_nb_col_sav := l.PAL_NB_COL;
                    li_cde_nb_col_sav := l.CDE_NB_COL;

                        -- Bruno  le 12/05/22
                    -- rendre compatible le nouveau et ancien monde
                    li_num_version_uk_old := null;
                    begin
                        select NUM_VERSION INTO li_num_version_uk_old
                        FROM GEO_GEST_REGROUP
                        where ORD_REF_RGP = ls_ord_ref_regroup and
                                FOU_CODE_ORIG =l.fou_code;
                    exception when others then
                        li_num_version_uk_old := null;
                    end;

                    If li_num_version_uk_old is not null and li_num_version_uk_old <> li_num_version_uk then
                        li_num_version_uk := li_num_version_uk_old;
                    End If;

                    If li_num_version_uk > 1 Then
                        select max(GRP_RGP) into li_grp_rgp
                        FROM GEO_GEST_REGROUP
                        where  GEO_GEST_REGROUP.ORD_REF_RGP = ls_ord_ref_regroup;

                        If li_grp_rgp is null  Then
                            li_grp_rgp:= 1;
                        Else
                            li_grp_rgp := li_grp_rgp + 1;
                        End If;

                    End If;

                End IF;

                IF li_num_version_uk = 1 Then
                    If     lb_nouv  = TRUE Then
                        li_grp_rgp := li_grp_ori;
                    Else
                        li_grp_rgp := null;
                    End If;
                End IF;

                update GEO_GEST_REGROUP
                set     GRP_ORIG = li_grp_ori,
                        GRP_RGP = li_grp_rgp,
                        ART_REF_ORIG= l.art_ref,
                        FOU_CODE_ORIG= l.fou_code,
                        PAL_NB_COL_ORIG = li_pal_nb_col_sav,
                        SOC_CODE_DETAIL = l.SOC_CODE_DETAIL,
                        NB_COL_ORIG = li_cde_nb_col_sav,
                        NUM_VERSION = li_num_version_uk
                where
                        GEO_GEST_REGROUP.ORD_REF_RGP = ls_ord_ref_regroup and
                        GEO_GEST_REGROUP.ORD_REF_ORIG =  arg_ord_ref_origine and
                        GEO_GEST_REGROUP.ORL_REF_ORIG = l.orl_ref;

            end LOOP;

        end;




        /* Calcul des frais */
        select C.FRAIS_PLATEFORME,
                C.FRAIS_PU,
                C.CRT_PU,
                C.CRT_BTA_CODE
        into ld_frais_plat_client,ld_mark_client,ld_crt_client,ls_crt_bta_client
        from  GEO_CLIENT C,  GEO_ORDRE O
        where O.ORD_REF =arg_ord_ref_origine and
                O.CLi_REF = C.CLI_REF;

        /*Calcul des frais de douane*/
        delete  GEO_ORDFRA where ORD_REF =arg_ord_ref_origine and FRA_CODE ='DEDIMP';

        select sum(CDE_NB_PAL) into li_cde_nb_pal_orig from GEO_ORDLIG where ORD_REF =arg_ord_ref_origine;

        begin
        select (GEO_FRAIS_TYP.ACH_DEV_PU /26)* li_cde_nb_pal_orig,GEO_FRAIS_TYP.ACH_DEV_CODE,GEO_DEVISE_REF.DEV_TX,(GEO_FRAIS_TYP.ACH_DEV_PU /26)* li_cde_nb_pal_orig*GEO_DEVISE_REF.DEV_TX
        into ld_doua_pu_tot_new_dev, ls_doua_dev_code,ld_doua_dev_tx,ld_doua_pu_tot_new
        from GEO_FRAIS_TYP,GEO_DEVISE_REF
        where FRA_CODE ='DEDIMP' and
              TYT_CODE = 'S' and
              FRA_TIERS_CODE = ls_decl_doua and
              GEO_DEVISE_REF.DEV_CODE_REF = 'GBP' and
              GEO_DEVISE_REF.DEV_CODE = GEO_FRAIS_TYP.ACH_DEV_CODE;

        insert INTO GEO_ORDFRA
            (ORD_REF,FRA_CODE,ACH_DEV_PU,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS,ACH_QTE,ACH_PU,MONTANT_TOT)
            VALUES (arg_ord_ref_origine,'DEDIMP',ld_doua_pu_tot_new_dev,ld_doua_pu_tot_new_dev,ls_doua_dev_code,ld_doua_dev_tx,ls_decl_doua,1,ld_doua_pu_tot_new,ld_doua_pu_tot_new);

        exception when no_data_found then
            insert INTO GEO_ORDFRA
            (ORD_REF,FRA_CODE,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS) VALUES (arg_ord_ref_origine,'DEDIMP',ld_doua_pu_tot,'GBP',1,ls_decl_doua);
        end;




        begin
            select     E.TRP_PU
            into         ld_trp_pu_entrep
            from  GEO_ENTREP E,  GEO_ORDRE O
            where O.ORD_REF =arg_ord_ref_origine and
                    O.CEN_REF =E.CEN_REF and
                    E.TRP_BTA_CODE ='KILO';
        exception when no_data_found then
            ld_trp_pu_entrep :=0;
        end;

        /* Frais marge + ristourne */
        declare
            /*cursor C_marge_pu is
                select OL_OR.ORL_REF,OL_OR.VTE_PU*O_CL.TAUX_MARGE as marge_pu,OL_OR.VTE_PU*O_CL.rem_sf_tx/100 as rist_sf,OL_OR.VTE_PU*O_CL.rem_hf_tx/100 as rist_hf
                from GEO_ORDRE O_OR, GEO_ORDLIG OL_OR, GEO_CLIENT O_CL
                where   O_OR.ORD_REF =arg_ord_ref_origine and
                            O_OR.ORD_REF = OL_OR.ORD_REF and
                        O_OR.CLI_REF =  O_CL.CLI_REf;*/
                cursor C_marge_pu is
                select OL_OR.ORL_REF,OL_OR.VTE_PU*O_CL.TAUX_MARGE*D.DEV_TX as marge_pu,OL_OR.VTE_PU*D.DEV_TX*O_CL.rem_sf_tx/100 as rist_sf,OL_OR.VTE_PU*D.DEV_TX*O_CL.rem_hf_tx/100 as rist_hf
                from GEO_ORDRE O_OR, GEO_ORDLIG OL_OR, GEO_CLIENT O_CL, GEO_DEVISE_REF D
                where   O_OR.ORD_REF =arg_ord_ref_origine and
                            O_OR.ORD_REF = OL_OR.ORD_REF  and
                            O_OR.CLI_REF =  O_CL.CLI_REf  and
                            D.DEV_CODE_REF = 'GBP'           and
                            D.DEV_CODE = O_OR.DEV_CODE;


        begin
            for mp in C_marge_pu loop
                ld_marg_pu := mp.marge_pu;
                ld_rist_pu_sf := mp.rist_sf;
                ld_rist_pu_hf := mp.rist_hf;
                if ld_rist_pu_sf is null then
                    ld_rist_pu_sf := 0;
                end if;
                If ld_rist_pu_hf is null then
                    ld_rist_pu_hf := 0;
                end if;

                ld_rist_pu := ld_rist_pu_sf + ld_rist_pu_hf;

                update   GEO_ORDLIG set MARG_PU = ld_marg_pu ,RIST_PU = ld_rist_pu
                    where   ORD_REF =arg_ord_ref_origine and
                            ORL_REF = mp.orl_ref;

            end loop;
        end;


        /* Frais pordre + */
        begin
        select sum(MONTANT_TOT) into ld_fra_tot
        from GEO_ORDFRA
        where ORD_REF =arg_ord_ref_origine;
        exception when no_data_found then
            ld_fra_tot :=0;
        end;

        IF ld_fra_tot is NULL  THEN
            ld_fra_tot :=0;
        END IF;

        IF ld_doua_pu_tot_new is NULL  THEN
            ld_doua_pu_tot_new :=0;
        END IF;

         declare
            cursor  C_maj_pdnet is
                select distinct R.GRP_ORIG, R.ART_REF_ORIG, A.COL_PDNET
                from GEO_GEST_REGROUP R, GEO_ARTICLE A
                where   R.ORD_REF_ORIG = arg_ord_ref_origine and
                            R.ART_REF_ORIG = A.ART_REF;

        begin
            for cmajpdnet in C_maj_pdnet loop
            update GEO_GEST_REGROUP
            set COL_PDNET = cmajpdnet.COL_PDNET
            where ORD_REF_ORIG   = arg_ord_ref_origine and
                     ART_REF_ORIG   = cmajpdnet.ART_REF_ORIG and
                     GRP_ORIG        =   cmajpdnet.GRP_ORIG;
            end loop;
        end;

        declare
        cursor  C_maj_pdnet_moy is
        select GRP_ORIG,round(AVG(COL_PDNET),2) "COL_PDNET_MOY"
        from GEO_GEST_REGROUP
        where ORD_REF_ORIG   = arg_ord_ref_origine
        group by GRP_ORIG;
        begin
            for cmajpdnetmoy in C_maj_pdnet_moy loop
            update GEO_GEST_REGROUP
            set COL_PDNET_MOY = cmajpdnetmoy.COL_PDNET_MOY
            where ORD_REF_ORIG   = arg_ord_ref_origine and
                  GRP_ORIG         =   cmajpdnetmoy.GRP_ORIG;
            end loop;
        end;



        declare
            /*cursor C_info_pu is
                select  OL.VTE_PU,MARG_PU ,RIST_PU,OL.VTE_BTA_CODE, OL.ORL_REF, A.COL_PDNET,CDE_NB_PAL,GRP_ORIG,OL.PAL_NB_COL,CDE_NB_COL
                from GEO_ARTICLE_COLIS A,GEO_ORDLIG OL,GEO_GEST_REGROUP R
                where OL.ORD_REF = arg_ord_ref_origine and
                        R.ORD_REF_RGP = ls_ord_ref_regroup and
                        R.ORD_REF_ORIG = arg_ord_ref_origine and
                        R.ORL_REF_ORIG = OL.ORL_REF and
                        OL.ART_REF = A.ART_REF and
                       OL.CDE_NB_COL > 0;*/

            cursor C_info_pu is
                select  OL.VTE_PU*D.DEV_TX "VTE_PU",MARG_PU ,RIST_PU,OL.VTE_BTA_CODE, OL.ORL_REF, R.COL_PDNET_MOY,CDE_NB_PAL,GRP_ORIG,OL.PAL_NB_COL,CDE_NB_COL
                from GEO_ARTICLE_COLIS A,GEO_ORDLIG OL,GEO_GEST_REGROUP R,GEO_DEVISE_REF D,GEO_ORDRE O_OR
                where OL.ORD_REF = arg_ord_ref_origine and
                        R.ORD_REF_RGP = ls_ord_ref_regroup and
                        R.ORD_REF_ORIG = arg_ord_ref_origine and
                        R.ORL_REF_ORIG = OL.ORL_REF and
                        OL.ART_REF = A.ART_REF and
                        OL.CDE_NB_COL > 0 and
                        O_OR.ORD_REF = R.ORD_REF_ORIG and
                        D.DEV_CODE_REF = 'GBP' and
                        D.DEV_CODE = O_OR.DEV_CODE;

            ld_doua_pu_tot number := 0;
        begin


            select SUM(COL_PDNET_MOY),sum(CDE_NB_COL)   into li_cde_pdnet_tot_orig ,li_cde_nb_col_tot_orig
            from GEO_ORDLIG OL,GEO_GEST_REGROUP R
                where OL.ORD_REF = arg_ord_ref_origine and
                        R.ORD_REF_ORIG = arg_ord_ref_origine and
                        R.ORL_REF_ORIG = OL.ORL_REF and
                        OL.CDE_NB_COL > 0;



            li_cde_nb_pal_orig :=0;
            for ipu in C_info_pu loop
                ld_vte_pu := ipu.VTE_PU;
                ld_marg_pu := ipu.MARG_PU;
                ld_rist_pu := ipu.RIST_PU;
                ls_vte_bta_code_tmp := ipu.VTE_BTA_CODE;
                ls_orl_ref_tmp := ipu.orl_ref;
                ld_pdnet_colis := ipu.col_pdnet_moy;
                li_cde_nb_pal_tmp := ipu.cde_nb_pal;
                li_grp_ori := ipu.grp_orig;
                li_pal_nb_col_tmp := ipu.PAL_NB_COL;
                li_cde_nb_col_tmp := ipu.cde_nb_col;

                li_cde_nb_pal_orig := li_cde_nb_pal_orig + li_cde_nb_pal_tmp;
                ld_doua_pu_tot := ld_doua_pu_tot + (li_cde_nb_pal_tmp *2);
                case ls_vte_bta_code_tmp
                    when 'KILO' then
                        ld_pds_pu := 1;
                        If (li_cde_pdnet_tot_orig) > 0 Then
                            ld_doua_pu :=ld_doua_pu_tot_new / li_cde_pdnet_tot_orig;

                            ld_fra_pu := ld_fra_tot  / li_cde_pdnet_tot_orig;
                        Else
                            ld_doua_pu := 0;
                            ld_fra_pu :=0;
                        End IF;
                    when 'COLIS' then
                        ld_pds_pu:=ld_pdnet_colis;
                        If li_cde_nb_col_tot_orig > 0 Then
                            ld_doua_pu := ld_doua_pu_tot_new/li_cde_nb_col_tot_orig  ;

                             ld_fra_pu := ld_fra_tot/li_cde_nb_col_tot_orig;
                        ELse
                            ld_doua_pu := 0;

                            ld_fra_pu :=0;
                        End IF;
                end  case;

                ld_trp_pu             := ld_pds_pu*ld_trp_pu_entrep;
                ld_frais_plat_pu     := ld_pds_pu*ld_frais_plat_client;
                ld_mark_pu             := ld_pds_pu*ld_mark_client;
                If ls_crt_bta_client ='KILO' THEN
                    ld_crt_pu := ld_crt_client *ld_pds_pu;
                End If;
                If ld_crt_pu is null then ld_crt_pu := 0; end if;
                If ld_rist_pu is null then ld_rist_pu := 0; end if;
                If ld_trp_pu is null then ld_trp_pu := 0; end if;
                If ld_frais_plat_pu is null then ld_frais_plat_pu := 0; end if;
                If ld_mark_pu is null then ld_mark_pu := 0; end if;
                If ld_crt_pu is null then ld_crt_pu := 0; end if;

                ld_ach_pu := ld_vte_pu - ipu.marg_pu  - ld_rist_pu -ld_trp_pu -ld_frais_plat_pu -ld_fra_pu -ld_mark_pu -ld_crt_pu;
                If ld_ach_pu < 0 then ld_ach_pu := ld_vte_pu; end if;

                If ls_dev_code_orig = 'GBP' Then
                    ls_ach_dev_taux_tmp := 1;
                    ls_ach_dev_pu_tmp := ld_ach_pu;
                    ld_ach_pu_tmp := ld_ach_pu;
                Else

                    ls_ach_dev_taux_tmp := ld_dev_tx_ordre_orig;
                    ld_ach_pu_tmp := ld_ach_pu;
                    ls_ach_dev_pu_tmp := ld_ach_pu / ls_ach_dev_taux_tmp;
                End If;

                update GEO_ORDLIG set ACH_DEV_PU = ls_ach_dev_pu_tmp,
                                                ACH_PU  = ld_ach_pu_tmp,
                                                ACH_BTA_CODE = ls_vte_bta_code_tmp,
                                                ACH_DEV_CODE= ls_dev_code_orig,
                                                ACH_DEV_TAUX = ls_ach_dev_taux_tmp,
                                                MARG_PU = ipu.marg_pu,
                                                RIST_PU= ld_rist_pu,
                                                TRP_PU = ld_trp_pu,
                                                FRAIS_PLAT_PU = ld_frais_plat_pu,
                                                DOUA_PU = ld_doua_pu,
                                                PRES_COURT_PU= ld_crt_pu,
                                                MARK_PU = ld_mark_pu,
                                                FRA_PU =ld_fra_pu
                where ORD_REF =arg_ord_ref_origine and
                        exists (select 1
                                from GEO_GEST_REGROUP R
                                where  R.ORD_REF_RGP = ls_ord_ref_regroup and
                                            R.ORD_REF_ORIG =arg_ord_ref_origine and
                                            R.ORL_REF_ORIG = GEO_ORDLIG.ORL_REF and
                                            R.GRP_ORIG =li_grp_ori);



            end loop;
        end;

        declare
            CURSOR C_GEST_REGROUP is
                SELECT        GEO_GEST_REGROUP.ORD_REF_ORIG,
                            GEO_GEST_REGROUP.ORL_REF_ORIG,
                            GEO_GEST_REGROUP.ART_REF_ORIG,
                            GEO_GEST_REGROUP.FOU_CODE_ORIG,
                            GEO_GEST_REGROUP.PAL_NB_COL_ORIG,
                            GEO_GEST_REGROUP.GRP_ORIG,
                            GEO_GEST_REGROUP.GRP_RGP,
                            GEO_GEST_REGROUP.ORL_REF_RGP,
                            L.PROPR_CODE,
                            L.PAL_CODE,
                            --CASE WHEN (L.FOU_CODE ='STEFLEMANS' or 'O' =ls_ind_grossiste)  THEN NULL ELSE L.VTE_PU  END as VTE_PUD,
                            --CASE WHEN (L.FOU_CODE ='STEFLEMANS' or 'O' =ls_ind_grossiste)  THEN NULL ELSE L.VTE_BTA_CODE END as VTE_BTA_CODE,
                           --- CASE WHEN (L.FOU_CODE ='STEFLEMANS' or 'O' =ls_ind_grossiste)  THEN NULL ELSE L.ACH_PU END as ACH_PU,
                           ---CASE WHEN (L.FOU_CODE ='STEFLEMANS' or 'O' =ls_ind_grossiste)  THEN NULL ELSE L.ACH_BTA_CODE END as ACH_BTA_CODE,
                           --- CASE WHEN (L.FOU_CODE ='STEFLEMANS' or 'O' =ls_ind_grossiste)  THEN NULL ELSE L.ACH_DEV_PU END as ACH_DEV_PU,
                            NULL AS VTE_PUD,
                            NULL AS VTE_BTA_CODE,
                            NULL AS ACH_PU,
                            NULL AS ACH_BTA_CODE,
                            NULL AS ACH_DEV_PU,
                            L.ACH_DEV_CODE,
                            L.ACH_DEV_TAUX,
                            L.LIB_DLV,
                            L.BAC_CODE,
                            L.VAR_RISTOURNE,
                            L.FRAIS_PU,
                            L.ESP_CODE,
                            L.PAL_NB_PALINTER,
                            L.FRAIS_DESC
            FROM
                GEO_GEST_REGROUP,GEO_ORDLIG L
            WHERE
                GEO_GEST_REGROUP.ORD_REF_RGP =ls_ord_ref_regroup and
                GEO_GEST_REGROUP.ORD_REF_ORIG =L.ORD_REF and
                GEO_GEST_REGROUP.ORL_REF_ORIG =L.ORL_REF
            ORDER BY
                GEO_GEST_REGROUP.ART_REF_ORIG ASC   ;
        begin
            for gr in C_GEST_REGROUP loop

                li_indice := li_indice + 1;

                ls_ord_ref_orig_tmp := gr.ORD_REF_ORIG;
                ls_orl_ref_tmp := gr.ORL_REF_ORIG;
                ls_art_ref_tmp := gr.ART_REF_ORIG;
                ls_fou_code_tmp := gr.FOU_CODE_ORIG;
                li_pal_nb_col_tmp := gr.PAL_NB_COL_ORIG;
                li_grp_orig_tmp := gr.GRP_ORIG;
                li_grp_rgp_tmp := gr.GRP_RGP;
                ls_orl_ref_rgp_tmp := gr.ORL_REF_RGP;
                ls_propr_code_tmp := gr.PROPR_CODE;
                ls_pal_code_tmp := gr.PAL_CODE;
                ld_vte_pu_tmp := gr.VTE_PUD;
                ls_vte_bta_code_tmp := gr.VTE_BTA_CODE;
                ld_ach_pu_tmp := gr.ACH_PU;
                ls_ach_bta_code_tmp := gr.ACH_BTA_CODE;
                ls_ach_dev_pu_tmp := gr.ACH_DEV_PU;
                ls_ach_dev_code_tmp := gr.ACH_DEV_CODE;
                ls_ach_dev_taux_tmp := gr.ACH_DEV_TAUX;
                ls_lib_dlv_tmp := gr.LIB_DLV;
                ls_bac_code_tmp := gr.BAC_CODE;
                ls_var_ristourne_tmp := gr.VAR_RISTOURNE;
                ldc_frais_pu_tmp := gr.FRAIS_PU;
                ls_esp_code_tmp := gr.ESP_CODE;
                li_pal_nb_inter_tmp := gr.PAL_NB_PALINTER;
                ls_frais_desc_tmp := gr.FRAIS_DESC;

                ls_tab_ord_ref_orig.extend();
                ls_tab_ord_ref_orig(li_indice) := ls_ord_ref_orig_tmp;
                ls_tab_orl_ref_orig.extend();
                ls_tab_orl_ref_orig(li_indice) := ls_orl_ref_tmp;
                ls_tab_art_ref_orig.extend();
                ls_tab_art_ref_orig(li_indice) := ls_art_ref_tmp;
                ls_tab_fou_code_orig.extend();
                ls_tab_fou_code_orig(li_indice) := ls_fou_code_tmp;
                li_tab_grp_orig.extend();
                li_tab_grp_orig(li_indice) := li_grp_orig_tmp;
                li_tab_grp_rgp.extend();
                li_tab_grp_rgp(li_indice) := li_grp_rgp_tmp;
                li_tab_pal_nb_col_orig.extend();
                li_tab_pal_nb_col_orig(li_indice) := li_pal_nb_col_tmp;
                ls_tab_orl_ref_rgp.extend();
                ls_tab_orl_ref_rgp(li_indice) := ls_orl_ref_rgp_tmp;

                If ls_lib_dlv_tmp is null then ls_lib_dlv_tmp :=''; end if;
                ls_lib_dlv_tmp := trim(ls_lib_dlv_tmp);
                If ls_frais_desc_tmp is null then ls_frais_desc_tmp :=''; end if;
                ls_frais_desc_tmp := trim(ls_frais_desc_tmp);

                If ld_vte_pu_tmp is null Then ld_vte_pu_tmp := 0; end if;
                If ls_vte_bta_code_tmp is null Then ls_vte_bta_code_tmp :=''; end if;

                If ld_ach_pu_tmp is null  Then ld_ach_pu_tmp := 0; end if;
                If ls_ach_bta_code_tmp is null Then ls_ach_bta_code_tmp :=''; end if;

                ls_tab_propr_code.extend();
                ls_tab_propr_code(li_indice):='';
                ls_tab_pal_code.extend();
                ls_tab_pal_code(li_indice) := ls_pal_code_tmp;
                ld_tab_vte_pu.extend();
                ld_tab_vte_pu(li_indice):= ld_vte_pu_tmp;
                ls_tab_vte_bta_code.extend();
                ls_tab_vte_bta_code(li_indice):=ls_vte_bta_code_tmp;
                ls_tab_ach_bta_code.extend();
                ls_tab_ach_bta_code(li_indice)    :=ls_ach_bta_code_tmp;

                ls_tab_ach_dev_taux.extend();
                ls_tab_ach_dev_taux(li_indice)    :=ls_ach_dev_taux_tmp;
                ls_tab_lib_dlv.extend();
                ls_tab_lib_dlv(li_indice) :=ls_lib_dlv_tmp;
                ls_tab_bac_code.extend();
                ls_tab_bac_code(li_indice) :=ls_bac_code_tmp;
                ls_tab_var_ristourne.extend();
                ls_tab_var_ristourne(li_indice) :=ls_var_ristourne_tmp;
                ldc_tab_frais_pu.extend();
                ldc_tab_frais_pu(li_indice):=ldc_frais_pu_tmp;
                ls_tab_esp_code.extend();
                ls_tab_esp_code(li_indice):=    ls_esp_code_tmp;
                li_tab_pal_nb_inter.extend();
                li_tab_pal_nb_inter(li_indice):=li_pal_nb_inter_tmp;
                ls_tab_frais_desc.extend();
                ls_tab_frais_desc(li_indice):=ls_frais_desc_tmp;


                begin
                    select distinct A.col_code, A.mode_culture, A.cat_code, A.ori_code, A.var_code, O.tvt_code, O.sco_code, A.ccw_code
                    into ls_col_code, ll_article_mode_culture,ls_cat_code, ls_ori_code, ls_var_code, ls_tvt_code, ls_sco_code, ls_ccw_code
                    from geo_article_colis A,  geo_ordre O
                    where O.ord_ref = ls_ord_ref_orig_tmp and
                            A.art_ref  = gr.ART_REF_ORIG;

                    --ll_k_frais = f_recup_frais(ls_var_code, ls_cat_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code)
                    f_recup_frais(ls_var_code, ls_ccw_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code, ll_k_frais, msg);
                    select perequation, accompte
                    into ls_perequation, ld_accompte
                    from geo_attrib_frais
                    where k_frais = ll_k_frais;

                    if  ls_perequation =  'O' then
                        ld_prix_mini := ld_accompte;
                    end if;
                exception when no_data_found then
                    null; -- pass
                end;
            --FIN LLEF

                --If not(isnull(ld_prix_mini )) and ld_prix_mini > 0 and mid(ls_col_code,1,2) <>'CP'  and ll_article_mode_culture = 0 and ls_ori_code = 'F' then
                If ld_prix_mini is not null and ld_prix_mini > 0 and substr(ls_col_code,1,2) <>'CP'  then
                    ld_tab_ach_pu.extend();
                    ld_tab_ach_pu(li_indice) := ld_prix_mini;
                    ls_tab_ach_dev_pu.extend();
                    ls_tab_ach_dev_pu(li_indice) :=    ld_prix_mini;
                    ls_tab_ach_dev_code.extend();
                    ls_tab_ach_dev_code(li_indice) :='EUR';
                Else
                    ld_tab_ach_pu.extend();
                    ld_tab_ach_pu(li_indice):=ld_ach_pu_tmp;
                    ls_tab_ach_dev_pu.extend();
                    ls_tab_ach_dev_pu(li_indice) :=    ld_ach_pu_tmp;
                    ls_tab_ach_dev_code.extend();
                    ls_tab_ach_dev_code(li_indice)     :=ls_ach_dev_code_tmp;
                End If;


            end loop;
        end;

        for li_i in 1 .. li_indice loop
            If li_tab_grp_rgp(li_i) is null Then
                For li_j in 1 .. li_indice loop


                    If li_j <> li_i Then

                        If li_max_grp_rgp < li_tab_grp_rgp(li_j) Then
                            li_max_grp_rgp  := li_tab_grp_rgp(li_j);
                        End If;

                         If ((ls_tab_art_ref_orig(li_j) = ls_tab_art_ref_orig(li_i) and ls_tab_fou_code_orig(li_j) = ls_tab_fou_code_orig(li_i) and li_tab_grp_rgp(li_j) is not null and li_tab_pal_nb_col_orig(li_j) = li_tab_pal_nb_col_orig(li_i) and
                        ls_tab_pal_code(li_j) = ls_tab_pal_code(li_i) and ld_tab_vte_pu(li_j)= ld_tab_vte_pu(li_i) and (ls_tab_vte_bta_code(li_j)=ls_tab_vte_bta_code(li_i) OR (ls_tab_vte_bta_code(li_j) IS NULL and ls_tab_vte_bta_code(li_i) IS NULL))  and
                        ld_tab_ach_pu(li_j)=ld_tab_ach_pu(li_i) and (ls_tab_ach_bta_code(li_j) =ls_tab_ach_bta_code(li_i) OR (ls_tab_ach_bta_code(li_j) IS NULL AND ls_tab_ach_bta_code(li_i) IS NULL ))
                        and (ls_tab_ach_dev_pu(li_j) = ls_tab_ach_dev_pu(li_i) OR (ls_tab_ach_dev_pu(li_j) IS NULL AND ls_tab_ach_dev_pu(li_i) IS NULL))
                        and (ls_tab_ach_dev_code(li_j) = ls_tab_ach_dev_code(li_i) OR (ls_tab_ach_dev_code(li_j) IS NULL AND ls_tab_ach_dev_code(li_i) IS NULL))
                        and ls_tab_ach_dev_taux(li_j) =ls_tab_ach_dev_taux(li_i) and ((ls_tab_lib_dlv(li_j) = ls_tab_lib_dlv(li_i)) OR (ls_tab_lib_dlv(li_j) IS NULL AND ls_tab_lib_dlv(li_i)IS NULL)) and ls_tab_bac_code(li_j) =ls_tab_bac_code(li_i) and
                        ls_tab_esp_code(li_j)=ls_tab_esp_code(li_i) and li_tab_pal_nb_inter(li_j)=li_tab_pal_nb_inter(li_i) and ((ls_tab_frais_desc(li_j)=ls_tab_frais_desc(li_i)) OR (ls_tab_frais_desc(li_j) IS NULL OR ls_tab_frais_desc(li_i) IS NULL ) )
                        ) OR
                            ls_tab_ord_ref_orig(li_j) = ls_tab_ord_ref_orig(li_i) and ls_tab_fou_code_orig(li_j) = ls_tab_fou_code_orig(li_i) and li_tab_grp_orig(li_j) = li_tab_grp_orig(li_i) and li_tab_grp_rgp(li_j) is not null) Then
                            li_tab_grp_rgp(li_i) := li_tab_grp_rgp(li_j);
                        End IF;

                        If (ls_tab_art_ref_orig(li_j) = ls_tab_art_ref_orig(li_i) and ls_tab_fou_code_orig(li_j) = ls_tab_fou_code_orig(li_i) and li_tab_grp_rgp(li_j) is not null and ls_tab_orl_ref_rgp(li_i) is null and ls_tab_orl_ref_rgp(li_j) is not null and li_tab_pal_nb_col_orig(li_j) = li_tab_pal_nb_col_orig(li_i)and
                        ls_tab_pal_code(li_j) = ls_tab_pal_code(li_i) and ld_tab_vte_pu(li_j)= ld_tab_vte_pu(li_i) and
                        (ls_tab_vte_bta_code(li_j)=ls_tab_vte_bta_code(li_i) or (ls_tab_vte_bta_code(li_j)IS NULL and ls_tab_vte_bta_code(li_i) IS NULL))  and
                        ld_tab_ach_pu(li_j)=ld_tab_ach_pu(li_i)
                        and (ls_tab_ach_bta_code(li_j) =ls_tab_ach_bta_code(li_i) OR (ls_tab_ach_bta_code(li_j) IS NULL AND ls_tab_ach_bta_code(li_i) IS NULL) )
                        and (ls_tab_ach_dev_pu(li_j) = ls_tab_ach_dev_pu(li_i) OR (ls_tab_ach_dev_pu(li_j) IS NULL and ls_tab_ach_dev_pu(li_i) IS NULL))
                        and (ls_tab_ach_dev_code(li_j) = ls_tab_ach_dev_code(li_i) OR (ls_tab_ach_dev_code(li_j) IS NULL AND ls_tab_ach_dev_code(li_i) IS NULL))
                        and (ls_tab_ach_dev_taux(li_j) =ls_tab_ach_dev_taux(li_i) OR (ls_tab_ach_dev_taux(li_j) IS NULL AND ls_tab_ach_dev_taux(li_i) IS NULL) )
                        and((ls_tab_lib_dlv(li_j) = ls_tab_lib_dlv(li_i)) OR (ls_tab_lib_dlv(li_j) IS NULL AND ls_tab_lib_dlv(li_i)IS NULL)) and ls_tab_bac_code(li_j) =ls_tab_bac_code(li_i) and
                        ls_tab_esp_code(li_j)=ls_tab_esp_code(li_i) and li_tab_pal_nb_inter(li_j)=li_tab_pal_nb_inter(li_i) and ((ls_tab_frais_desc(li_j)=ls_tab_frais_desc(li_i)) OR (ls_tab_frais_desc(li_j) IS NULL OR ls_tab_frais_desc(li_i) IS NULL ) )
                        ) Then
                            ls_tab_orl_ref_rgp(li_i) := ls_tab_orl_ref_rgp(li_j);
                        End if;

                    End If;
                end loop;
                If li_tab_grp_rgp(li_i) is null Then
                    li_tab_grp_rgp(li_i) :=     li_max_grp_rgp     + 1;
                end if;
            End If;
        end loop;

        for li_i in 1 .. li_indice loop

            update    GEO_GEST_REGROUP
            SET         GRP_RGP= li_tab_grp_rgp(li_i),
                        ORL_REF_RGP =ls_tab_orl_ref_rgp(li_i)
            WHERE     ORD_REF_RGP =ls_ord_ref_regroup and
                        ORD_REF_ORIG =ls_tab_ord_ref_orig(li_i) and
                        ORL_REF_ORIG = ls_tab_orl_ref_orig(li_i);
        end loop;

        declare
            cursor C_MAJ_LIG_REGROUP_PAL is
                select  ORD_REF_ORIG,GRP_ORIG,sum(L.CDE_NB_PAL) as CDE_NB_PAL
                from GEO_GEST_REGROUP R, GEO_ORDLIG L
                where R.ORD_REF_RGP =  ls_ord_ref_regroup and
                        R.ORD_REF_ORIG=  L.ORD_REF and
                        R.ORL_REF_ORIG = L.ORL_REF
                group by ORD_REF_ORIG,GRP_ORIG;
        begin
            for mlrp in C_MAJ_LIG_REGROUP_PAL loop
-- DEGUGING FROM HERE
                UPDATE GEO_GEST_REGROUP sET CDE_NB_PAL_ORIG = mlrp.CDE_NB_PAL
                where ORD_REF_RGP = ls_ord_ref_regroup and
                        ORD_REF_ORIG =mlrp.ORD_REF_ORIG and
                        GRP_ORIG =mlrp.grp_orig;

            end loop;
        end;


        /*
        UNIF_PA
        */
        declare
            CURSOR C_UNIF_PA is
                select  GRP_RGP,max(nb_col_orig) as max_col
                from GEO_GEST_REGROUP
                where      ORD_REF_ORIG =arg_ord_ref_origine
                group by  GRP_RGP;
        begin
            li_tab_grp_rgp:=ll_tab_null;
            li_indice := 0;
            for up in C_UNIF_PA loop

                        select count(distinct nb_col_orig) into ll_count_nb_col_orig
                        from GEO_GEST_REGROUP
                        where  ORD_REF_ORIG = arg_ord_ref_origine and  GRP_RGP =up.GRP_RGP;

                        If ll_count_nb_col_orig > 1 Then
                            li_indice := li_indice + 1;
                            li_tab_grp_rgp.extend();
                            li_tab_grp_rgp(li_indice) := up.GRP_RGP;
                            ll_tab_max_nb_col_orig.extend();
                            ll_tab_max_nb_col_orig(li_indice) := up.max_col;
                        End If;
            end loop;
        end;



        for li_indice in 1 .. li_tab_grp_rgp.COUNT  loop

            li_grp_rgp_tmp :=li_tab_grp_rgp(li_indice);
            ll_max_nb_col_orig:= ll_tab_max_nb_col_orig(li_indice);

            select distinct ACH_DEV_PU ,
                            ACH_PU ,
                            ACH_BTA_CODE,
                            ACH_DEV_CODE,
                            ACH_DEV_TAUX,
                            MARG_PU,
                            RIST_PU,
                            TRP_PU,
                            FRAIS_PLAT_PU,
                            DOUA_PU,
                            PRES_COURT_PU,
                            MARK_PU
            into  ls_ach_dev_pu_tmp,ld_ach_pu_tmp,ls_vte_bta_code_tmp,ls_dev_code_orig,ls_ach_dev_taux_tmp,ld_marg_pu,ld_rist_pu,ld_trp_pu,ld_frais_plat_pu,ld_doua_pu,ld_crt_pu,ld_mark_pu
            from GEO_ORDLIG, GEO_GEST_REGROUP
            where            GEO_GEST_REGROUP.ORD_REF_ORIG = arg_ord_ref_origine and
                            GEO_GEST_REGROUP.ORD_REF_ORIG = GEO_ORDLIG.ORD_REF  and
                            GEO_GEST_REGROUP.GRP_RGP          =li_grp_rgp_tmp and
                            GEO_GEST_REGROUP.nb_col_orig      =ll_max_nb_col_orig and
                            GEO_GEST_REGROUP.ORL_REF_ORIG =      GEO_ORDLIG.ORL_REF;

            update GEO_ORDLIG set ACH_DEV_PU = ls_ach_dev_pu_tmp,
                                    ACH_PU   = ld_ach_pu_tmp,
                                    ACH_BTA_CODE =ls_vte_bta_code_tmp,
                                    ACH_DEV_CODE=ls_dev_code_orig,
                                    ACH_DEV_TAUX =ls_ach_dev_taux_tmp,
                                    MARG_PU =ld_marg_pu,
                                    RIST_PU=ld_rist_pu,
                                    TRP_PU =ld_trp_pu,
                                    FRAIS_PLAT_PU =ld_frais_plat_pu,
                                    DOUA_PU =ld_doua_pu,
                                    PRES_COURT_PU=ld_crt_pu,
                                    MARK_PU =ld_mark_pu
                    where exists (select 1
                                         from GEO_GEST_REGROUP
                                         where            GEO_GEST_REGROUP.ORD_REF_ORIG =arg_ord_ref_origine  and
                                                              GEO_GEST_REGROUP.ORD_REF_ORIG = GEO_ORDLIG.ORD_REF  and
                                                              GEO_GEST_REGROUP.GRP_RGP          =li_grp_rgp_tmp and
                                                              GEO_GEST_REGROUP.nb_col_orig  <> ll_max_nb_col_orig and
                                                              GEO_GEST_REGROUP.ORL_REF_ORIG =      GEO_ORDLIG.ORL_REF);

        end loop;

        declare
            cursor C_GEN_LIG_REGROUP is
                select
                    R.GRP_RGP,
                    R.FOU_CODE_ORIG,
                    L.PROPR_CODE,
                    L.PAL_CODE,
                    --CASE WHEN (L.FOU_CODE ='STEFLEMANS' or RGP.CEN_CODE ='GROSSISTE') THEN NULL ELSE  L.ACH_DEV_PU  END as f1,
                    NULL as f1,
                    CASE WHEN (L.FOU_CODE ='STEFLEMANS' or RGP.CEN_CODE ='GROSSISTE') THEN NULL ELSE L.VTE_BTA_CODE END as f2,
                    NULL as f3,
                    NULL as f4,
                    NULL as f5,
                    'EUR' as f6,
                    1 as f7,
                    L.LIB_DLV,
                    L.BAC_CODE,
                    R.PAL_NB_COL_ORIG,
                    V.VAR_RISTOURNE,
                    V.FRAIS_PU,
                    L.ESP_CODE,
                    L.PAL_NB_PALINTER,
                    L.FRAIS_DESC,
                    V.FRAIS_UNITE,
                    sum(L.CDE_NB_COL) as CDE_NB_COL,
                    sum(L.CDE_NB_PAL) as CDE_NB_PAL,
                    sum(CDE_PDS_BRUT) as CDE_PDS_BRUT,
                    sum(CDE_PDS_NET) as CDE_PDS_NET
                from GEO_GEST_REGROUP R, GEO_ORDLIG L, geo_article_colis A,  geo_origine O,geo_variet V, GEO_ORDRE RGP
                where  R.ORD_REF_RGP =  ls_ord_ref_regroup and
                        R.ORD_REF_ORIG=  L.ORD_REF and
                        R.ORL_REF_ORIG = L.ORL_REF and
                        L.ART_REF = A.ART_REF and
                        A.esp_code = O.esp_code and
                        A.ori_code = O.ori_code and
                        A.esp_code = V.esp_code and
                        A.var_code = V.var_code and
                        R.ORD_REF_RGP = RGP.ORD_REF
                group by R.GRP_RGP,
                    R.FOU_CODE_ORIG,
                    L.PROPR_CODE,
                    L.PAL_CODE,
                    --CASE WHEN (L.FOU_CODE ='STEFLEMANS' or RGP.CEN_CODE ='GROSSISTE') THEN NULL ELSE  L.ACH_DEV_PU  END ,
                    NULL,
                    CASE WHEN (L.FOU_CODE ='STEFLEMANS' or RGP.CEN_CODE ='GROSSISTE') THEN NULL ELSE L.VTE_BTA_CODE END,
                    NULL,
                    NULL,
                    NULL,
                    'EUR',
                    1,
                    L.LIB_DLV,L.BAC_CODE,R.PAL_NB_COL_ORIG,V.VAR_RISTOURNE,V.FRAIS_PU,
                    L.ESP_CODE,
                    L.PAL_NB_PALINTER,
                    L.FRAIS_DESC,
                    V.FRAIS_UNITE;
        begin

            li_orl_lig_sav := 0;


            for glr in C_GEN_LIG_REGROUP loop

                li_grp_rgp_tmp := glr.GRP_RGP;
                ls_fou_code_tmp := glr.FOU_CODE_ORIG;
                ls_propr_code_tmp := glr.PROPR_CODE;
                ls_pal_code_tmp := glr.PAL_CODE;
                ld_vte_pu_tmp := glr.f1;
                ls_vte_bta_code_tmp := glr.f2;
                ld_ach_pu_tmp := glr.f3;
                ls_ach_bta_code_tmp := glr.f4;
                ls_ach_dev_pu_tmp := glr.f5;
                ls_ach_dev_code_tmp := glr.f6;
                ls_ach_dev_taux_tmp := glr.f7;
                ls_lib_dlv_tmp := glr.LIB_DLV;
                ls_bac_code_tmp := glr.BAC_CODE;
                li_pal_nb_col_tmp := glr.PAL_NB_COL_ORIG;
                ls_var_ristourne_tmp := glr.VAR_RISTOURNE;
                ldc_frais_pu_tmp := glr.FRAIS_PU;
                ls_esp_code_tmp := glr.ESP_CODE;
                li_pal_nb_inter_tmp := glr.PAL_NB_PALINTER;
                ls_frais_desc_tmp := glr.FRAIS_DESC;
                ls_frais_unite := glr.FRAIS_UNITE;
                li_cde_nb_col_tmp := glr.CDE_NB_COL;
                li_cde_nb_pal_tmp := glr.CDE_NB_PAL;
                ldc_cde_pds_brut_tmp := glr.CDE_PDS_BRUT;
                ldc_cde_pds_nett_tmp := glr.CDE_PDS_NET;

                declare
                    CURSOR C_GEN_LIG_REGROUP_ART is
                        select distinct L.ORL_LIG,R.ART_REF_ORIG , R.ORL_REF_RGP
                        from GEO_GEST_REGROUP R,GEO_ORDLIG L
                        where   R.ORD_REF_RGP = ls_ord_ref_regroup  and
                                    R.ORD_REF_RGP =   L.ORD_REF and
                                    R.ORL_REF_RGP =   L.ORL_REF and
                                    R.GRP_RGP = glr.GRP_RGP and
                                    R.ORL_REF_RGP  IS NOT  NULL
                                UNION
                        select distinct '99' ,R.ART_REF_ORIG,R.ORL_REF_RGP
                        from GEO_GEST_REGROUP R
                        where   R.ORD_REF_RGP = ls_ord_ref_regroup and
                                    R.GRP_RGP =glr.GRP_RGP and
                                    R.ORL_REF_RGP  IS NULL;
                    begin
                        li_nb_lig := 0;

                        for glra in C_GEN_LIG_REGROUP_ART loop
                            ls_orl_lig_rgp_tmp := glra.orl_lig;
                            ls_art_ref_tmp := glra.ART_REF_ORIG;
                            ls_orl_ref_tmp := glra.ORL_REF_RGP;

                            li_nb_lig := li_nb_lig + 1;
                            li_orl_lig_sav := li_orl_lig_sav + 1;

                            if li_orl_lig_sav > 99 then
                                msg := 'Impossible de dépasser le nombre de ligne de commandes maximal (99)';
                                res := 0;
                                return;
                            end if;

                            ls_orl_lig_sav := trim(to_char(li_orl_lig_sav,'00'));
                            If li_nb_lig =2  Then
                                li_pal_nb_col_tmp :=0;
                                li_cde_nb_col_tmp := 0;
                                li_cde_nb_pal_tmp :=0;
                                ldc_cde_pds_brut_tmp := 0;
                                ldc_cde_pds_nett_tmp := 0;
                            End If;

                            --LLEF: NEW table frais marketing/PEREQUATION
                            begin
                                -- select L.orl_ref, A.mode_culture, A.cat_code, A.ori_code, A.var_code, O.tvt_code, O.sco_code, A.col_code, L.fou_code, A.ccw_code
                                -- INTO ls_orl_ref_mark, ll_mode_culture_mark, ls_cat_code_mark, ls_ori_code_mark, ls_var_code_mark, ls_tvt_code_mark, ls_sco_code_mark, ls_col_code_mark, ls_fou_code_mark, ls_ccw_code_mark
                                -- from geo_article_colis A, geo_ordlig L, geo_ordre O
                                -- where L.ord_ref = arg_ord_ref_origine     and
                                --         L.orl_ref = ls_orl_lig_rgp_tmp and
                                --         O.ord_ref = L.ord_ref            and
                                --         A.art_ref  = ls_art_ref_tmp;

                                select A.mode_culture, A.cat_code, A.ori_code, A.var_code, A.col_code, A.ccw_code
                                INTO ll_mode_culture_mark, ls_cat_code_mark, ls_ori_code_mark, ls_var_code_mark, ls_col_code_mark, ls_ccw_code_mark
                                from geo_article_colis A
                                where A.art_ref  = ls_art_ref_tmp;

                                select  O.tvt_code, O.sco_code
                                INTO ls_tvt_code_mark, ls_sco_code_mark
                                from geo_ordre O
                                where O.ord_ref = ls_ord_ref_regroup;

                                    f_recup_frais(ls_var_code_mark, ls_ccw_code_mark, ls_sco_code_mark, ls_tvt_code_mark, ll_mode_culture_mark, ls_ori_code_mark,ll_k_frais,msg);

                                    select accompte, frais_pu, frais_unite into ld_accompte, ld_frais_pu_mark, ls_frais_unite_mark
                                    from geo_attrib_frais
                                    where k_frais = ll_k_frais;

                                    if (ls_fou_code_tmp <> 'STEFLEMANS'  and  ls_ind_grossiste <>'O' )then
                                            if (ld_accompte is not null AND ld_accompte >0) and substr(ls_col_code,1,2)<> 'CP' then
                                                ls_ach_dev_taux_tmp        :=    1;
                                                ls_ach_dev_code_tmp    :=    'EUR';
                                                ls_ach_dev_pu_tmp        :=    ld_accompte;
                                                ls_ach_bta_code_tmp        :=    'KILO';
                                                ld_ach_pu_tmp                :=  ld_accompte;
                                            else
                                                ls_ach_dev_taux_tmp     :=    1;
                                                ls_ach_dev_code_tmp :=    'EUR';
                                                ls_ach_dev_pu_tmp     :=    0;
                                                ls_ach_bta_code_tmp     :=    '';
                                                ld_ach_pu_tmp             :=    0;
                                            end if;
                                    else
                                            ls_ach_dev_taux_tmp        :=    0;
                                            ls_ach_dev_code_tmp    :=    'EUR';
                                            ls_ach_dev_pu_tmp        :=  0;
                                            ls_ach_bta_code_tmp        :=    '';
                                            ld_ach_pu_tmp                :=    0;
                                    end if;
                                    ls_frais_unite                        :=    ls_frais_unite_mark;
                                    ldc_frais_pu_tmp                    :=    ld_frais_pu_mark;
                            exception when no_data_found then
                                null; -- pass
                            end;
                            --FIN LLEF

                            If ls_orl_lig_rgp_tmp = '99' Then
                            /*    insert */
                                select distinct F_Seq_Orl_Seq() into ls_orl_ref_tmp   from DUAL;

                                /*ACHAT*/
                                If ls_ach_dev_code_tmp = geo_soc.dev_code  Then
                                    ls_ach_dev_taux_tmp := 1;
                                    ld_ach_pu_tmp := ls_ach_dev_pu_tmp;
                                Else
                                    select dev_tx_achat into ls_ach_dev_taux_tmp
                                    from geo_devise_ref
                                    where dev_code = ls_ach_dev_code_tmp and
                                        dev_code_ref =geo_soc.dev_code;

                                        ld_ach_pu_tmp := ls_ach_dev_taux_tmp * ls_ach_dev_pu_tmp;

                                End IF;

                                begin
                                    select ind_exp into ls_ind_exp  from GEO_FOURNI where FOU_code = ls_fou_code_tmp;
                                exception when no_data_found then
                                    ls_ind_exp := '';
                                end;

                                If  ls_ind_exp <>   'F' Then
                                    ls_propr_code_tmp := ls_fou_code_tmp;
                                ELSE
                                    begin
                                        select  PROP_CODE into ls_list_propr_code
                                        from GEO_FOURNI
                                        where FOU_code = ls_fou_code_tmp;
                                    exception when no_data_found then
                                        ls_list_propr_code := '';
                                    end;

                                    ls_tab_propr_code_2 :=  p_str_tab_type();
                                    f_split(ls_list_propr_code, ',', ls_tab_propr_code_2);

                                    If     ls_tab_propr_code_2.count() > 0 then
                                        ls_propr_code_tmp := ls_tab_propr_code_2(1);
                                    End If;

                                End IF;

                                INSERT INTO GEO_ORDLIG (
                                    ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL,
                                    ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, VTE_PU, VTE_BTA_CODE, FOU_CODE,
                                    CDE_PDS_BRUT, CDE_PDS_NET,
                                    FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX,
                                    REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU,PROPR_CODE,PAL_NB_PALINTER,LIB_DLV,FRAIS_DESC,FRAIS_UNITE
                                )
                                VALUES (

                                    ls_orl_ref_tmp, ls_ord_ref_regroup, ls_orl_lig_sav,ls_pal_code_tmp,li_pal_nb_col_tmp, li_cde_nb_pal_tmp, li_cde_nb_col_tmp,
                                    ld_ach_pu_tmp, ls_ach_dev_code_tmp,ls_ach_bta_code_tmp, ld_vte_pu_tmp,ls_vte_bta_code_tmp,  ls_fou_code_tmp,
                                    ldc_cde_pds_brut_tmp,ldc_cde_pds_nett_tmp,
                                    'N', 'N', 'N', 'N', 'N', ls_var_ristourne_tmp, ldc_frais_pu_tmp, 'N', 'N', ls_bac_code_tmp,0,
                                    0, ls_art_ref_tmp , ls_esp_code_tmp, 0, ls_ach_dev_taux_tmp, ls_ach_dev_pu_tmp,ls_propr_code_tmp,li_pal_nb_inter_tmp,ls_lib_dlv_tmp,ls_frais_desc_tmp,ls_frais_unite
                                );



                                update GEO_GEST_REGROUP SET ORL_REF_RGP =ls_orl_ref_tmp
                                where ORD_REF_RGP = ls_ord_ref_regroup and
                                            GRP_RGP =li_grp_rgp_tmp and
                                        ART_REF_ORIG =ls_art_ref_tmp;

                            Else
                                update GEO_ORDLIG set     ORL_LIG        =ls_orl_lig_sav,
                                                            PAL_NB_COL    =li_pal_nb_col_tmp,
                                                            CDE_NB_COL     =li_cde_nb_col_tmp,
                                                            CDE_NB_PAL    =li_cde_nb_pal_tmp,
                                                            CDE_PDS_BRUT =ldc_cde_pds_brut_tmp,
                                                            CDE_PDS_NET    =ldc_cde_pds_nett_tmp
                                where ORD_REF  = ls_ord_ref_regroup  and
                                        ORL_REF    =ls_orl_ref_tmp;

                            End If;

                        end loop;
                    end;


            end loop;
        end;


        declare
            CURSOR C_nordre_orig is
                select distinct O.NORDRE
                from GEO_ORDRE O , GEO_GEST_REGROUP R
                where  R.ORD_REF_RGP  = ls_ord_ref_regroup and
                            O.ORD_REF =  R.ORd_REF_ORIG
                ORDER BY  O.NORDRE;
        begin
            for nog in C_nordre_orig loop
                IF nog.nordre is not null Then
                    if ls_list_nordre_orig is not null then
                        ls_list_nordre_orig := ls_list_nordre_orig || ',' || nog.nordre;
                    else
                        ls_list_nordre_orig := nog.nordre;
                    end if;
                ENd IF;
            end LOOP;
        end;

        begin
        UPDATE GEO_GEST_REGROUP
        SET SOC_CODE_DETAIL ='BUK'
        where ORD_REF_RGP = ls_ord_ref_regroup and
                     SOC_CODE_DETAIL = 'SA' and
                     exists (
                     select 1
                     from GEO_GEST_REGROUP G
                     where     G.ORD_REF_RGP = ls_ord_ref_regroup and
                            G.FOU_CODE_ORIG= GEO_GEST_REGROUP.FOU_CODE_ORIG and
                            G.SOC_CODE_DETAIL = 'BUK');
        exception when no_data_found then
         null;
        end;

        declare
        CURSOR C_SOM_QTE_ORIG is
            select distinct
                FOU_CODE_ORIG,
                GRP_RGP,
                GRP_ORIG,
                CDE_NB_PAL_ORIG,
                NB_COL_ORIG,
                ORD_REF_ORIG
            from GEO_GEST_REGROUP
            where    ORD_REF_RGP =ls_ord_ref_regroup and
                        SOC_CODE_DETAIL ='BUK' and
                        NB_COL_ORIG IS NOT NULL
            ORDER BY  FOU_CODE_ORIG,GRP_RGP;
        begin
            li_cde_nb_pal_tot := 0 ;
            li_cde_nb_col_tot := 0 ;

            for sqo in C_SOM_QTE_ORIG loop
                ls_fou_code_tmp := sqo.FOU_CODE_ORIG;
                li_grp_rgp_tmp := sqo.GRP_RGP;
                li_grp_orig_tmp := sqo.GRP_ORIG;
                li_cde_nb_pal_tmp := sqo.CDE_NB_PAL_ORIG;
                li_cde_nb_col_tmp := sqo.NB_COL_ORIG;
                ls_ord_ref_orig_tmp := sqo.ORD_REF_ORIG;

                ib_buk := TRUE;

                If ls_fou_code_old = ls_fou_code_tmp  and  li_grp_rgp_old = li_grp_rgp_tmp then
                    li_cde_nb_pal_tot := li_cde_nb_pal_tot + li_cde_nb_pal_tmp ;
                    li_cde_nb_col_tot := li_cde_nb_col_tot + li_cde_nb_col_tmp ;
                ELse
                    update GEO_ORDLIG SET CDE_NB_PAL    =li_cde_nb_pal_tot,
                                                    CDE_NB_COL     =li_cde_nb_col_tot
                    where ORD_REF = ls_ord_ref_regroup         and
                            exists (select 1
                                    from GEO_GEST_REGROUP
                                    where GEO_GEST_REGROUP.ORD_REF_RGP =ls_ord_ref_regroup         and
                                            GEO_GEST_REGROUP.ORL_REF_RGP = GEO_ORDLIG.ORL_REF     and
                                            GEO_GEST_REGROUP.GRP_RGP=li_grp_rgp_old                      and
                                            GEO_ORDLIG.CDE_NB_COL >0 );

                    ls_fou_code_old :=  ls_fou_code_tmp;
                    li_grp_rgp_old   := li_grp_rgp_tmp;

                    li_cde_nb_pal_tot := li_cde_nb_pal_tmp ;
                    li_cde_nb_col_tot := li_cde_nb_col_tmp ;
                ENd If;

            end loop;
        end;

        If ib_buk =TRUE Then

            update GEO_ORDLIG SET CDE_NB_PAL    =li_cde_nb_pal_tot,
                                                    CDE_NB_COL     =li_cde_nb_col_tot
                    where ORD_REF = ls_ord_ref_regroup         and
                            exists (select 1
                                    from GEO_GEST_REGROUP
                                    where GEO_GEST_REGROUP.ORD_REF_RGP =ls_ord_ref_regroup         and
                                            GEO_GEST_REGROUP.ORL_REF_RGP = GEO_ORDLIG.ORL_REF     and
                                            GEO_GEST_REGROUP.GRP_RGP=li_grp_rgp_old                      and
                                            GEO_ORDLIG.CDE_NB_COL >0 );
        END IF;

        select sum(CDE_NB_PAL)  into ll_nb_cde_nb_pal_tot_rgp
        from GEO_ORDLIG
        where ORD_REF =ls_ord_ref_regroup;

        If  ls_decl_doua ='BOLLORE' Then
            ld_doua_pu_tot_rgp := ll_nb_cde_nb_pal_tot_rgp*3.5;
        Else
            ld_doua_pu_tot_rgp := ll_nb_cde_nb_pal_tot_rgp*2.5;
        End IF;

        delete  GEO_ORDFRA where ORD_REF =ls_ord_ref_regroup and FRA_CODE ='DEDEXP';

        begin
        select (GEO_FRAIS_TYP.ACH_DEV_PU/26)*ll_nb_cde_nb_pal_tot_rgp ,GEO_FRAIS_TYP.ACH_DEV_CODE,GEO_DEVISE_REF.DEV_TX,(GEO_FRAIS_TYP.ACH_DEV_PU *GEO_DEVISE_REF.DEV_TX/26)*ll_nb_cde_nb_pal_tot_rgp
        into ld_doua_pu_tot_new_dev_dedexp, ls_doua_dev_code,ld_doua_dev_tx,ld_doua_pu_tot_new_dedexp
        from GEO_FRAIS_TYP,GEO_DEVISE_REF
        where FRA_CODE ='DEDEXP' and
              TYT_CODE = 'S' and
              FRA_TIERS_CODE = ls_decl_doua and
              GEO_DEVISE_REF.DEV_CODE_REF = 'EUR' and
              GEO_DEVISE_REF.DEV_CODE = GEO_FRAIS_TYP.ACH_DEV_CODE;

        insert INTO GEO_ORDFRA
            (ORD_REF,FRA_CODE,ACH_DEV_PU,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS,ACH_QTE,ACH_PU,MONTANT_TOT)
            VALUES (ls_ord_ref_regroup,'DEDEXP',ld_doua_pu_tot_new_dev_dedexp,ld_doua_pu_tot_new_dev_dedexp,ls_doua_dev_code,ld_doua_dev_tx,ls_decl_doua,1,ld_doua_pu_tot_new_dedexp,ld_doua_pu_tot_new_dedexp);

        exception when no_data_found then
            insert INTO GEO_ORDFRA
            (ORD_REF,FRA_CODE,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS) VALUES (ls_ord_ref_regroup,'DEDEXP',ld_doua_pu_tot_rgp,'EUR',1,ls_decl_doua);
        end;

        delete  GEO_ORDFRA where ORD_REF =ls_ord_ref_regroup and FRA_CODE ='GMVS';

        begin
        select GEO_FRAIS_TYP.ACH_DEV_PU ,GEO_FRAIS_TYP.ACH_DEV_CODE,GEO_DEVISE_REF.DEV_TX,GEO_FRAIS_TYP.ACH_DEV_PU *GEO_DEVISE_REF.DEV_TX
        into ld_doua_pu_tot_new_dev_gmvs, ls_doua_dev_code,ld_doua_dev_tx,ld_doua_pu_tot_new_gmvs
        from GEO_FRAIS_TYP,GEO_DEVISE_REF
        where FRA_CODE ='GMVS' and
              TYT_CODE = 'T' and
              FRA_TIERS_CODE = ls_transp and
              GEO_DEVISE_REF.DEV_CODE_REF = 'EUR' and
              GEO_DEVISE_REF.DEV_CODE = GEO_FRAIS_TYP.ACH_DEV_CODE;

        insert INTO GEO_ORDFRA
            (ORD_REF,FRA_CODE,ACH_DEV_PU,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS,ACH_QTE,ACH_PU,MONTANT_TOT)
            VALUES (ls_ord_ref_regroup,'GMVS',ld_doua_pu_tot_new_dev_gmvs,ld_doua_pu_tot_new_dev_gmvs,ls_doua_dev_code,ld_doua_dev_tx,ls_transp,1,ld_doua_pu_tot_new_gmvs,ld_doua_pu_tot_new_gmvs);

        exception when no_data_found then
            ls_ord_ref_regroup := ls_ord_ref_regroup;
        end;



        update GEO_ORDRE
        SET  LIST_NORDRE_ORIG =ls_list_nordre_orig
        where ORD_REF =ls_ord_ref_regroup;

        select NORDRE into ls_nordre_regroup
        from GEO_ORDRE
        where ORD_REF =ls_ord_ref_regroup;


        /*
        UPDATE GEO_GEST_REGROUP SET SOC_CODE_DETAIL ='BUK'
        where ORD_REF_RGP =:ls_ord_ref_regroup and
                FOU_CODE_ORIG ='STEFLEMANS';
        */
        update GEO_ORDRE
        SET TYP_ORDRE ='ORI', NORDRE_RGP =ls_nordre_regroup
        where ORD_REF =arg_ord_ref_origine;

        fn_chg_dev_ordre_rgp(ls_ord_ref_regroup,res,msg);
        fn_maj_pu_vte_rgp_platef(ls_ord_ref_regroup,res,msg);

       insert into GEO_ORDLOG (ORX_REF, ORD_REF, FOU_CODE,DATDEP_FOU_P,DATDEP_GRP_P,FLAG_EXPED_FOURNNI,FLAG_EXPED_GROUPA,TYP_FOU )
        select  F_SEQ_ORX_SEQ(), ORD_REF, FOU_CODE ,DEPDATP, DEPDATP,'N' ,'N' ,'F'
        from      (  select distinct GEO_ORDRE.ORD_REF "ORD_REF",GEO_ORDLIG.FOU_CODE  "FOU_CODE",GEO_ORDRE.DEPDATP "DEPDATP"
        from GEO_ORDRE, GEO_ORDLIG
        where       GEO_ORDRE.ORD_REF =ls_ord_ref_regroup and
                    GEO_ORDRE.ORD_REF =  GEO_ORDLIG.ORD_REF and
                    not exists (select 1 from GEO_ORDLOG
                            where  GEO_ORDLOG.ORD_REF = GEO_ORDLIG.ORD_REF and
                                    GEO_ORDLOG.FOU_CODE = GEO_ORDLIG.FOU_CODE)) "temp_sel";

        COMMIT;

        f_insert_mru_ordre(ls_ord_ref_regroup,arg_username,res ,msg);

        msg := 'Réussi, L''ordre de regroupement est le : ' || ls_nordre_regroup;

    end loop;

    -- arg_soc_code :=ls_soc_code_old;
    -- geo_soc :=  f_load_geo_societe(ls_soc_code_old);


    res := 1;

end;
/
