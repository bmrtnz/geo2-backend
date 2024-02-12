CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CRE_ORD_PAL_PR_LIG_PAL_V2 (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_list_orl_ref IN VARCHAR,
    res IN OUT number,
    msg IN OUT varchar2,
    ls_nordre_pallox OUT GEO_ORDRE.NORDRE%TYPE
)
AS
    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;

    ls_cli_ref GEO_CLIENT.CLI_REF%TYPE;
    ls_cli_ref_palox GEO_CLIENT.CLI_REF_PALOX%TYPE;
    ls_palox_gratuit GEO_CLIENT.IND_PALOX_GRATUIT%TYPE;
    ls_nordre_ori GEO_ORDRE.NORDRE%TYPE;
    ls_cli_code_pallox GEO_CLIENT.CLI_CODE%TYPE;
    ls_cen_code_pallox GEO_ENTREP.CEN_CODE%TYPE;
    ls_dat_dep_pallox varchar(20);
    ls_transp GEO_ORDRE.TRP_CODE%TYPE;
    ls_ref_cli GEO_ORDRE.REF_CLI%TYPE;
    ls_ref varchar(70);

    ls_ord_ref_pallox GEO_ORDRE.ORD_REF%TYPE;
    --ls_nordre_pallox GEO_ORDRE.NORDRE%TYPE;
    ls_orl_ref_pallox GEO_ORDLIG.ORL_REF%TYPE;

    ls_sql clob;
    ls_fou_code GEO_ORDLIG.FOU_CODE%TYPE;
    ls_ART_REF GEO_ARTICLE.ART_REF%TYPE;
    ll_EXP_PDS_NET GEO_ARTICLE.PDNET_CLIENT%TYPE;
    ll_ACH_PU GEO_COLIS.PU_ACHAT%TYPE;
    ll_VTE_PU GEO_COLIS.PU_VENTE%TYPE;
    ls_bac_code GEO_FOURNI.BAC_CODE%TYPE;
    ll_nb_col number;

    ls_IND_EXP GEO_FOURNI.IND_EXP%TYPE;
    array_list_prop p_str_tab_type;

    ls_CDE_NB_COL GEO_ORDLIG.CDE_NB_COL%TYPE;
    ls_EXP_NB_COL GEO_ORDLIG.EXP_NB_COL%TYPE;
    ls_EXP_PDS_BRUT GEO_ORDLIG.EXP_PDS_BRUT%TYPE;
    ls_EXP_PDS_NET GEO_ORDLIG.EXP_PDS_NET%TYPE;
    ls_ACH_PU GEO_ORDLIG.ACH_PU%TYPE;
    ls_ACH_DEV_CODE GEO_ORDLIG.ACH_DEV_CODE%TYPE;
    ls_ACH_QTE GEO_ORDLIG.ACH_QTE%TYPE;
    ls_VTE_PU GEO_ORDLIG.VTE_PU%TYPE;
    ls_VTE_QTE GEO_ORDLIG.VTE_QTE%TYPE;
    ls_TOTVTE GEO_ORDLIG.TOTVTE%TYPE;
    ls_TOTACH GEO_ORDLIG.TOTACH%TYPE;
    ls_TOTMOB GEO_ORDLIG.TOTMOB%TYPE;
    ls_ESP_CODE GEO_ORDLIG.ESP_CODE%TYPE;
    ls_ACH_DEV_TAUX GEO_ORDLIG.ACH_DEV_TAUX%TYPE;
    ls_ACH_DEV_PU GEO_ORDLIG.ACH_DEV_PU%TYPE;
    ls_propr_code GEO_ORDLIG.PROPR_CODE%TYPE;

    ls_orx_ref GEO_ORDLOG.ORX_REF%TYPE;
BEGIN
    -- correspond à f_creation_ordre_palox_pr_ligne_palox_v2.pbl
    res := 0;
    msg := '';

    select DEV_CODE into ls_soc_dev_code FROM GEO_SOCIETE WHERE SOC_CODE = arg_soc_code;

    -- INFORMATION CLIENT
    select GEO_CLIENT.CLI_REF,
           GEO_CLIENT.CLI_REF_PALOX,
           GEO_CLIENT_PALLOX.IND_PALOX_GRATUIT,
           GEO_ORDRE.NORDRE,
           GEO_CLIENT_PALLOX.CLI_CODE,
           GEO_ENTREP_PALLOX.CEN_CODE,
           to_char(GEO_ORDRE.DEPDATP,'dd/mm/yy'),
           GEO_ORDRE.TRP_CODE,
           GEO_ORDRE.REF_CLI
    into 	ls_cli_ref,
        ls_cli_ref_palox,
        ls_palox_gratuit,
        ls_nordre_ori,
        ls_cli_code_pallox,
        ls_cen_code_pallox,
        ls_dat_dep_pallox,
        ls_transp,
        ls_ref_cli
    from 	GEO_CLIENT,
            GEO_ORDRE	,
            GEO_CLIENT GEO_CLIENT_PALLOX,
            GEO_ENTREP	GEO_ENTREP_PALLOX
    where 	GEO_ORDRE.ORD_REF = arg_ord_ref AND
            GEO_CLIENT.CLI_REF = GEO_ORDRE.CLI_REF AND
            GEO_CLIENT_PALLOX.CLI_REF = GEO_CLIENT.CLI_REF_PALOX AND
            GEO_CLIENT_PALLOX.CLI_REF  = GEO_ENTREP_PALLOX.CLI_REF and
            GEO_ENTREP_PALLOX.VALIDE ='O';

    If ls_cli_ref_palox is null or ls_cli_ref_palox = '' then
        msg := '%%% Erreur pas d entrepot palox';
        return;
    End If;

    If ls_ref_cli is null Then
        ls_ref_cli := '';
    end if;
    ls_ref := substr('OM ' || ls_nordre_ori || '/ ' || ls_ref_cli, 1, 70);
    if ls_transp = '' then
        ls_transp := '-';
    end if;

    -- llef mis à true du is_baf pourle mettre en bon à facturé
    F_CREATE_ORDRE_V2(
        arg_soc_code, ls_cli_code_pallox, ls_cen_code_pallox,
        ls_transp, ls_ref, false,
        true, ls_dat_dep_pallox,'ORD',
        res, msg, ls_ord_ref_pallox
    );

    if substr(msg, 1, 3) = '%%%' then
        rollback;
        return;
    else
        SELECT nordre INTO ls_nordre_pallox FROM GEO_ORDRE WHERE ord_ref = ls_ord_ref_pallox;
    end if;

    select F_SEQ_ORL_SEQ() into ls_orl_ref_pallox FROM DUAL;
    If ls_palox_gratuit is null Then
        ls_palox_gratuit := 'N';
    end if;

    -- INFORMATION LIGNE DE COMMANDE
    ls_sql := 'SELECT	OL.FOU_CODE, A.ART_REF, A.PDNET_CLIENT, E.PU_ACHAT, E.PU_VENTE, F.BAC_CODE, sum(OL.EXP_NB_COL)' ||
              ' FROM	GEO_ARTICLE A, GEO_COLIS E, GEO_ORDLIG	OL, GEO_ARTICLE A_OL, GEO_FOURNI F' ||
              ' WHERE	OL.ORL_REF  in (' || arg_list_orl_ref  || ') and ' ||
              ' OL.ART_REF	 = A_OL.ART_REF and ' ||
              '	A.ESP_CODE = ''EMBALL'' AND ' ||
              '	A.VAR_CODE = ''PALLOX'' AND ' ||
              '	A.GER_CODE = ''B'' AND ' ||
              '	A.COL_CODE = E.COL_CODE AND ' ||
              '	A.ESP_CODE = E.ESP_CODE AND ' ||
              '	E.SUIVI_PALLOX = ''O'' AND ' ||
              '	A.VALIDE = ''O'' AND ' ||
              '	E.COL_CODE = A_OL.COL_CODE AND' ||
              '	OL.FOU_CODE = F.FOU_CODE ' ||
              'group by OL.FOU_CODE, A.ART_REF, A.PDNET_CLIENT, E.PU_ACHAT, E.PU_VENTE, F.BAC_CODE';

    declare
        C_CREAT_LIGNE_PALLOX SYS_REFCURSOR;
    begin
        OPEN C_CREAT_LIGNE_PALLOX FOR to_char(ls_sql);
        FETCH C_CREAT_LIGNE_PALLOX into ls_fou_code, ls_ART_REF, ll_EXP_PDS_NET, ll_ACH_PU, ll_VTE_PU, ls_bac_code, ll_nb_col;
        CLOSE C_CREAT_LIGNE_PALLOX;
    end;

    If ls_palox_gratuit = 'O' Then
        ll_ACH_PU := 0;
        ll_VTE_PU := 0;
    End If;

    ls_CDE_NB_COL := to_char(ll_nb_col);
    ls_EXP_NB_COL := to_char(ll_nb_col);

    ls_EXP_PDS_BRUT := to_char(ll_EXP_PDS_NET * ll_nb_col);
    ls_EXP_PDS_NET := to_char(ll_EXP_PDS_NET * ll_nb_col);

    ls_ACH_DEV_CODE := ls_soc_dev_code;
    ls_ACH_QTE := to_char(ll_nb_col);
    ls_VTE_PU := to_char(ll_VTE_PU);
    ls_VTE_QTE := to_char(ll_nb_col);

    ls_TOTVTE := to_char(ll_VTE_PU * ll_nb_col);
    ls_TOTACH := to_char(ll_ACH_PU * ll_nb_col);
    ls_TOTMOB := '0';

    ls_ESP_CODE := 'EMBALL';
    ls_ACH_PU := to_char(ll_ACH_PU);
    ls_ACH_DEV_TAUX := '1';
    ls_ACH_DEV_PU := to_char(ll_ACH_PU);

    SELECT IND_EXP INTO ls_IND_EXP FROM GEO_FOURNI WHERE FOU_CODE = ls_FOU_CODE;

    if coalesce(ls_IND_EXP, '') = 'F' THEN
        SELECT PROP_CODE INTO ls_PROPR_CODE FROM GEO_FOURNI WHERE FOU_CODE = ls_FOU_CODE;
        -- f_string2array_sep( ',',ls_PROPR_CODE, arrayval);
        F_SPLIT(ls_PROPR_CODE, ',', array_list_prop);

        If array_list_prop.COUNT > 0 Then
            ls_PROPR_CODE := array_list_prop(1);
        Else
            msg := '%%% Aucune station propriétaire est définie pour ' || ls_FOU_CODE || ' , contacter le service informatique';
            return;
        End IF;
    else
        ls_PROPR_CODE := ls_FOU_CODE;
    end if;

    begin
        INSERT INTO GEO_ORDLIG (
            ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL,EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT,
            EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE,
            CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT,
            FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX,
            REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU,PROPR_CODE)
        VALUES (
           ls_orl_ref_pallox, ls_ord_ref_pallox, '01', 'DEPAL', 0, 0, ls_CDE_NB_COL, 0, ls_EXP_NB_COL, ls_EXP_PDS_BRUT,
           ls_EXP_PDS_NET, ls_ACH_PU, ls_ACH_DEV_CODE, 'COLIS', ls_ACH_QTE, ls_VTE_PU, 'COLIS', ls_VTE_QTE, ls_FOU_CODE,
           0, 0, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0,
           'N', 'N', 'N', 'N', 'N', 'O', 0, 'N', 'N', ls_bac_code, 0, 0, ls_ART_REF, ls_ESP_CODE, 0, ls_ACH_DEV_TAUX, ls_ACH_DEV_PU,ls_propr_code
        );
    exception when others then
        msg := '%%% Erreur à la création de la ligne d ordre : ' || SQLERRM;
        return;
    end;

    begin
        UPDATE GEO_ORDRE SET
             TOTVTE = ls_TOTVTE,
             TOTACH = ls_TOTACH,
             TOTMOB = ls_TOTMOB,
             TOTCOL = ls_CDE_NB_COL,
             TOTPDSNET = ls_EXP_PDS_NET,
             TOTPDSBRUT = ls_EXP_PDS_BRUT,
             ORD_REF_PALOX_PERE = arg_ord_ref
        WHERE
            ORD_REF = ls_ord_ref_pallox;
    exception when others then
        msg := '%%% Erreur mise à jour de l ordre : ' || SQLERRM;
        return;
    end;

    ls_orx_ref := F_SEQ_ORX_SEQ();

    -- INFORMATION LOGISTIQUE
    insert into GEO_ORDLOG (
         ORX_REF,
         ORD_REF,
         FOU_CODE,
         TRP_CODE,
         INSTRUCTIONS,
         FOU_REF_DOC,
         REF_LOGISTIQUE,
         FLAG_EXPED_FOURNNI,
         DATDEP_FOU_R,
         PLOMB,
         IMMATRICULATION,
         DETECTEUR_TEMP,
         CERTIF_CONTROLE,
         CERTIF_PHYTO,
         BILL_OF_LADING,
         CONTAINER,
         LOCUS_TRACE) -- LLEF rajout pour la côlture automatique
    select ls_orx_ref,
           ls_ord_ref_pallox,
           OL.FOU_CODE,
           OL.TRP_CODE,
           OL.INSTRUCTIONS,
           OL.FOU_REF_DOC,
           OL.REF_LOGISTIQUE,
           'O',-- mise à 'O' automatiquement
            sysdate, --forcer la date de clôture à la date du jour
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
				 OL.FOU_CODE = ls_FOU_CODE;

    begin
        UPDATE GEO_ORDRE SET
            ORD_REF_PALOX_LIST_FILS = ls_ord_ref_pallox || ';' || ORD_REF_PALOX_LIST_FILS
        WHERE
            ORD_REF = arg_ord_ref;
    exception when others then
        msg := '%%% Erreur mise à jour de l''ordre: ' || SQLERRM;
        return;
    end;

    -- commit pour avoir les enregistrements en base pour le calcul des marges
    commit;
    -- mise à jour des TOT... suite à la clôture automatique
    F_CALCUL_MARGE(ls_ord_ref_pallox, res, msg);
    if (res <> 1) then
        msg := '%%% problème sur le calcul de la marge ' || msg;
        return;
    else
        F_CALCUL_PEREQUATION(ls_ord_ref_pallox, arg_soc_code, res, msg);
        if (res <> 1) then
            msg := '%%% problème sur le calcul de la marge sur variété club ' || msg;
            return;
        end if;
    end if;

    res := 1;

END F_CRE_ORD_PAL_PR_LIG_PAL_V2;
/

