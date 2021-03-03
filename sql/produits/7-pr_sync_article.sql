CREATE OR REPLACE PROCEDURE GEO_ADMIN.sync_article (avi_art_ref in varchar2)
    IS
    geo_art_found NUMBER;
BEGIN

    -- Search GEO_ARTICLE.ART_REF
    SELECT COUNT(*)
    INTO geo_art_found
    FROM GEO_ARTICLE
    WHERE ART_REF = avi_art_ref;

    -- Initial sync (with required fields)
    IF geo_art_found = 0
    THEN
        INSERT INTO GEO_ARTICLE (
            ART_REF,
            AUTEUR_DDE,
            ESP_CODE,
            CAT_CODE,
            CLR_CODE,
            COL_CODE,
            VAR_CODE,
            ORI_CODE,
            CUN_CODE,
            CAM_CODE,
            ETF_CODE,
            CAF_CODE,
            CRE_DATE,
            CRE_USER
        )  SELECT
               avi_art_ref,
               '---', -- AUTEUR_DDE
               aac.ESP_CODE,
               aac.CAT_CODE,
               aac.CLR_CODE,
               aae.COL_CODE,
               aam.VAR_CODE,
               aam.ORI_CODE,
               aam.CUN_CODE,
               aan.CAM_CODE,
               aan.ETF_CODE,
               '---', -- CAF_CODE (calibre fournisseur)
               aam.CRE_DATE,
               aam.CRE_USER
        FROM AVI_ART_GESTION aag
                 LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc
                 LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage
                 LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem
                 LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation
        WHERE aag.ART_REF = avi_art_ref;
    END IF;

    -- Update sync (with all fields)
    UPDATE GEO_ARTICLE
    SET
        -- Cahier des charges
        ESP_CODE = (SELECT ESP_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        CAT_CODE = (SELECT CAT_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        CLR_CODE = (SELECT CLR_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        SUC_CODE = (SELECT SUC_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        PEN_CODE = (SELECT PEN_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        CIR_CODE = (SELECT CIR_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        RAN_CODE = (SELECT RAN_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        INS_STATION = (SELECT aac.INS_STATION FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        INS_SECCOM = (SELECT aac.INS_SECCOM FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        -- Emballage
        COL_CODE = (SELECT COL_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage WHERE aag.ART_REF = avi_art_ref),
        COS_CODE = (SELECT COS_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage WHERE aag.ART_REF = avi_art_ref),
        ALV_CODE = (SELECT ALV_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage WHERE aag.ART_REF = avi_art_ref),
        U_PAR_COLIS = (SELECT U_PAR_COLIS FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage WHERE aag.ART_REF = avi_art_ref),
        COL_PDNET = (SELECT COL_PDNET FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage WHERE aag.ART_REF = avi_art_ref),
        UC_PDNET_GARANTI = (SELECT UC_PDNET_GARANTI FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage WHERE aag.ART_REF = avi_art_ref),
        COL_PREPESE = (SELECT COL_PREPESE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage WHERE aag.ART_REF = avi_art_ref),
        -- Matiere premiere
        VAR_CODE = (SELECT VAR_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        ORI_CODE = (SELECT ORI_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        CUN_CODE = (SELECT CUN_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        TYP_REF  = (SELECT TYP_REF FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        MODE_CULTURE = (SELECT MODE_CULTURE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        TVT_CODE  = (SELECT TVT_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        PLU_CODE  = (SELECT PLU_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        TYP_CODE  = (SELECT TYP_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        -- Normalisation
        CAM_CODE = (SELECT CAM_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        ETF_CODE = (SELECT ETF_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        ETC_CODE = (SELECT ETC_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        ETP_CODE = (SELECT ETP_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        ETV_CODE = (SELECT ETV_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        MAQ_CODE = (SELECT MAQ_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        GTIN_UC = (SELECT GTIN_UC FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        GTIN_COLIS = (SELECT GTIN_COLIS FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        GTIN_PALETTE = (SELECT GTIN_PALETTE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        PDE_CLIART = (SELECT PDE_CLIART FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        COM_CLIENT = (SELECT COM_CLIENT FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        IDS_CODE = (SELECT IDS_CODE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        MDD = (SELECT MDD FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        --- Autres
        REF_CDC = (SELECT aac.REF_CDC FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_CDC aac ON aag.ref_cdc = aac.ref_cdc WHERE aag.ART_REF = avi_art_ref),
        REF_EMBALLAGE = (SELECT aae.REF_EMBALLAGE FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_EMBALLAGE aae ON aag.ref_emballage = aae.ref_emballage WHERE aag.ART_REF = avi_art_ref),
        REF_MAT_PREM = (SELECT aam.REF_MAT_PREM FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_MAT_PREM aam ON aag.ref_mat_prem = aam.ref_mat_prem WHERE aag.ART_REF = avi_art_ref),
        REF_NORMALISATION = (SELECT aan.REF_NORMALISATION FROM AVI_ART_GESTION aag LEFT JOIN AVI_ART_NORMALISATION aan ON aag.ref_normalisation = aan.ref_normalisation WHERE aag.ART_REF = avi_art_ref),
        ART_ALPHA = (SELECT ART_ALPHA FROM AVI_ART_GESTION WHERE ART_REF = avi_art_ref),
        BWSTOCK = (SELECT BWSTOCK FROM AVI_ART_GESTION WHERE ART_REF = avi_art_ref),
        VALIDE = (SELECT VALIDE FROM AVI_ART_GESTION WHERE ART_REF = avi_art_ref),
        MOD_DATE = (SELECT MOD_DATE FROM AVI_ART_GESTION WHERE ART_REF = avi_art_ref),
        MOD_USER = (SELECT MOD_USER FROM AVI_ART_GESTION WHERE ART_REF = avi_art_ref)
    WHERE ART_REF = avi_art_ref;

    -- Mark original as synced
    UPDATE AVI_ART_GESTION
    SET SYNC_STAMP = (SELECT CURRENT_DATE FROM DUAL)
    WHERE ART_REF = avi_art_ref;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        -- Mark original as not synced
        UPDATE AVI_ART_GESTION
        SET SYNC_STAMP = NULL
        WHERE ART_REF = avi_art_ref;

        COMMIT;

        DBMS_OUTPUT.PUT_LINE('Erreur de synchronisation de l''article ' || avi_art_ref || CHR(10)
            || SQLERRM);

END;
