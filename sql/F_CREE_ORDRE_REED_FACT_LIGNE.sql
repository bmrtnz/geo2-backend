DROP PROCEDURE GEO_ADMIN.F_CREE_ORDRE_REED_FACT_LIGNE;

CREATE OR REPLACE PROCEDURE GEO_ADMIN."F_CREE_ORDRE_REED_FACT_LIGNE" (
    arg_ord_ref in GEO_ORDLIG.ORD_REF%TYPE,
    arg_ord_ref_ori in GEO_ORDLIG.ORD_REF%TYPE,
    gs_soc_code in varchar2,
    res in out number,
    msg in out varchar2,
    ls_orl_ref out varchar2
)
AS
    ls_orl_lig varchar2(50);
    ll_ACH_PU number := 0;
    ll_nb_col number ;
    ls_CDE_NB_COL varchar2(50);
    ls_EXP_NB_COL varchar2(50);
    ls_EXP_PDS_NET varchar2(50);
    ld_PDS_NET number;
    ld_col_tare number;
    ll_VTE_PU number;
    ldc_frais_pu_ori number := 0;
    ls_EXP_PDS_BRUT varchar2(50);
    ls_ACH_DEV_CODE varchar2(50);
    ls_ACH_QTE varchar2(50);
    ls_ESP_CODE varchar2(50);
    ls_ACH_BTA_CODE varchar2(50);
    ls_VTE_BTA_CODE varchar2(50);
    ls_VTE_QTE varchar2(50);
    ls_VTE_PU varchar2(50);
    ls_TOTVTE varchar2(50);
    ls_TOTACH varchar2(50);
    ls_TOTMOB varchar2(50);
    ls_ACH_PU varchar2(50);
    ls_ach_dev_taux varchar2(50);
    ls_ACH_DEV_PU varchar2(50);

    ls_PROPR_CODE varchar2(50);
    ls_FOU_CODE varchar2(50);
    ls_BAC_CODE varchar2(50);
    ls_CDE_NB_PAL varchar2(50);
    ls_EXP_NB_PAL varchar2(50);

    ll_nb_pal number;
    ll_pal_nb_inter_ori number;

    ls_pal_code_ori varchar2(50);
    ls_art_ref_ori varchar2(50);
    ls_orx_ref varchar2(50);
    ls_mdd varchar2(50);

    ldc_remsf_tx_mdd number;
    ldc_remsf_tx number;
    ldc_remhf_tx number;
    ld_ACH_QTE number;
    ld_VTE_QTE number;
    ld_pmb_per_com number;

    ls_var_ristourne varchar2(50);

    ls_orl_ref_ori varchar2(50);

    cursor cur_orl_ref_ori is
        select ORL_REF
        from GEO_ORDLIG
        where ORD_REF = arg_ord_ref_ori and
            VALIDE ='O';
BEGIN
    res := 0;

    open cur_orl_ref_ori;
    loop

        fetch cur_orl_ref_ori into ls_orl_ref_ori;
        EXIT WHEN cur_orl_ref_ori%notfound;

        /* RECUPERATION DES INFORMATIONS */

            select F_SEQ_ORL_SEQ() into ls_orl_ref FROM DUAL;

            ls_bac_code := 'SW';
            ls_PROPR_CODE :='BW';
            ls_FOU_CODE	:='BW';

            SELECT dev_code
            INTO ls_ACH_DEV_CODE
            FROM GEO_SOCIETE
            WHERE soc_code = gs_soc_code;

            ls_ACH_DEV_TAUX := '1';
            ls_ACH_PU := '0';
            ls_ACH_DEV_PU := '0';
            ls_TOTACH := '0';

            BEGIN
                INSERT INTO GEO_ORDLIG (
                orl_ref,ord_ref,orl_lig,pro_ref,pal_code,pan_code,pal_nb_col,cde_nb_pal,cde_nb_col,exp_nb_pal,exp_nb_col,exp_pds_brut,exp_pds_net,ach_pu,ach_dev_code,ach_bta_code,ach_qte,vte_pu,vte_bta_code,
                vte_qte,fou_code,grp_code,trp_code,mod_user,mod_date,valide,lib_dlv,cde_pds_brut,cde_pds_net,obs_fourni,totvte,totrem,totres,totfrd,totach,totmob,tottrp,tottrs,totcrt,flexp,flliv,flbaf,flfac,
                fac_nb_pal,fac_nb_col,fac_pds_brut,fac_pds_net,ht_net,tva_1,tva_2,tva_3,compte_a_credit,lig_ht_brut_0,lig_ht_brut_1,lig_ht_brut_2,lig_ht_rist_0,lig_ht_rist_1,lig_ht_rist_2,lig_ht_ctifl_0,lig_ht_ctifl_1,lig_ht_ctifl_2,tvc_code,lig_ht_ctiflrist_0,
                lig_ht_ctiflrist_1,lig_ht_ctiflrist_2,ht_brut,fou_flver,fou_facnum,fou_facdate,var_ristourne,frais_pu,frais_unite,frais_desc,flverfou,flvertrp,lig_remsf,lig_remhf,cq_ref,fou_facref,trp_facref,pde_ref,pca_ref,bac_code,exp_nb_pal_inter,remsf_tx,
                remhf_tx,totremsf,vte_pu_net,lig_ht_net_0,lig_ht_net_1,lig_ht_net_2,lig_ht_net_3,bac_fac_num,fou_bac_fac_num,art_ref,esp_code,suc_code,pen_code,tem_code,stm_ref,cql_ref,stock_nb_resa,lig_ht_interfel_0,lig_ht_interfel_1,lig_ht_interfel_2,
                lig_ht_interfelrist_0,lig_ht_interfelrist_1,lig_ht_interfelrist_2,totfad,ach_dev_taux,ach_dev_pu,user_verfou,user_vertrp,date_verfou,date_vertrp,exp_pal_nb_col,ref_edi_ligne,pal_nb_palinter,palinter_code,demipal_ind,propr_code,ind_gratuit,
                date_entree_ifco,date_sortie_ifco,nb_cqphotos,indbloq_ach_dev_pu,date_envoi_syleg_fou,palox_ko_nbr,palox_ko_cause,totfrais_plateforme,promo_code,cotisation_interfel_fr,cotisation_interfel_imp,lig_ht_interfel_imp_0,lig_ht_interfel_imp_1,
                lig_ht_interfel_imp_2,lig_ht_interfelrist_imp_0,lig_ht_interfelrist_imp_1,lig_ht_interfelrist_imp_2,art_ref_kit,gtin_colis_kit,nb_colis_manquant,list_certifs,cert_origine)
                select ls_orl_ref, arg_ord_ref,orl_lig,pro_ref,pal_code,pan_code,pal_nb_col,cde_nb_pal,cde_nb_col,exp_nb_pal,exp_nb_col,exp_pds_brut,exp_pds_net,ls_ACH_PU,ls_ACH_DEV_CODE,ach_bta_code,ach_qte,vte_pu,vte_bta_code,
                vte_qte,ls_FOU_CODE,grp_code,trp_code,mod_user,mod_date,valide,lib_dlv,cde_pds_brut,cde_pds_net,obs_fourni,totvte,totrem,totres,totfrd,ls_TOTACH,totmob,tottrp,tottrs,totcrt,flexp,flliv,flbaf,flfac,
                fac_nb_pal,fac_nb_col,fac_pds_brut,fac_pds_net,ht_net,tva_1,tva_2,tva_3,compte_a_credit,lig_ht_brut_0,lig_ht_brut_1,lig_ht_brut_2,lig_ht_rist_0,lig_ht_rist_1,lig_ht_rist_2,lig_ht_ctifl_0,lig_ht_ctifl_1,lig_ht_ctifl_2,tvc_code,lig_ht_ctiflrist_0,
                lig_ht_ctiflrist_1,lig_ht_ctiflrist_2,ht_brut,fou_flver,fou_facnum,fou_facdate,var_ristourne,frais_pu,frais_unite,frais_desc,flverfou,flvertrp,lig_remsf,lig_remhf,cq_ref,fou_facref,trp_facref,pde_ref,pca_ref,ls_bac_code,exp_nb_pal_inter,remsf_tx,
                remhf_tx,totremsf,vte_pu_net,lig_ht_net_0,lig_ht_net_1,lig_ht_net_2,lig_ht_net_3,bac_fac_num,fou_bac_fac_num,art_ref,esp_code,suc_code,pen_code,tem_code,stm_ref,cql_ref,stock_nb_resa,lig_ht_interfel_0,lig_ht_interfel_1,lig_ht_interfel_2,
                lig_ht_interfelrist_0,lig_ht_interfelrist_1,lig_ht_interfelrist_2,totfad,ls_ACH_DEV_TAUX,ls_ACH_DEV_PU,user_verfou,user_vertrp,date_verfou,date_vertrp,exp_pal_nb_col,ref_edi_ligne,pal_nb_palinter,palinter_code,demipal_ind,ls_PROPR_CODE,ind_gratuit,
                date_entree_ifco,date_sortie_ifco,nb_cqphotos,indbloq_ach_dev_pu,date_envoi_syleg_fou,palox_ko_nbr,palox_ko_cause,totfrais_plateforme,promo_code,cotisation_interfel_fr,cotisation_interfel_imp,lig_ht_interfel_imp_0,lig_ht_interfel_imp_1,
                lig_ht_interfel_imp_2,lig_ht_interfelrist_imp_0,lig_ht_interfelrist_imp_1,lig_ht_interfelrist_imp_2,art_ref_kit,gtin_colis_kit,nb_colis_manquant,list_certifs,cert_origine
                from GEO_ORDLIG
                where ORD_REF = arg_ord_ref_ori and
                        ORL_REF = ls_orl_ref_ori;
            exception when others then
                res := 0;
                msg := '%%% Erreur à la création de la ligne d''ordre: ' || SQLERRM;
                return;
            end;


        --
            ls_orx_ref :=  F_SEQ_ORX_SEQ();

            /* INFORMATION LOGISTIQUE */
            begin
                insert into GEO_ORDLOG
                        (ORX_REF,
                        ORD_REF,
                        FOU_CODE,
                        TRP_CODE,
                        INSTRUCTIONS,
                        FOU_REF_DOC,
                        REF_LOGISTIQUE,
                        PLOMB,
                        IMMATRICULATION,
                        DETECTEUR_TEMP,
                        CERTIF_CONTROLE,
                        CERTIF_PHYTO,
                        BILL_OF_LADING,
                        CONTAINER,
                        LOCUS_TRACE)
                select ls_orx_ref,
                    arg_ord_ref,
                    OL.FOU_CODE,
                    OL.TRP_CODE,
                    OL.INSTRUCTIONS,
                    OL.FOU_REF_DOC,
                    OL.REF_LOGISTIQUE,
                    OL.PLOMB,
                    OL.IMMATRICULATION,
                    OL.DETECTEUR_TEMP,
                    OL.CERTIF_CONTROLE,
                    OL.CERTIF_PHYTO,
                    OL.BILL_OF_LADING,
                    OL.CONTAINER,
                    OL.LOCUS_TRACE
                    from GEO_ORDLOG OL
                    where OL.ORD_REF = arg_ord_ref_ori  and
                            OL.FOU_CODE =ls_FOU_CODE   and
                            not exists (select 1
                                            from GEO_ORDLOG OL2
                                            where OL2.ORD_REF  =arg_ord_ref and
                                                    OL2.FOU_CODE =ls_FOU_CODE);
            exception when others then
                res := 0;
                msg := '%%% Erreur mise à jour de l''ordre: ' || SQLERRM;
                return;
            end;

            f_duplique_traca(arg_ord_ref_ori, ls_orl_ref_ori,arg_ord_ref,ls_orl_ref,res,msg);
    end loop;
    close cur_orl_ref_ori;

    res := 1;
END;
/
