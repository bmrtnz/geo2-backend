CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_VERIF_ORDRE_BWS (
    arg_ref_edi_ordre IN GEO_EDI_ORDRE.REF_EDI_ORDRE%TYPE,
    arg_ref_edi_ligne IN GEO_CLIENT.CLI_REF%TYPE,
    arg_art_ref IN GEO_ARTICLE_COLIS.ART_REF%TYPE,
	arg_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
	arg_bws_ecris  IN P_STR_TAB_TYPE,

	arg_cli_ref GEO_CLIENT.CLI_REF%TYPE,
	arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
	arg_ean_prod_client GEO_EDI_LIGNE.EAN_PROD_CLIENT%TYPE,
	arg_vte_pu GEO_ORDLIG.VTE_PU%TYPE,
	arg_vte_bta_code GEO_ORDLIG.VTE_BTA_CODE%TYPE,
	arg_canal_cde GEO_EDI_ORDRE.CANAL_CDE%TYPE,
	arg_art_ref_client GEO_EDI_LIGNE.CODE_INTERNE_PROD_CLIENT%TYPE,
	arg_pal_code_entrep GEO_ENTREP.PAL_CODE%TYPE,

    res IN OUT number,
    msg IN OUT varchar2
)
AS

	--ls_cli_code GEO_CLIENT.CLI_CODE%TYPE;
	--ls_cen_code GEO_ENTREP.CEN_CODE%TYPE;
	--ls_esp_code GEO_ARTICLE_COLIS.ESP_CODE%TYPE;
	--ls_var_code GEO_ARTICLE_COLIS.VAR_CODE%TYPE;
	ls_ord_ref GEO_ORDLIG.ORD_REF%TYPE;
	ls_orl_ref GEO_ORDLIG.ORL_REF%TYPE;
    ll_cde_nb_col number;

	--cursor C_LIG_ORD_BWS(cam_code GEO_ORDRE.CAM_CODE%TYPE, art_ref GEO_ARTICLE_COLIS.ART_REF%TYPE)
	cursor C_LIG_ORD_BWS
	IS
		--select /*+ optimizer_features_enable('8.1.7) */ CDE_NB_COL, ORD_REF, ORL_REF
		--from VIEW_EDI_LIGNE_ORDRE
		--where soc_code = 'BWS'
		--and sco_code = 'F'
		--and cam_code = arg_cam_code
		--and art_ref = arg_art_ref
		--and livdatp = to_date(sysdate, 'dd/mm/YY')
		--and cen_code in ('ENTREPOT PCHANTEGR','ENTREPOT MOISSACDV','ENTREPOT TERRYLOIR','MOISSACDV NON CHEP');

		select /*+ optimizer_features_enable('8.1.7) */  L.cde_nb_col, L.ord_ref, L.orl_ref
		from geo_ordre O, geo_ordlig L
		where O.soc_code ='BWS'
		and O.sco_code = 'F'
		and O.cam_code = arg_cam_code
		and O.ord_ref = L.ord_ref
		and O.TYP_ORDRE = 'ORD'
		and O.FLANNUL = 'N'
		and L.art_ref = arg_art_ref
		and O.livdatp = to_date(sysdate, 'dd/mm/YY')
		and cen_code in ('ENTREPOT PCHANTEGR','ENTREPOT MOISSACDV','ENTREPOT TERRYLOIR','MOISSACDV NON CHEP');



BEGIN
    -- correspond à of_verif_ordre_bws.pbl
    res := 0;
    msg := '';

	begin
		for lig_ord_bws IN C_LIG_ORD_BWS
		loop
			ll_cde_nb_col	:= lig_ord_bws.cde_nb_col;
			ls_ord_ref		:= lig_ord_bws.ord_ref;
			ls_orl_ref		:= lig_ord_bws.orl_ref;
			if ll_cde_nb_col > 0 then
				declare
                    bws_ecris P_STR_TAB_TYPE := arg_bws_ecris;
                    save_res number;
                begin
				    F_SAUVE_ORD_BWS(ls_ord_ref, ls_orl_ref, arg_ref_edi_ordre, arg_ref_edi_ligne, arg_art_ref, arg_cam_code, bws_ecris, arg_cli_ref, arg_cen_ref, arg_ean_prod_client, arg_vte_pu, arg_vte_bta_code, arg_canal_cde, arg_art_ref_client, arg_pal_code_entrep, save_res, msg);
                    if save_res = 0 then
                        res := 0;
						close C_LIG_ORD_BWS;
                        return;
                    else
                        res := 1;
                    end if;
				end;
			end if;
		end loop;
	end;

END F_VERIF_ORDRE_BWS;
/
