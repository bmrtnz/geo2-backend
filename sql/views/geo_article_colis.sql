CREATE OR REPLACE FORCE VIEW "GEO_ADMIN"."GEO_ARTICLE_COLIS"
        (ART_REF, ART_ALPHA, PLU_CODE, GTIN_UC, GTIN_COLIS,
         GTIN_PALETTE, GTIN_UC_BW, GTIN_COLIS_BW, GTIN_PALETTE_BW, LF_EAN_ACHETEUR,
         MDD, PDE_CLIART, COM_CLIENT, INS_SECCOM, INS_STATION,
         CRE_USER, CRE_DATE, MOD_USER, MOD_DATE, VALIDE,
         PDE_REF, PCA_REF, ESP_CODE, ESP_DESC, VAR_CODE,
         VAR_DESC, ORI_CODE, ORI_DESC, ORI_LIBVTE, CUN_CODE,
         CUN_DESC, CAF_CODE, CAF_DESC, CAM_CODE, CAM_DESC,
         CAT_CODE, CAT_DESC, CAT_LIBVTE, CLR_CODE, CLR_DESC,
         CLR_LIBVTE, SUC_CODE, SUC_DESC, SUC_LIBVTE, PEN_CODE,
         PEN_DESC, PEN_LIBVTE, ETF_CODE, ETF_DESC, ETF_LIBVTE,
         MAQ_CODE, MAQ_DESC, MAQ_LIBVTE, CIR_CODE, CIR_DESC,
         CIR_LIBVTE, RAN_CODE, RAN_DESC, RAN_LIBVTE, ETC_CODE,
         ETC_DESC, ETC_LIBVTE, ETP_CODE, ETP_DESC, ETP_LIBVTE,
         IDS_CODE, IDS_DESC, IDS_LIBVTE, COS_CODE, COS_DESC,
         COS_LIBVTE, ALV_CODE, ALV_DESC, ALV_LIBVTE, ETV_CODE,
         ETV_DESC, ETV_LIBVTE, TVT_CODE, COL_CODE, COL_DESC,
         COL_LIBLONG, GEM_CODE, COL_DIM, COL_PREPESE, COL_NORMALI,
         COL_COMMENT, COL_HMAXPAL, COL_PUMP, COL_PUMO, COL_PDNET,
         COL_TARE, COL_XB, COL_XH, COL_YB, COL_YH,
         COL_ZB, COL_ZH, COL_MOD_USER, COL_MOD_DATE, COL_VALIDE,
         PMB_CODE, EMBADIF_COL_ART, EMB_CONSIGNE, GEST_CODE, PMB_PER_COM,
         GEST_REF, GEM_DESC, PMB_DESC, PMB_LIBVTE, PMB_DIM,
         PMB_TARE, PMB_COUT_MP, EMBADIF_PMB_ART, GRV_CODE, GEN_CODE,
         CUG_CODE, BWSTOCK, U_PAR_COLIS, CCW_CODE, UC_PDNET_GARANTI,
         GER_CODE, ART_REF_ASS, TYP_CODE, TYP_DESC, CEE,
         QTE_UC, BTA_UC, MODE_CULTURE, MODE_CULTURE_DESC, PEN_MIN,
         PEN_MAX, PEN_MOY, SUC_MIN, SUC_MOY, PDNET_CLIENT, PDNET_COLIS,
         IND_MODIF_DETAIL)
AS
SELECT geo_article.art_ref,
       geo_article.art_alpha,
       geo_article.plu_code,
       geo_article.gtin_uc,
       geo_article.gtin_colis,
       geo_article.gtin_palette,
       geo_article.gtin_uc_bw,
       geo_article.gtin_colis_bw,
       geo_article.gtin_palette_bw,
       geo_article.lf_ean_acheteur,
       geo_article.mdd,
       geo_article.pde_cliart,
       geo_article.com_client,
       geo_article.ins_seccom,
       geo_article.ins_station,
       geo_article.cre_user,
       geo_article.cre_date,
       geo_article.mod_user,
       geo_article.mod_date,
       geo_article.valide,
       geo_article.pde_ref,
       geo_article.pca_ref,
       geo_article.esp_code,
       geo_espece.esp_desc,
       geo_article.var_code,
       geo_variet.var_desc,
       geo_article.ori_code,
       geo_origine.ori_desc,
       geo_origine.ori_libvte,
       geo_article.cun_code,
       geo_caluni.cun_desc,
       geo_article.caf_code,
       geo_calfou.caf_desc,
       geo_article.cam_code,
       geo_calmar.cam_desc,
       geo_article.cat_code,
       geo_catego.cat_desc,
       geo_catego.cat_libvte,
       geo_article.clr_code,
       geo_colora.clr_desc,
       geo_colora.clr_libvte,
       geo_article.suc_code,
       geo_sucre.suc_desc,
       geo_sucre.suc_libvte,
       geo_article.pen_code,
       geo_penetro.pen_desc,
       geo_penetro.pen_libvte,
       geo_article.etf_code,
       geo_etifru.etf_desc,
       geo_etifru.etf_libvte,
       geo_article.maq_code,
       geo_marque.maq_desc,
       geo_marque.maq_libvte,
       geo_article.cir_code,
       geo_cirage.cir_desc,
       geo_cirage.cir_libvte,
       geo_article.ran_code,
       geo_rangem.ran_desc,
       geo_rangem.ran_libvte,
       geo_article.etc_code,
       geo_eticol.etc_desc,
       geo_eticol.etc_libvte,
       geo_article.etp_code,
       geo_etipmb.etp_desc,
       geo_etipmb.etp_libvte,
       geo_article.ids_code,
       geo_idsymb.ids_desc,
       geo_idsymb.ids_libvte,
       geo_article.cos_code,
       geo_conspe.cos_desc,
       geo_conspe.cos_libvte,
       geo_article.alv_code,
       geo_alveol.alv_desc,
       geo_alveol.alv_libvte,
       geo_article.etv_code,
       geo_etievt.etv_desc,
       geo_etievt.etv_libvte,
       geo_article.tvt_code,
       geo_colis.col_code,
       geo_colis.col_desc,
       geo_colis.col_liblong,
       geo_colis.gem_code,
       geo_colis.col_dim,
       geo_article.col_prepese,
       geo_colis.col_normali,
       geo_colis.col_comment,
       geo_colis.col_hmaxpal,
       geo_colis.col_pump,
       geo_colis.col_pumo,
       geo_article.col_pdnet,
       geo_colis.col_tare,
       geo_colis.col_xb,
       geo_colis.col_xh,
       geo_colis.col_yb,
       geo_colis.col_yh,
       geo_colis.col_zb,
       geo_colis.col_zh,
       geo_colis.mod_user,
       geo_colis.mod_date,
       geo_colis.valide,
       geo_colis.pmb_code,
       geo_colis.embadif_col_art,
       geo_colis.emb_consigne,
       geo_colis.gest_code,
       geo_article.u_par_colis,
       geo_colis.gest_ref,
       geo_grpemb.gem_desc,
       geo_preemb.pmb_desc,
       geo_preemb.pmb_libvte,
       geo_preemb.pmb_dim,
       geo_preemb.pmb_tare,
       geo_preemb.pmb_cout_mp,
       geo_preemb.embadif_pmb_art,
       geo_variet.grv_code,
       geo_espece.gen_code,
       geo_caluni.cug_code,
       geo_article.bwstock,
       geo_article.u_par_colis,
       geo_catego.ccw_code,
       geo_article.uc_pdnet_garanti,
       geo_article.ger_code,
       geo_article.art_ref_ass,
       geo_typ.typ_code,
       geo_typ.typ_desc,
       geo_origine.cee,
       geo_preemb.qte_uc,
       geo_preemb.bta_uc,
       geo_article.mode_culture,
       geo_mode_culture.libelle,
       geo_penetro.pen_min,
       geo_penetro.pen_max,
       geo_penetro.pen_moy,
       geo_sucre.suc_min,
       geo_sucre.suc_moy,
       geo_article.pdnet_client,
       geo_article.col_pdnet,
       geo_variet.ind_modif_detail
FROM geo_article,
     geo_espece,
     geo_variet,
     geo_origine,
     geo_caluni,
     geo_calfou,
     geo_calmar,
     geo_catego,
     geo_colora,
     geo_sucre,
     geo_penetro,
     geo_etifru,
     geo_marque,
     geo_cirage,
     geo_rangem,
     geo_eticol,
     geo_etipmb,
     geo_idsymb,
     geo_conspe,
     geo_alveol,
     geo_etievt,
     geo_colis,
     geo_grpemb,
     geo_preemb,
     geo_typ,
     geo_mode_culture
WHERE     (geo_espece.esp_code(+) = geo_article.esp_code)
  AND (geo_variet.var_code(+) = geo_article.var_code)
  AND (geo_origine.esp_code(+) = geo_article.esp_code)
  AND (geo_origine.ori_code(+) = geo_article.ori_code)
  AND (geo_caluni.esp_code(+) = geo_article.esp_code)
  AND (geo_caluni.cun_code(+) = geo_article.cun_code)
  AND (geo_calfou.esp_code(+) = geo_article.esp_code)
  AND (geo_calfou.caf_code(+) = geo_article.caf_code)
  AND (geo_calmar.esp_code(+) = geo_article.esp_code)
  AND (geo_calmar.cam_code(+) = geo_article.cam_code)
  AND (geo_catego.esp_code(+) = geo_article.esp_code)
  AND (geo_catego.cat_code(+) = geo_article.cat_code)
  AND (geo_colora.esp_code(+) = geo_article.esp_code)
  AND (geo_colora.clr_code(+) = geo_article.clr_code)
  AND (geo_sucre.esp_code(+) = geo_article.esp_code)
  AND (geo_sucre.suc_code(+) = geo_article.suc_code)
  AND (geo_penetro.esp_code(+) = geo_article.esp_code)
  AND (geo_penetro.pen_code(+) = geo_article.pen_code)
  AND (geo_etifru.esp_code(+) = geo_article.esp_code)
  AND (geo_etifru.etf_code(+) = geo_article.etf_code)
  AND (geo_marque.esp_code(+) = geo_article.esp_code)
  AND (geo_marque.maq_code(+) = geo_article.maq_code)
  AND (geo_cirage.esp_code(+) = geo_article.esp_code)
  AND (geo_cirage.cir_code(+) = geo_article.cir_code)
  AND (geo_rangem.esp_code(+) = geo_article.esp_code)
  AND (geo_rangem.ran_code(+) = geo_article.ran_code)
  AND (geo_colis.esp_code(+) = geo_article.esp_code)
  AND (geo_colis.col_code(+) = geo_article.col_code)
  AND (geo_eticol.esp_code(+) = geo_article.esp_code)
  AND (geo_eticol.etc_code(+) = geo_article.etc_code)
  AND (geo_etipmb.esp_code(+) = geo_article.esp_code)
  AND (geo_etipmb.etp_code(+) = geo_article.etp_code)
  AND (geo_idsymb.esp_code(+) = geo_article.esp_code)
  AND (geo_idsymb.ids_code(+) = geo_article.ids_code)
  AND (geo_conspe.esp_code(+) = geo_article.esp_code)
  AND (geo_conspe.cos_code(+) = geo_article.cos_code)
  AND (geo_alveol.esp_code(+) = geo_article.esp_code)
  AND (geo_alveol.alv_code(+) = geo_article.alv_code)
  AND (geo_etievt.esp_code(+) = geo_article.esp_code)
  AND (geo_etievt.etv_code(+) = geo_article.etv_code)
  AND (geo_preemb.esp_code(+) = geo_colis.esp_code)
  AND (geo_preemb.pmb_code(+) = geo_colis.pmb_code)
  AND (geo_grpemb.esp_code(+) = geo_colis.esp_code)
  AND (geo_grpemb.gem_code(+) = geo_colis.gem_code)
  AND (geo_typ.typ_ref(+) = geo_article.typ_ref)
  AND geo_mode_culture.REF = geo_article.mode_culture;
/
