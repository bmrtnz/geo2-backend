CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_DETAILS_EXP_CLICK_MODIFIER (
	arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
	arg_orl_ref IN GEO_ORDLIG.ORL_REF%TYPE,
	arg_histo_orx_ref IN GEO_HISTO_MODIF_DETAIL.HISTO_ORX_REF%TYPE,
	res OUT NUMBER,
	msg OUT varchar2
)
IS
	ls_flbaf GEO_ORDRE.FLBAF%TYPE;
	ls_flfac GEO_ORDRE.FLFAC%TYPE;
	ldc_exp_nb_col_new GEO_HISTO_MODIF_DETAIL.EXP_NB_COL_NEW%TYPE;
	ldc_exp_pds_brut_new GEO_HISTO_MODIF_DETAIL.EXP_PDS_BRUT_NEW%TYPE;
	ldc_exp_pds_net_new GEO_HISTO_MODIF_DETAIL.EXP_PDS_NET_NEW%TYPE;
	ldc_exp_nb_pal_new GEO_HISTO_MODIF_DETAIL.EXP_NB_PAL_NEW%TYPE;
	ls_ach_bta_code GEO_ORDLIG.ACH_BTA_CODE%TYPE;
	ls_vte_bta_code GEO_ORDLIG.VTE_BTA_CODE%TYPE;
	ll_pmb_per_col NUMBER;
	ls_art_ref GEO_ARTICLE.ART_REF%TYPE;
	ls_sco_code GEO_CLIENT.SCO_CODE%TYPE;
	is_cur_cli_ref GEO_ORDRE.CLI_REF%TYPE;
	gs_soc_code GEO_ORDRE.SOC_CODE%TYPE;
	
BEGIN
	-- correspond à f_details_exp_on_click_modifier.pbl
	res := 0;
	msg := '';

	select FLBAF, FLFAC, CLI_REF, soc_code into ls_flbaf, ls_flfac, is_cur_cli_ref, gs_soc_code
	FROM GEO_ORDRE
	where ORD_REF = arg_ord_ref;
	
	if ls_flbaf = 'O' or ls_flfac = 'O'  then
		msg := 'Ordre bon à facturer par une autre personne, modification impossible';
		res := 1;
		RETURN;
	End IF;

	IF arg_histo_orx_ref<>'' Then
		SELECT EXP_NB_COL_NEW,
		EXP_PDS_BRUT_NEW,
		EXP_PDS_NET_NEW,
		EXP_NB_PAL_NEW
		INTO  ldc_exp_nb_col_new, ldc_exp_pds_brut_new, ldc_exp_pds_net_new, ldc_exp_nb_pal_new   
		FROM GEO_HISTO_MODIF_DETAIL
		WHERE GEO_HISTO_MODIF_DETAIL.HISTO_ORX_REF = arg_histo_orx_ref;
	
		SELECT ach_bta_code, vte_bta_code, art_ref INTO ls_ach_bta_code, ls_vte_bta_code, ls_art_ref
		FROM geo_ordlig 
		WHERE orl_ref = arg_orl_ref;
	
		If ldc_exp_nb_pal_new IS NOT null THEN
			UPDATE geo_ordlig SET exp_nb_pal = ldc_exp_nb_pal_new WHERE orl_ref = arg_orl_ref;
				
			if ls_ach_bta_code = 'PAL' then				
				UPDATE geo_ordlig SET ach_qte = ldc_exp_nb_pal_new WHERE orl_ref = arg_orl_ref;
			end IF;
			IF ls_vte_bta_code = 'PAL' then
				UPDATE geo_ordlig SET vte_qte = ldc_exp_nb_pal_new WHERE orl_ref = arg_orl_ref;
			end IF;
		End IF;
	
		If ldc_exp_pds_brut_new IS NOT null Then	
			UPDATE geo_ordlig SET exp_pds_brut = ldc_exp_pds_brut_new WHERE orl_ref = arg_orl_ref;
		End IF;
	
		IF ldc_exp_pds_net_new IS NOT null Then						
			UPDATE geo_ordlig SET exp_pds_net = ldc_exp_pds_net_new WHERE orl_ref = arg_orl_ref;						
					
			case ls_ach_bta_code
				when 'KILO' then
					UPDATE geo_ordlig SET ach_qte = ldc_exp_pds_net_new WHERE orl_ref = arg_orl_ref;
				when 'TONNE' then
					UPDATE geo_ordlig SET ach_qte = round(ldc_exp_pds_net_new / 1000, 3) WHERE orl_ref = arg_orl_ref;
			end CASE;
			case ls_vte_bta_code
				when 'KILO' then
					UPDATE geo_ordlig SET vte_qte = ldc_exp_pds_net_new WHERE orl_ref = arg_orl_ref;
				when 'TONNE' then
					UPDATE geo_ordlig SET vte_qte = round(ldc_exp_pds_net_new / 1000, 3) WHERE orl_ref = arg_orl_ref;
			end CASE;				
		END IF;
	
	
		If ldc_exp_nb_col_new IS NOT NULL Then						
			UPDATE geo_ordlig SET exp_nb_col = ldc_exp_nb_col_new WHERE orl_ref = arg_orl_ref;

			SELECT u_par_colis INTO ll_pmb_per_col FROM geo_article WHERE art_ref = ls_art_ref;

			if ls_ach_bta_code = 'COLIS' then
				UPDATE geo_ordlig SET ach_qte = ldc_exp_nb_col_new WHERE orl_ref = arg_orl_ref;
			else 
				if ls_ach_bta_code not in ('KILO','TONNE','PAL','CAMION') THEN
					--ll_pmb_per_col = This.GetItemNumber(Row,'geo_article_u_par_colis')
					UPDATE geo_ordlig SET ach_qte = round( ldc_exp_nb_col_new * ll_pmb_per_col,0) WHERE orl_ref = arg_orl_ref;
				end if;
			End if;
			
			if ls_vte_bta_code = 'COLIS' then
				UPDATE geo_ordlig SET vte_qte = ldc_exp_nb_col_new WHERE orl_ref = arg_orl_ref;
			else 
				if ls_vte_bta_code not in ('KILO','TONNE','PAL','CAMION') THEN
					if ll_pmb_per_col <> 0 then
						UPDATE geo_ordlig SET vte_qte = round(ldc_exp_nb_col_new * ll_pmb_per_col,0) WHERE orl_ref = arg_orl_ref;
					end IF;
				END if;
			End if;	
		END IF;
	
		of_sauve_ordre(arg_ord_ref, res, msg);
		--DEBUT LLEF AUTOM. IND. TRANSP. + tri et emballage retrait
		select sco_code into ls_sco_code from GEO_CLIENT where CLI_REF = is_cur_cli_ref;
			if gs_soc_code ='UDC' and ls_sco_code = 'RET' then
				-- Créer fonction globale 
				f_chgt_qte_art_ret(arg_ord_ref, res, msg);
				
				/*IF ib_ligne_ordre_bloquer = TRUE 	then
					ls_bloque :='1'
				ELSE
					ls_bloque :='0'
				End IF;
				idw_lig_cde.retrieve(is_cur_ord_ref, ls_bloque)*/
			end IF;
		--FIN LLEF
			
	
	END IF;

	res := 1;
	msg := 'OK';
	RETURN;
	
END F_DETAILS_EXP_CLICK_MODIFIER;
/

