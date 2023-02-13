/*  AUTEUR : B.AMADEI																			DATE : 11/04/19	 	*/
/*  BUT 	: Créer un ordre de replacement 																				*/
/*  PARAMETRE																												*/
/*  	   	ordre d'origine du litige																							*/
/* RETOUR																														*/
/*																																	*/
/***************************************************************************/

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CREE_ORDRE_REPLACEMENT_LIGNE" (
    arg_lil_ref varchar2,
    arg_ord_ref varchar2,
    arg_ord_ref_ori varchar2,
    arg_orl_ref_ori varchar2,
    arg_soc_code varchar2,
    res in out number,
    msg in out varchar2,
    ls_orl_ref out varchar2
)
AS
    ls_orl_lig varchar2(50);
    ls_CDE_NB_COL varchar2(50);
    ls_EXP_NB_COL varchar2(50);
    ls_EXP_PDS_NET varchar2(50);
    ls_EXP_PDS_BRUT varchar2(50);
    ls_ACH_DEV_CODE varchar2(50);
    ls_ACH_QTE  varchar2(50);
    ls_ESP_CODE varchar2(50);
    ls_ACH_BTA_CODE varchar2(50);
    ls_VTE_BTA_CODE varchar2(50);
    ls_VTE_QTE varchar2(50);
    ls_VTE_PU varchar2(50);
    ls_TOTVTE varchar2(50);
    ls_TOTACH varchar2(50);
    ls_TOTMOB varchar2(50);
    ls_ACH_DEV_PU varchar2(50);
    ls_PROPR_CODE varchar2(50);
    ls_FOU_CODE varchar2(50);
    ls_BAC_CODE varchar2(50);
    ls_CDE_NB_PAL varchar2(50);
    ls_EXP_NB_PAL varchar2(50);
    ls_pal_code_ori varchar2(50);
    ls_art_ref_ori varchar2(50);
    ls_orx_ref varchar2(50);
    ls_mdd varchar2(50);
    ls_var_ristourne varchar2(50);
    ls_ach_dev_taux varchar2(50);
    ls_ach_pu varchar2(50);

    ll_nb_pal number;
    ll_pal_nb_inter_ori number;
    ll_nb_col number ;
    ld_PDS_NET number;
    ld_col_tare number;
    ll_VTE_PU number;
    ldc_frais_pu_ori number := 0;
    ldc_ach_pu number;
    ldc_ach_dev_pu number;
    ldc_remsf_tx_mdd number;
    ldc_remsf_tx number;
    ldc_remhf_tx number;
    ld_ACH_QTE number;
    ld_VTE_QTE number;
    ld_pmb_per_com number;
    ll_pal_nb_col number;
    ldc_ach_dev_taux number;

    soc_dev_code varchar2(50);
    ls_EXP_PDS_NET_ORDLIG varchar2(50);
    ls_EXP_PDS_BRUT_ORDLIG varchar2(50);

BEGIN
    res := 0;

    /* RECUPERATION DES INFORMATIONS */

    select F_SEQ_ORL_SEQ() into ls_orl_ref FROM DUAL;

    --recherche du prochain numero de ligne dans nouvel ordre
    select TRIM(to_char(count(*)*0+1,'00'))
    into ls_orl_lig
    from GEO_ORDLIG
    where ORD_REF =arg_ord_ref;


    select  GEO_CLIENT.REM_SF_TX_MDD,  GEO_CLIENT.REM_SF_TX,GEO_CLIENT.REM_HF_TX
    into  	ldc_remsf_tx_mdd,ldc_remsf_tx, ldc_remhf_tx
    from GEO_ORDRE , GEO_CLIENT
    where GEO_ORDRE.ORD_REF = arg_ord_ref and
            GEO_ORDRE.CLI_REF = GEO_CLIENT.CLI_REF;

    select dev_code
    into soc_dev_code
    from geo_societe
    where soc_code = arg_soc_code;


    --recherche des informations sur la ligne de l'ordre d'origine du litige
    select  C.col_tare,X.esp_code,OL.fou_code ,OL.pal_code,OL.art_ref,OL.pal_nb_palinter,X.mdd,V.var_ristourne ,OL.propr_code, OL.bac_code, X.u_par_colis,OL.PAL_NB_COL,OL.EXP_PDS_NET,OL.EXP_PDS_BRUT, OL.ACH_DEV_PU,OL.ACH_DEV_CODE
    into  ld_col_tare, ls_ESP_CODE,ls_fou_code,ls_pal_code_ori,ls_art_ref_ori,ll_pal_nb_inter_ori,ls_mdd,ls_var_ristourne,ls_PROPR_CODE,ls_bac_code,ld_pmb_per_com,ll_pal_nb_col,ls_EXP_PDS_NET_ORDLIG,ls_EXP_PDS_BRUT_ORDLIG,  ldc_ach_dev_pu,ls_ach_dev_code
    from geo_article X, geo_colis C, geo_ordlig OL, GEO_VARIET V
            where 	OL.orl_ref =arg_orl_ref_ori 	and
                        OL.art_ref = X.art_ref 			and
                        C.esp_code = X.esp_code 		and
                        C.col_code = X.col_code and
                        X.esp_code = V.esp_code and
                        X.var_code = V.var_code;

    If ld_pmb_per_com = 0 or ld_pmb_per_com is null Then ld_pmb_per_com := 1; end if;

    if ls_mdd = 'O' then
        ldc_remsf_tx :=ldc_remsf_tx_mdd;
    End If;


    --recherche des informations de la ligne du litige
    select CLI_NB_COL,
            CLI_NB_PAL,
            CLI_PDS_NET,
            CLI_BTA_CODE,
            RES_BTA_CODE,
            CLI_PU
    into   	ll_nb_col,
            ll_nb_pal,
            ld_PDS_NET,
            ls_vte_bta_code,
            ls_ACH_BTA_CODE,
            ll_VTE_PU
    from GEO_LITLIG
    where  LIL_REF = arg_lil_ref;



    --ll_VTE_PU = 0

    ls_CDE_NB_COL := to_char(ll_nb_col);
    ls_EXP_NB_COL := to_char(ll_nb_col);

    ls_CDE_NB_PAL := to_char(ll_nb_pal );
    ls_EXP_NB_PAL := to_char(ll_nb_pal );

    ls_EXP_PDS_NET := to_char(ld_PDS_NET);
    If  ls_EXP_PDS_NET_ORDLIG <> ls_EXP_PDS_NET Then
        ls_EXP_PDS_BRUT := to_char( round(ld_pds_net + (ld_col_tare * ll_nb_col), 0));
    ELse
        ls_EXP_PDS_BRUT := ls_EXP_PDS_BRUT_ORDLIG;
    End If;

    case ls_ach_bta_code
        when 'COLIS' then
            ld_ACH_QTE 	:= ll_nb_col;
        when 'KILO' then
            ld_ACH_QTE 	:=ld_pds_net;
        when 'PAL' then
            ld_ACH_QTE 	:= ll_nb_pal;
        when 'TONNE' then
            ld_ACH_QTE 	:= round(ld_pds_net / 1000, 0);
        when 'CAMION' then
            ld_ACH_QTE 	:= 0;
        else
                ld_ACH_QTE 	:= round(ll_nb_col *ld_pmb_per_com, 0);
    end case;
            -- calcul nombre unité de vente
    case ls_vte_bta_code
        when 'COLIS' then
            ld_VTE_QTE	:= ll_nb_col;
        when 'KILO' then
            ld_VTE_QTE	:= ld_pds_net;
        when 'PAL' then
            ld_VTE_QTE	:= ll_nb_pal;
        when 'TONNE' then
            ld_VTE_QTE	:= round(ld_pds_net / 1000, 0);
        when 'CAMION' then
            ld_VTE_QTE	:= 0;
        else
            ld_VTE_QTE	:= round(ll_nb_col * ld_pmb_per_com, 0);
    end case;


    ls_ACH_DEV_CODE :=soc_dev_code;
    If ls_ach_dev_code = soc_dev_code  Then
        ldc_ach_dev_taux  := 1;
        ldc_ach_pu := ldc_ach_dev_pu;
    ELSE
        select dev_tx into ldc_ach_dev_taux
        from geo_devise_ref
        where dev_code = ls_ach_dev_code and
                dev_code_ref=soc_dev_code;

        ldc_ach_pu := ldc_ach_dev_pu*ldc_ach_dev_taux;
    ENd If;

    --ls_ACH_QTE = to_char(ll_nb_col)
    ls_ACH_DEV_TAUX := '1';
    ls_ACH_PU := '0';
    ls_ACH_DEV_PU := '0';


    ls_VTE_PU := to_char(ll_VTE_PU );
    --ls_VTE_QTE = to_char(ll_nb_col)


    ls_TOTVTE := to_char(ll_VTE_PU * ll_nb_col);
    ls_TOTACH := to_char(ldc_ACH_PU * ll_nb_col);
    ls_TOTMOB := '0';

    If arg_soc_code ='BWS' then
        ls_TOTVTE :='0';
        -- ls_TOTACH := '0';
        ls_VTE_PU:='0';
        -- ls_ACH_PU := '0';
        ldc_frais_pu_ori := 0;
    End If;
    If ll_pal_nb_inter_ori is null Then ll_pal_nb_inter_ori := 0; end if;

    /*
    ls_PROPR_CODE ='BW'
    ls_bac_code = 'SW' */
    begin
        INSERT INTO GEO_ORDLIG (
            ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL,EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT,
            EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE,
            CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT,
            FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX,
            REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU,PROPR_CODE,PAL_NB_PALINTER)
        VALUES (

            ls_orl_ref, arg_ord_ref,ls_orl_lig,ls_pal_code_ori,ll_pal_nb_col, ls_CDE_NB_PAL, ls_CDE_NB_COL,ls_EXP_NB_PAL, ls_EXP_NB_COL, ls_EXP_PDS_BRUT,
            ls_EXP_PDS_NET, ldc_ACH_PU, ls_ACH_DEV_CODE,ls_ACH_BTA_CODE, ld_ACH_QTE, ls_VTE_PU,ls_VTE_BTA_CODE, ld_VTE_QTE, ls_FOU_CODE,
            0, 0, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0,
            'N', 'N', 'N', 'N', 'N', ls_var_ristourne, ldc_frais_pu_ori, 'N', 'N', ls_bac_code,ldc_remsf_tx, ldc_remhf_tx, ls_ART_REF_ori , ls_ESP_CODE, 0, ldc_ACH_DEV_TAUX, ls_ACH_DEV_PU,ls_PROPR_CODE,ll_pal_nb_inter_ori);
    exception when others then
        res := 0;
        msg := '%%% Erreur à la création de la ligne d''ordre: ' || SQLERRM;
        return;
    end;

    begin
        UPDATE GEO_ORDRE SET
            TOTVTE = TOTVTE + ls_TOTVTE,
            TOTCOL = TOTCOL + ls_CDE_NB_COL,
            TOTPDSNET = TOTPDSNET + ls_EXP_PDS_NET,
            TOTPDSBRUT = TOTPDSBRUT + ls_EXP_PDS_BRUT
        WHERE
            ORD_REF = arg_ord_ref;
    exception when others then
        res := 0;
        msg := '%%% Erreur mise à jour de l''ordre: ' || SQLERRM;
        return;
    end;

    ls_orx_ref :=  F_SEQ_ORX_SEQ();

    begin
        /* INFORMATION LOGISTIQUE */
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
                    LOCUS_TRACE,
                    DATDEP_FOU_P,
                    DATDEP_FOU_R)
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
                OL.LOCUS_TRACE,
                OL.DATDEP_FOU_P,
                OL. DATDEP_FOU_R
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


    f_duplique_traca(arg_ord_ref_ori, arg_orl_ref_ori,arg_ord_ref,ls_orl_ref,res,msg);
    if res = 0 then return; end if;

    res := 1;

END;
/


