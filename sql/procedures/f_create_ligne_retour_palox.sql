CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_CREATE_LIGNE_RETOUR_PALOX(
    arg_ord_ref varchar2,
    arg_fourni varchar2,
    arg_palox varchar2,
    arg_nbr_pallox number,
    arg_is_regulation boolean,
    res IN OUT number,
    msg IN OUT varchar2,
    ls_orl_ref IN OUT varchar2
)
AS
    ls_ORD_REF varchar2(50);
    ls_CDE_NB_COL varchar2(50);
    ls_EXP_NB_COL varchar2(50);
    ls_EXP_PDS_BRUT varchar2(50);
    ls_EXP_PDS_NET varchar2(50);
    ls_ACH_PU varchar2(50);
    ls_ACH_DEV_CODE varchar2(50);
    ls_ACH_QTE varchar2(50);
    ls_VTE_PU varchar2(50);
    ls_VTE_QTE varchar2(50);
    ls_FOU_CODE varchar2(50);
    ls_TOTVTE varchar2(50);
    ls_TOTACH varchar2(50);
    ls_TOTMOB varchar2(50);
    ls_ART_REF varchar2(50);
    ls_ESP_CODE varchar2(50);
    ls_ACH_DEV_TAUX varchar2(50);
    ls_ACH_DEV_PU varchar2(50);
    ls_IND_PALOX_GRATUIT varchar2(50);
    ls_PROPR_CODE varchar2(50);
    ls_IND_EXP varchar2(50);
    arrayval p_str_tab_type := p_str_tab_type();

    ll_EXP_PDS_NET number;
    ll_ACH_PU number;
    ll_VTE_PU number;
BEGIN
    res := 0;
    msg := '';

    select F_SEQ_ORL_SEQ() into ls_ORL_REF FROM DUAL;
    --ls_ORL_REF = SQLCA.F_SEQ_ORL_SEQ()
    --ls_ORL_REF = ''

    begin
        SELECT
            A.ART_REF, A.PDNET_CLIENT, E.PU_ACHAT, E.PU_VENTE
        INTO
            ls_ART_REF, ll_EXP_PDS_NET, ll_ACH_PU, ll_VTE_PU
        FROM
            GEO_ARTICLE A, GEO_COLIS E
        WHERE
            A.ESP_CODE = 'EMBALL' AND A.VAR_CODE = 'PALLOX' AND A.GER_CODE = 'B' AND A.COL_CODE = E.COL_CODE AND A.ESP_CODE = E.ESP_CODE AND E.SUIVI_PALLOX = 'O' AND E.COL_CODE = arg_palox;
    exception when others then
        msg := 'Erreur durant la création de la ligne de retour palox: ' || SQLERRM;
        res := 0;
        return;
    end;

    if arg_is_regulation = true then
        ll_ACH_PU := 0;
        ll_VTE_PU := 0;
    Else
        select IND_PALOX_GRATUIT into ls_IND_PALOX_GRATUIT
        from GEO_CLIENT
        where exists
            (select 1
            from GEO_ORDRE
            where GEO_ORDRE.ORD_REF = arg_ord_ref and
                    GEO_ORDRE.CLI_CODE = GEO_CLIENT.CLI_CODE) and
                    ROWNUM = 1;

        If ls_IND_PALOX_GRATUIT ='O' then
            ll_ACH_PU := 0;
            ll_VTE_PU := 0;
        end If;
    end if;

    ls_ORD_REF := arg_ord_ref;
    ls_CDE_NB_COL := to_char(arg_nbr_pallox * (-1));
    ls_EXP_NB_COL := to_char(arg_nbr_pallox * (-1));

    ls_EXP_PDS_BRUT := to_char(ll_EXP_PDS_NET * arg_nbr_pallox * (-1));
    ls_EXP_PDS_NET := to_char(ll_EXP_PDS_NET * arg_nbr_pallox * (-1));

    ls_ACH_DEV_CODE := 'EUR';
    ls_ACH_QTE := to_char(arg_nbr_pallox * (-1));
    ls_VTE_PU := to_char(ll_VTE_PU);
    ls_VTE_QTE := to_char(arg_nbr_pallox * (-1));
    ls_FOU_CODE := arg_fourni;
    ls_TOTVTE := to_char(ll_VTE_PU * arg_nbr_pallox * (-1));
    ls_TOTACH := to_char(ll_ACH_PU * arg_nbr_pallox * (-1));
    ls_TOTMOB := '0';
    -- ls_ART_REF = ''
    ls_ESP_CODE := 'EMBALL';
    ls_ACH_PU := to_char(ll_ACH_PU);
    ls_ACH_DEV_TAUX := '1';
    ls_ACH_DEV_PU := to_char(ll_ACH_PU);

    SELECT IND_EXP INTO ls_IND_EXP FROM GEO_FOURNI WHERE FOU_CODE = ls_FOU_CODE;

    if ls_IND_EXP is not null and ls_IND_EXP = 'F' THEN
        SELECT PROP_CODE INTO ls_PROPR_CODE FROM GEO_FOURNI WHERE FOU_CODE = ls_FOU_CODE;
        f_split(ls_PROPR_CODE, ',', arrayval);
        ls_PROPR_CODE := arrayval(1);
    else
        ls_PROPR_CODE := ls_FOU_CODE;
    end if;

    begin
        INSERT INTO GEO_ORDLIG (
            ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL, EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX, REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, PROPR_CODE
        ) VALUES (
            ls_ORL_REF, ls_ORD_REF, '01', 'DEPAL', 0, 0, ls_CDE_NB_COL, 0, ls_EXP_NB_COL, ls_EXP_PDS_BRUT, ls_EXP_PDS_NET, ls_ACH_PU, ls_ACH_DEV_CODE, 'COLIS', ls_ACH_QTE, ls_VTE_PU, 'COLIS', ls_VTE_QTE, ls_FOU_CODE, 0, 0, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0, 'N', 'N', 'N', 'N', 'N', 'O', 0, 'N', 'N', 'UDC', 0, 0, ls_ART_REF, ls_ESP_CODE, 0, ls_ACH_DEV_TAUX, ls_ACH_DEV_PU, ls_PROPR_CODE
        );
    exception when others then
        msg := '%%% Erreur à la création de la ligne d~''ordre: ' || SQLERRM;
        return;
    end;

    begin
        UPDATE GEO_ORDRE SET
            TOTVTE = ls_TOTVTE,
            TOTACH = ls_TOTACH,
            TOTMOB = ls_TOTMOB,
            TOTCOL = ls_CDE_NB_COL,
            TOTPDSNET = ls_EXP_PDS_NET,
            TOTPDSBRUT = ls_EXP_PDS_BRUT
        WHERE
            ORD_REF = arg_ord_ref;
    exception when others then
        msg := '%%% Erreur mise à jour de l~''ordre: ' || SQLERRM;
    end;

    res := 1;

end;
/

