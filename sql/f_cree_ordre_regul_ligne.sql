CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_CREE_ORDRE_REGUL_LIGNE(
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_orl_ref IN GEO_ORDLIG.ORL_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_ord_ref_regul IN GEO_ORDRE.ORD_REF%TYPE,
    arg_ind_det IN varchar2,
    res OUT number,
    msg OUT varchar2,
    ls_orl_ref OUT GEO_ORDLIG.ORL_REF%TYPE
)
AS
    --ls_orl_ref GEO_ORDLIG.ORL_REF%TYPE;
    ls_orl_lig GEO_ORDLIG.ORL_LIG%TYPE;
    ldc_remsf_tx_mdd GEO_CLIENT.REM_SF_TX_MDD%TYPE;
    ldc_remsf_tx GEO_CLIENT.REM_SF_TX%TYPE;
    ldc_remhf_tx GEO_CLIENT.REM_HF_TX%TYPE;
    ld_col_tare geo_colis.COL_TARE%TYPE;
    ls_ESP_CODE geo_article.ESP_CODE%TYPE;
    ls_fou_code geo_ordlig.FOU_CODE%TYPE;
    ls_pal_code_ori geo_ordlig.PAL_CODE%TYPE;
    ls_art_ref_ori geo_ordlig.ART_REF%TYPE;
    ll_pal_nb_inter_ori geo_ordlig.PAL_NB_PALINTER%TYPE;
    ls_mdd geo_article.MDD%TYPE;
    ls_var_ristourne GEO_VARIET.var_ristourne%TYPE;
    ls_PROPR_CODE GEO_ORDLIG.PROPR_CODE%TYPE;
    ls_bac_code GEO_ORDLIG.BAC_CODE%TYPE;
    ld_pmb_per_com GEO_ARTICLE.u_par_colis%TYPE;
    ls_ach_bta_code GEO_ORDLIG.ACH_BTA_CODE%TYPE;
    ls_vte_bta_code GEO_ORDLIG.VTE_BTA_CODE%TYPE;
    ls_ACH_DEV_CODE GEO_ORDLIG.ACH_DEV_CODE%TYPE;
    ld_ACH_DEV_TAUX GEO_ORDLIG.ACH_DEV_TAUX%TYPE;

    ls_EXP_PDS_NET GEO_ORDLIG.EXP_PDS_NET%TYPE;
    ls_EXP_PDS_BRUT GEO_ORDLIG.EXP_PDS_BRUT%TYPE;
    ls_EXP_NB_COL GEO_ORDLIG.EXP_NB_COL%TYPE;
    ls_EXP_NB_PAL GEO_ORDLIG.EXP_NB_PAL%TYPE;
    ls_CDE_NB_COL GEO_ORDLIG.CDE_NB_COL%TYPE;
    ls_CDE_NB_PAL GEO_ORDLIG.CDE_NB_PAL%TYPE;
    ll_VTE_PU GEO_ORDLIG.VTE_PU%TYPE;
    ld_VTE_QTE GEO_ORDLIG.VTE_QTE%TYPE;
    ll_pal_nb_col GEO_ORDLIG.PAL_NB_COL%TYPE;

    ll_ACH_PU GEO_ORDLIG.ACH_PU%TYPE := 0;
    ls_ACH_PU varchar2(20);
    ls_ACH_DEV_PU varchar2(20);
    ld_ACH_QTE number;
    ls_VTE_PU GEO_ORDLIG.ACH_PU%TYPE;
    ls_TOTVTE varchar2(20);
    ls_TOTACH varchar2(20);
    ls_TOTMOB varchar2(20);
    ldc_frais_pu_ori number := 0;

    ls_orx_ref GEO_ORDLOG.ORX_REF%TYPE;
BEGIN
    -- correspond à f_cree_ordre_regul_ligne.pbl
    res := 0;
    msg := '';

    -- RECUPERATION DES INFORMATIONS
    select F_SEQ_ORL_SEQ() into ls_orl_ref FROM DUAL;

    -- recherche du prochain numero de ligne dans nouvel ordre
    select ORL_lig
    into ls_orl_lig
    from GEO_ORDLIG
    where ORD_REF = arg_ord_ref and
            ORL_REF = arg_orl_ref;

    select  GEO_CLIENT.REM_SF_TX_MDD,  GEO_CLIENT.REM_SF_TX,GEO_CLIENT.REM_HF_TX
    into  	ldc_remsf_tx_mdd, ldc_remsf_tx, ldc_remhf_tx
    from GEO_ORDRE , GEO_CLIENT
    where GEO_ORDRE.ORD_REF = arg_ord_ref_regul and
            GEO_ORDRE.CLI_REF = GEO_CLIENT.CLI_REF;

    -- recherche des informations sur la ligne de l'ordre d'origine du litige
    select  C.col_tare,X.esp_code,OL.fou_code ,OL.pal_code,OL.art_ref,OL.pal_nb_palinter,X.mdd,V.var_ristourne ,OL.propr_code, OL.bac_code, X.u_par_colis,OL.ach_bta_code,OL.vte_bta_code,OL.ACH_DEV_CODE,OL.ACH_DEV_TAUX
    into  ld_col_tare, ls_ESP_CODE,ls_fou_code,ls_pal_code_ori,ls_art_ref_ori,ll_pal_nb_inter_ori,ls_mdd,ls_var_ristourne,ls_PROPR_CODE,ls_bac_code,ld_pmb_per_com,ls_ach_bta_code,ls_vte_bta_code,ls_ACH_DEV_CODE,ld_ACH_DEV_TAUX
    from geo_article X, geo_colis C, geo_ordlig OL, GEO_VARIET V
    where 	OL.orl_ref = arg_orl_ref 	and
            OL.art_ref = X.art_ref 			and
            C.esp_code = X.esp_code 		and
            C.col_code = X.col_code and
            X.esp_code = V.esp_code and
            X.var_code = V.var_code;

    If ld_pmb_per_com = 0 or ld_pmb_per_com is null Then
        ld_pmb_per_com := 1;
    end if;
    if ls_mdd = 'O' then
        ldc_remsf_tx := ldc_remsf_tx_mdd;
    End If;

    If arg_ind_det = 'O' then
        select EXP_PDS_NET,
               EXP_PDS_BRUT,
               EXP_NB_COL,
               EXP_NB_PAL,
               CDE_NB_COL,
               CDE_NB_PAL,
               VTE_PU,
               VTE_QTE,
               PAL_NB_COL
        into ls_EXP_PDS_NET,
            ls_EXP_PDS_BRUT,
            ls_EXP_NB_COL,
            ls_EXP_NB_PAL,
            ls_CDE_NB_COL,
            ls_CDE_NB_PAL,
            ll_VTE_PU,
            ld_VTE_QTE,
            ll_pal_nb_col
        FROM GEO_ORDLIG
        where ORD_REF = arg_ord_ref and
                ORL_REF = arg_orl_ref;
    else
        ls_EXP_PDS_NET := null;
        ls_EXP_PDS_BRUT := null;

        ls_EXP_NB_COL := null;
        ls_EXP_NB_PAL := null;

        ls_CDE_NB_COL := null;
        ls_CDE_NB_PAL := null;

        ll_VTE_PU := 0;
    end if;

    ll_ACH_PU := 0;
    ls_ACH_PU := '0';
    ls_ACH_DEV_PU := '0';
    ls_VTE_PU := to_char(ll_VTE_PU);
    ls_TOTVTE := to_char(ll_VTE_PU * to_number(ls_EXP_NB_COL));
    ls_TOTACH := to_char(ll_ACH_PU * to_number(ls_EXP_NB_COL));
    ls_TOTMOB := '0';

    If arg_soc_code = 'BWS' then
        ls_TOTVTE := '0';
        ls_TOTACH := '0';
	    ls_VTE_PU :='0';
	    ls_ACH_PU := '0';
	    ldc_frais_pu_ori := 0;
    End If;

    begin
        INSERT INTO GEO_ORDLIG (
            ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL,EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT,
            EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE,
            CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT,
            FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX,
            REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU,PROPR_CODE,PAL_NB_PALINTER)
        VALUES (
               ls_orl_ref, arg_ord_ref, ls_orl_lig,ls_pal_code_ori,ll_pal_nb_col, ls_CDE_NB_PAL, ls_CDE_NB_COL, ls_EXP_NB_PAL, ls_EXP_NB_COL, ls_EXP_PDS_BRUT,
               ls_EXP_PDS_NET, ls_ACH_PU, ls_ACH_DEV_CODE,ls_ACH_BTA_CODE, ld_ACH_QTE, ls_VTE_PU, ls_VTE_BTA_CODE, ld_VTE_QTE, ls_FOU_CODE,
               0, 0, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0,
               'N', 'N', 'N', 'N', 'N', ls_var_ristourne, ldc_frais_pu_ori, 'N', 'N', ls_bac_code,ldc_remsf_tx, ldc_remhf_tx, ls_ART_REF_ori , ls_ESP_CODE, 0, ld_ACH_DEV_TAUX, ls_ACH_DEV_PU,ls_PROPR_CODE,ll_pal_nb_inter_ori
        );
    exception when others then
        msg := '%%% Erreur à la création de la ligne d~''ordre: ' || SQLERRM;
        return;
    end;

    ls_orx_ref := F_SEQ_ORX_SEQ();

    -- INFORMATION LOGISTIQUE
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
               arg_ord_ref_regul,
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
        where OL.ORD_REF = arg_ord_ref and
                OL.FOU_CODE = ls_FOU_CODE and
            not exists (select 1
                        from GEO_ORDLOG OL2
                        where OL2.ORD_REF  = arg_ord_ref_regul and
                                OL2.FOU_CODE =OL.FOU_CODE);
    exception when others then
        msg := '%%% Erreur mise à jour de l~''ordre: ' + SQLERRM;
        return;
    end;

    f_duplique_traca(arg_ord_ref, arg_orl_ref,arg_ord_ref_regul,ls_orl_ref, res, msg);
end;
/

