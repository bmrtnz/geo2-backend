CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CREE_ORDRE_DUPLIQUE_LIGNE" (
    arg_ord_ref_ori IN GEO_ORDRE.ORD_REF%TYPE,
    arg_ord_ref_dupliq IN GEO_ORDRE.ORD_REF%TYPE,
    arg_station IN char,
    arg_ach_pu IN char,
    arg_vte_pu IN char,
    arg_dluo IN char,
	res out number,
    msg out varchar2,
    d_ligne IN OUT varchar2
)
AS
    ls_orl_ref varchar2(50);
    ls_orl_lig varchar2(50);
    ll_ACH_PU number := 0;
    ll_nb_col number;
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
    ld_VTE_PU number;
    ls_TOTVTE varchar2(50);
    ls_TOTACH varchar2(50);
    ls_TOTMOB varchar2(50);
    ls_ACH_PU varchar2(50);
    ld_ACH_PU number;
    ls_ach_dev_taux varchar2(50);
    ls_ACH_DEV_PU varchar2(50);
    ld_ACH_DEV_PU number;

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
    ls_null varchar2(50);
    ld_ACH_DEV_TAUX number;

    ls_orl_ref_ori varchar2(50);
    ls_lib_dlv varchar2(50);
    ls_art_ref_kit varchar2(50);
    ls_gtin_colis_kit varchar2(50);
    ls_frais_unite varchar2(50);
    ls_list_certifs GEO_ORDLIG.LIST_CERTIFS%TYPE;
    ls_cert_origine GEO_ORDLIG.CERT_ORIGINE%TYPE;

    ls_var_code GEO_ARTICLE.var_code%type;
    ls_cat_code GEO_ARTICLE.cat_code%type;
    ls_ori_code GEO_ARTICLE.ori_code%type;
    ll_mode_culture GEO_ARTICLE.mode_culture%type;
    ls_sco_code GEO_ORDRE.sco_code%type;
    ls_tvt_code GEO_ORDRE.tvt_code%type;
    ls_soc_code GEO_ORDRE.soc_code%type;
    ld_dev_tx GEO_ORDRE.dev_tx%type;

    CURSOR C_ligne IS
        select ORL_REF
        FROM GEO_ORDLIG
        where ORD_REF = arg_ord_ref_ori;

begin

	res := 0;
	msg := '';

    for r in C_ligne
    loop
        /* RECUPERATION DES INFORMATIONS */

            select F_SEQ_ORL_SEQ() into ls_orl_ref FROM DUAL;


            --recherche des informations sur la ligne de l'ordre d'origine
            select  C.col_tare,X.esp_code,OL.fou_code ,OL.pal_code,OL.art_ref,OL.pal_nb_palinter,X.mdd,V.var_ristourne ,OL.propr_code, OL.bac_code, X.u_par_colis,OL.ach_bta_code,OL.vte_bta_code,OL.ACH_DEV_CODE,OL.ACH_DEV_TAUX,
            ACH_PU,ACH_DEV_PU,VTE_PU,LIB_DLV,OL.FRAIS_PU,OL.REMSF_TX,OL.REMHF_TX,OL.ART_REF_KIT,OL.GTIN_COLIS_KIT,ORL_LIG, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL,OL.FRAIS_UNITE, OL.LIST_CERTIFS, OL.cert_origine,
            X.var_code, X.cat_code, X.mode_culture, X.ori_code
            into  ld_col_tare, ls_ESP_CODE,ls_fou_code,ls_pal_code_ori,ls_art_ref_ori,ll_pal_nb_inter_ori,ls_mdd,ls_var_ristourne,ls_PROPR_CODE,ls_bac_code,ld_pmb_per_com,ls_ach_bta_code,ls_vte_bta_code,ls_ACH_DEV_CODE,ld_ACH_DEV_TAUX,
            ld_ACH_PU, ld_ACH_DEV_PU,ld_VTE_PU,ls_lib_dlv,ldc_frais_pu_ori,ldc_remsf_tx,ldc_remhf_tx,ls_art_ref_kit,ls_gtin_colis_kit,ls_orl_lig,ll_nb_col, ls_CDE_NB_PAL, ls_CDE_NB_COL,ls_frais_unite, ls_list_certifs, ls_cert_origine,
            ls_var_code, ls_cat_code, ll_mode_culture, ls_ori_code
            from geo_article X, geo_colis C, geo_ordlig OL, GEO_VARIET V
                    where 	OL.orl_ref =r.orl_ref 	and
                                OL.art_ref = X.art_ref 			and
                                C.esp_code = X.esp_code 		and
                                C.col_code = X.col_code and
                                X.esp_code = V.esp_code and
                                X.var_code = V.var_code;

            select dev_tx, sco_code, tvt_code
            into ld_dev_tx, ls_sco_code, ls_tvt_code
            from geo_ordre
            where ord_ref = arg_ord_ref_ori;

            ls_EXP_NB_COL:='0';
            ls_EXP_NB_PAL:= '0';
            ls_EXP_PDS_NET:= '0';
            ls_EXP_PDS_BRUT:= '0';

            If arg_station = 'N' THEN
                ls_PROPR_CODE := null;
                ls_fou_code := null;
            END IF;

            If arg_ach_pu = 'N' THEN

                -- gestion pu mini pour la variété club
                declare
                    key_frais number;
                    msg_frais clob;
                    attrib_frais geo_attrib_frais%rowtype;
                begin
                    f_recup_frais(ls_var_code, ls_cat_code, ls_sco_code, ls_tvt_code, ll_mode_culture, ls_ori_code, key_frais, msg_frais);
                    select * into attrib_frais from geo_attrib_frais where k_frais = key_frais and valide = 'O';
                    if attrib_frais.perequation = 'O' then
                        ls_ACH_DEV_PU 	:= attrib_frais.accompte * ld_dev_tx;
                        ls_ACH_PU 			:=  attrib_frais.accompte;
                        ls_ach_bta_code  	:=  attrib_frais.FRAIS_UNITE;
                    end if;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    ls_ACH_DEV_PU 	:=  null;
                    ls_ACH_PU 			:=  null;
                    ls_ach_bta_code  	:=  null;
                end;

            ELSE
                ls_ACH_DEV_PU := to_char(ld_ACH_DEV_PU);
                ls_ACH_PU := to_char(ld_ACH_PU);
            END IF;

            If arg_vte_pu = 'N' THEN
                ls_vte_PU 			:=  null;
                ls_vte_bta_code  	:=  null;
            ELSE
                ls_vte_PU := to_char(ld_VTE_PU);
            END IF;

            If arg_dluo = 'N' THEN
                ls_lib_dlv := null;
            End If;

        ll_nb_col:=0;
        ls_CDE_NB_PAL:='0';
        ls_CDE_NB_COL:='0';
        ll_pal_nb_inter_ori:=0;

        begin
            INSERT INTO GEO_ORDLIG (
                ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL,EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT,
                EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE,
                CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT,
                FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX,
                REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU,PROPR_CODE,PAL_NB_PALINTER,LIB_DLV,
                FRAIS_UNITE, LIST_CERTIFS, CERT_ORIGINE)
            VALUES (
                ls_orl_ref, arg_ord_ref_dupliq, ls_orl_lig,ls_pal_code_ori,ll_nb_col, ls_CDE_NB_PAL, ls_CDE_NB_COL, ls_EXP_NB_PAL, ls_EXP_NB_COL, ls_EXP_PDS_BRUT,
                ls_EXP_PDS_NET, ls_ACH_PU, ls_ACH_DEV_CODE,ls_ACH_BTA_CODE, ld_ACH_QTE, ls_VTE_PU,ls_VTE_BTA_CODE, ld_VTE_QTE, ls_FOU_CODE,
                0, 0, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0,
                'N', 'N', 'N', 'N', 'N', ls_var_ristourne, ldc_frais_pu_ori, 'N', 'N', ls_bac_code,ldc_remsf_tx, ldc_remhf_tx, ls_ART_REF_ori , ls_ESP_CODE, 0, ld_ACH_DEV_TAUX, ls_ACH_DEV_PU,ls_PROPR_CODE,ll_pal_nb_inter_ori,ls_lib_dlv,
                ls_frais_unite,ls_list_certifs,ls_cert_origine);
        exception when others then
            msg := 'erreur creation ' || SQLERRM;
            rollback;
            d_ligne := '%%%';
            return;
        end;
    end loop;

	res := 1;
	return;

end;
/

