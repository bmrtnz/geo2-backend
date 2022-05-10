CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_DETAILS_EXP_ON_CLICK_AUTO (
	arg_orl_ref IN GEO_ORDLIG.ORL_REF%TYPE,
	res OUT NUMBER,
	msg OUT varchar2
)
IS
	ld_pmb_per_col NUMBER;
	ld_pdnet_client NUMBER;
	ld_col_tare NUMBER;
	ls_art_ref GEO_ARTICLE.ART_REF%TYPE;
	ld_nb_piece NUMBER;
	ld_nb_pal NUMBER;
	ld_nb_col NUMBER;
	ls_ach_bta_code GEO_ORDLIG.ACH_BTA_CODE%TYPE;
	ls_vte_bta_code GEO_ORDLIG.VTE_BTA_CODE%TYPE;
	ld_pds_net GEO_ORDLIG.EXP_PDS_NET%TYPE; 
	ld_pds_brut GEO_ORDLIG.EXP_PDS_BRUT%TYPE;
	ld_ach_qte GEO_ORDLIG.ACH_QTE%TYPE;
	ld_vte_qte GEO_ORDLIG.VTE_QTE%TYPE;
BEGIN
	-- correspond à f_details_exp_on_click_auto
	res := 0;
	msg := '';

	SELECT ol.art_ref, ol.CDE_NB_PAL, ol.CDE_NB_COL 
	INTO ls_art_ref, ld_nb_pal, ld_nb_col
	FROM geo_ordlig ol
	WHERE ol.ORL_REF = arg_orl_ref;

	select X.u_par_colis, X.pdnet_client, C.col_tare 
	into ld_pmb_per_col, ld_pdnet_client, ld_col_tare
	from geo_article X, geo_colis C
	where X.art_ref = ls_art_ref and C.esp_code = X.esp_code and C.col_code = X.col_code;

	if ld_pmb_per_col IS null then ld_pmb_per_col := 0; END IF;
	ld_nb_piece	:= ld_pmb_per_col;
	--// geo_colis.pmb_per_com contient le nombre d'UC (il s'agit de sachets ou de barquettes) - il y a précédence
	--// geo_article.pmb_per_col  contient le nombre de fruit et seulement celui là
	--// on va remplir les champs 'expédié' par recopie des champs 'commandé' après quelques petits calculs
	--ld_nb_pal			= This.object.cde_nb_pal[Row]								// nbre palettes
	--ld_nb_col			= This.object.cde_nb_col[Row]								// nbre colis
	--//ld_pds_net			= round(ld_col_pdnet * ld_nb_col, 0)					// poids net
	ld_pds_net := round(ld_pdnet_client * ld_nb_col, 0);					--// poids net client
	ld_pds_brut	:= round(ld_pds_net + (ld_col_tare * ld_nb_col), 0);	--// poids brut
	--ls_ach_bta_code	= This.object.ach_bta_code[Row]						// unité achat
	--ls_vte_bta_code	= This.object.vte_bta_code[Row]						// unité vente
	
	-- calcul nombre unité d'achat
	case ls_ach_bta_code
		WHEN 'COLIS' then
			ld_ach_qte := ld_nb_col;
		when 'KILO' then
			ld_ach_qte:= ld_pds_net;
		when 'PAL' then
			ld_ach_qte := ld_nb_pal;
		when 'TONNE' then
			ld_ach_qte := round(ld_pds_net / 1000,3);
		when 'CAMION' then
			ld_ach_qte := 0;
		else
			If ld_nb_piece = 0 or ld_nb_piece IS null  then  ld_nb_piece := 1; END IF;
			ld_ach_qte := round(ld_nb_col * ld_nb_piece, 0);
	end CASE;

	-- calcul nombre unité de vente
	case ls_vte_bta_code
		when 'COLIS' then
			ld_vte_qte	:= ld_nb_col;
		when 'KILO' then
			ld_vte_qte	:= ld_pds_net;
		when 'PAL' then
			ld_vte_qte	:= ld_nb_pal;
		when 'TONNE' then
			ld_vte_qte	:= round(ld_pds_net / 1000, 3);
		when 'CAMION' then
			ld_vte_qte	:= 0;
		else
			If ld_nb_piece = 0 or ld_nb_piece IS null then ld_nb_piece := 1; END IF;
			ld_vte_qte	:= round(ld_nb_col * ld_nb_piece, 0);
	end CASE;


	/*This.object.exp_nb_pal[Row] 	= ld_nb_pal			// nbre palettes
	This.object.exp_nb_col[Row] 	= ld_nb_col			// nbre colis
	This.object.exp_pds_brut[Row] = ld_pds_brut		// poids brut
	This.object.exp_pds_net[Row] 	= ld_pds_net		// poids net
	This.object.ach_qte[Row]		= ld_ach_qte		// nb unités achat
	This.object.vte_qte[Row] 		= ld_vte_qte			// nb unités vente
	This.AcceptText()*/
	UPDATE geo_ordlig l 
	SET l.EXP_NB_PAL = ld_nb_pal,
	l.EXP_NB_COL = ld_nb_col,
	l.EXP_PDS_BRUT = ld_pds_brut,
	l.EXP_PDS_NET = ld_pds_net,
	l.ACH_QTE = ld_ach_qte,
	l.VTE_QTE = ld_vte_qte
	WHERE l.ORL_REF = arg_orl_ref;

	res := 1;
	RETURN;
	
END F_DETAILS_EXP_ON_CLICK_AUTO;
/

