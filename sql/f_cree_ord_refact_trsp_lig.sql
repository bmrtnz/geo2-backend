CREATE OR REPLACE PROCEDURE "F_CREE_ORD_REFACT_TRSP_LIG" (
    arg_ord_ref_origine in GEO_ORDRE.ord_ref%type,
    arg_ord_ref_refact in GEO_ORDRE.ord_ref%type,
    arg_mont_indemn in number,
    gs_soc_code in varchar2,
    gs_username in varchar2,
	res out number,
    msg out varchar2,
    ls_orl_ref out varchar2
)
AS
    ls_orl_lig varchar2(50);
    ls_CDE_NB_COL varchar2(50);
    ls_EXP_NB_COL varchar2(50);
    ls_EXP_PDS_NET varchar2(50);
    ls_EXP_PDS_BRUT varchar2(50);
    ls_DEV_CODE varchar2(50);
    ls_ACH_QTE  varchar2(50);
    ls_ESP_CODE varchar2(50);
    ls_ACH_BTA_CODE varchar2(50);
    ls_VTE_BTA_CODE varchar2(50);
    ls_VTE_QTE varchar2(50);
    ls_VTE_PU varchar2(50);
    ls_TOTVTE varchar2(50);
    ls_TOTACH varchar2(50);
    ls_TOTMOB varchar2(50);
    ls_ACH_PU varchar2(50);
    ls_dev_taux varchar2(50);
    ls_ACH_DEV_PU varchar2(50);
    ls_ACH_DEV_CODE varchar2(50);
    ls_ACH_DEV_TAUX  varchar2(50);
    ls_PROPR_CODE varchar2(50);
    ls_FOU_CODE varchar2(50);
    ls_BAC_CODE varchar2(50);
    ls_CDE_NB_PAL varchar2(50);
    ls_EXP_NB_PAL varchar2(50);
    ls_pal_code_ori varchar2(50);
    ls_art_ref_indemn varchar2(50);
    ls_orx_ref varchar2(50);

    ll_ACH_PU number := 0;
    ll_nb_col number;
    ld_PDS_NET number;
    ld_col_tare number;
    ll_VTE_PU number;
    ldc_frais_pu_ori number := 0;
    ldc_vte_dev_taux number;
    ll_nb_pal number;
    ll_pal_nb_inter_ori number;
    ldc_vte_pu number;

    soc_dev_code varchar2(50);

begin

	res := 0;
	msg := '';

    /* RECUPERATION DES INFORMATIONS */
    select F_SEQ_ORL_SEQ() into ls_orl_ref FROM DUAL;

    ls_ESP_CODE  := 'PRESTA';

    select ART_REF into ls_art_ref_indemn
    from GEO_ARTICLE
    where ESP_CODE = ls_ESP_CODE  	and
                VAR_CODE ='INDEMN' 	  		and
            VALIDE ='O';

    --recherche du prochain numero de ligne dans nouvel ordre
    select TRIM(to_char(count(*)*0+1,'00'))
    into ls_orl_lig
    from GEO_ORDLIG
    where ORD_REF = arg_ord_ref_refact;


    select DEV_TX into ldc_vte_dev_taux
    from GEO_ORDRE
    where ORD_REF = arg_ord_ref_refact;

    select dev_code into soc_dev_code
    from GEO_SOCIETE
    where soc_code = gs_soc_code;


    --recherche des informations sur la ligne de l'ordre d'origine du litige
    ls_PROPR_CODE :='BWINDEMNISAT';
    ls_FOU_CODE := 'BWINDEMNISAT';
    --ld_col_tare
    --ls_pal_code_ori
    --ls_art_ref_ori
    --ll_pal_nb_inter_ori

    --select CLI_NB_COL,
    --		CLI_NB_PAL,
    --		CLI_PDS_NET,
    --		CLI_BTA_CODE,
    --		CLI_PU
    --into   	 :ll_nb_col,
    --	   	 :ll_nb_pal,
    --		 :ld_PDS_NET,
    --		 :ls_ACH_BTA_CODE,
    --		 :ll_VTE_PU
    --from GEO_ORDRE
    --where  LIL_REF = :arg_lil_ref;

    ls_CDE_NB_COL := '0';
    ls_EXP_NB_COL :=  '0';
    --
    ls_CDE_NB_PAL := '0';
    ls_EXP_NB_PAL := '0';
    --
    --
    ls_EXP_PDS_NET := '0';
    ls_EXP_PDS_BRUT := '0';
    --
    ls_ACH_DEV_CODE := soc_dev_code;
    ls_ACH_QTE := '0';
    ls_ACH_DEV_TAUX := '1';
    ls_ACH_PU := '0';
    ls_ACH_DEV_PU := '0';
    ls_ACH_BTA_CODE :=  'UNITE';
    --
    --
    --
    ldc_VTE_PU := round(arg_mont_indemn/ldc_vte_dev_taux,2);
    ls_VTE_QTE := '1';
    ls_VTE_BTA_CODE :=  'UNITE';
    --
    --
    ls_TOTVTE := to_char(ldc_VTE_PU);
    ls_TOTACH := to_char(ll_ACH_PU * ll_nb_col);
    ls_TOTMOB := '0';
    --
    --
    --
    ls_PROPR_CODE :='BWINDEMNISAT';
    ls_FOU_CODE := 'BWINDEMNISAT';
    ls_bac_code := 'SW';

    ll_pal_nb_inter_ori := 0;

    begin
        INSERT INTO GEO_ORDLIG (
            ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL,EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT,
            EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE,
            CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT,
            FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX,
            REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU,PROPR_CODE,PAL_NB_PALINTER)
        VALUES (
            ls_orl_ref, arg_ord_ref_refact,ls_orl_lig,'-',ll_nb_col, ls_CDE_NB_PAL, ls_CDE_NB_COL,ls_EXP_NB_PAL, ls_EXP_NB_COL, ls_EXP_PDS_BRUT,
            ls_EXP_PDS_NET, ls_ACH_PU, ls_ACH_DEV_CODE,ls_ACH_BTA_CODE, ls_ACH_QTE, ldc_VTE_PU,ls_VTE_BTA_CODE, ls_VTE_QTE, ls_FOU_CODE,
            0, 0, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0,
            'N', 'N', 'N', 'N', 'N', 'O', ldc_frais_pu_ori, 'N', 'N', ls_bac_code, 0, 0, ls_art_ref_indemn , ls_ESP_CODE, 0, ls_ACH_DEV_TAUX, ls_ACH_DEV_PU,ls_PROPR_CODE,ll_pal_nb_inter_ori);
    exception when others then
        msg := '%%% Erreur à la création de la ligne d''ordre: ' || SQLERRM;
        res := 0;
        return;
    end;

    begin
        UPDATE GEO_ORDRE SET
            TOTVTE = TOTVTE + ls_TOTVTE,
            TOTCOL = TOTCOL + ls_CDE_NB_COL,
            TOTPDSNET = TOTPDSNET + ls_EXP_PDS_NET,
            TOTPDSBRUT = TOTPDSBRUT + ls_EXP_PDS_BRUT
        WHERE
            ORD_REF = arg_ord_ref_refact;
    exception when others then
        msg := '%%% Erreur mise à jour de l''ordre: ' || SQLERRM;
        res := 0;
        return;
    end;

    --
    ls_orx_ref :=  F_SEQ_ORX_SEQ();
    --
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
                arg_ord_ref_refact,
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
                where OL.ORD_REF = arg_ord_ref_refact and
                        OL.FOU_CODE = ls_FOU_CODE;
    exception when others then
        msg := '%%% Erreur mise à jour de l''ordre: ' || SQLERRM;
        res := 0;
        return;
    end;

	res := 1;
	msg := 'OK';

end;
/
