CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_SAUVE_ORD_BWS (
	arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_orl_ref IN GEO_ORDLIG.ORL_REF%TYPE,
    arg_ref_edi_ordre IN GEO_EDI_ORDRE.REF_EDI_ORDRE%TYPE,
    arg_ref_edi_ligne IN GEO_EDI_LIGNE.REF_EDI_LIGNE%TYPE,
    arg_art_ref IN GEO_ARTICLE_COLIS.ART_REF%TYPE,
	arg_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
	arg_bws_ecris  IN  OUT P_STR_TAB_TYPE,

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

	ls_ACH_BTA_CODE GEO_ORDLIG.ACH_BTA_CODE%TYPE;
	ls_ACH_DEV_CODE GEO_ORDLIG.ACH_DEV_CODE%TYPE;
	ls_vte_bta_code GEO_ORDLIG.VTE_BTA_CODE%TYPE;
	ls_histo_VTE_BTA_CODE GEO_ORDLIG.VTE_BTA_CODE%TYPE;
	ld_ACH_DEV_PU GEO_ORDLIG.ACH_DEV_PU%TYPE;
	ld_ACH_PU GEO_ORDLIG.ACH_PU%TYPE;
	--arg_vte_pu GEO_ORDLIG.VTE_PU%TYPE;
	ld_vte_pu_net GEO_ORDLIG.VTE_PU_NET%TYPE;
	ld_ach_dev_taux GEO_ORDLIG.ACH_DEV_TAUX%TYPE;
	ld_histo_VTE_PU GEO_ORDLIG.VTE_PU%TYPE;
	ld_histo_VTE_PU_NET GEO_ORDLIG.VTE_PU_NET%TYPE;
	--arg_cli_ref GEO_CLIENT.CLI_REF%TYPE;
	ls_fou_code GEO_FOURNI.FOU_CODE%TYPE;
	ls_prop_code GEO_FOURNI.FOU_CODE%TYPE;
	--arg_cen_ref GEO_ENTREP.CEN_REF%TYPE;
	--arg_ean_prod_client GEO_EDI_LIGNE.EAN_PROD_CLIENT%TYPE;
	ls_age GEO_STOCK.AGE%TYPE;
	ll_qte_restant_stock number;
	ls_flag_hors_bassin GEO_STOCK_ART_EDI_BASSIN.FLAG_HORS_BASSIN%TYPE;
	ls_sauve_stock varchar2(2);
	--arg_canal_cde GEO_EDI_ORDRE.CANAL_CDE%TYPE;
	ls_gem_code GEO_ARTICLE_COLIS.GEM_CODE%TYPE;
	--arg_art_ref_client GEO_EDI_LIGNE.CODE_INTERNE_PROD_CLIENT%TYPE;
	ls_code_prod_client varchar2(20);
	ls_pal_code GEO_ENTREP.PAL_CODE%TYPE;
	ls_bac_code_entrep varchar2(12);
	ls_dept_entrep varchar2(2);
	ll_dept_entrep number;
	--arg_cen_ref_client GEO_EDI_ORDRE.CEN_REF%TYPE;
	--arg_pal_code_entrep GEO_ENTREP.PAL_CODE%TYPE;
	ls_enr_bws_ecris varchar2(150);


BEGIN
    -- correspond à of_sauve_ord_bws.pbl
    res := 0;
    msg := '';
	arg_bws_ecris := p_str_tab_type();

	-- Init. avec les valeurs de base
	ls_sauve_stock := 'KO';
	ls_vte_bta_code := arg_vte_bta_code;
	ls_flag_hors_bassin := 'ABS'; --Attendu BwStock
	ls_enr_bws_ecris	:= '';

/*
    begin
        select cli_ref, cen_ref, ean_prod_client, prix_vente, unite_qtt, canal_cde, code_interne_prod_client
		into arg_cli_ref, arg_cen_ref, arg_ean_prod_client, arg_vte_pu, arg_vte_bta_code, arg_canal_cde, arg_art_ref_client
		from geo_edi_ordre O, geo_edi_ligne L
		where O.ref_edi_ordre = arg_ref_edi_ordre
		and L.ref_edi_ordre = O.ref_edi_ordre
		and L.ref_edi_ligne = arg_ref_edi_ligne;
    exception when others then
        msg := '%%%ERREUR f_sauve_ord_bws edi_ordre: ' || to_char(arg_ref_edi_ordre) || ' edi_ligne: ' + to_char(arg_ref_edi_ligne);
		res := 0;
        return;
    end;
*/

	ld_vte_pu_net := arg_vte_pu;
	ls_age := '';
	ll_qte_restant_stock := 0;

	if arg_ean_prod_client is null or arg_ean_prod_client = '' then
		ls_code_prod_client := arg_art_ref_client;
	else
		ls_code_prod_client := arg_ean_prod_client;
	end if;

    begin
        select /*+ optimizer_features_enable('8.1.7) */
		case O.cli_code when 'BWSTOCK' then 'BWSTOC' END,
		case O.cen_code when 'ENTREPOT PCHANTEGR' then 'CHANTEPRES'
		when 'ENTREPOT MOISSACDV' then 'MOISPRESCDV'
		when 'ENTREPOT TERRYLOIR' then  'TERRYPRES'
		when 'MOISSACDV NON CHEP' then 'MOISPRESCDV'
		END,
		SUBSTR(E.zip,1,2)
		into  ls_prop_code, ls_fou_code, ls_dept_entrep
		from geo_ordre O, geo_entrep E
		where O.ord_ref = arg_ord_ref
		and O.cen_ref = E.cen_ref
		and O.cam_code = arg_cam_code;
    exception when others then
        msg := '%%%ERREUR of_sauve_ord_bws read GEO_ORDRE ord_ref: ' || arg_ord_ref || ' ,ref_edi_ordre: ' || to_char(arg_ref_edi_ordre) || 'ref_edi_ligne: ' || to_char(arg_ref_edi_ligne) || ' ' || SQLERRM;
		res := 0;
        return;
    end;

	--F_RECUP_REGION_ENTREP(arg_cen_ref, res, msg, ls_bac_code_entrep);

	begin
		select /*+ optimizer_features_enable('8.1.7) */ bac_code into ls_bac_code_entrep
		from geo_dept
		where num_dept = ls_dept_entrep;
	exception when others then
        msg := '%%%ERREUR of_sauve_ord_bws read GEO_DEPT: ' || ls_dept_entrep || ' ,ref_edi_ordre: ' || to_char(arg_ref_edi_ordre) || 'ref_edi_ligne: ' || to_char(arg_ref_edi_ligne) || ' ' || SQLERRM;
		res := 0;
        return;
    end;

	begin
		--Récupération des informations d'achats de la précédente commande
		select /*+ optimizer_features_enable('8.1.7) */  *
		into ls_ACH_BTA_CODE, ls_ACH_DEV_CODE, ld_ACH_DEV_PU, ld_ACH_PU, ls_histo_VTE_BTA_CODE, ld_histo_VTE_PU, ld_ach_dev_taux from (
			select /*+ optimizer_features_enable('8.1.7) */   ACH_BTA_CODE, ACH_DEV_CODE, ACH_DEV_PU, ACH_PU, VTE_BTA_CODE, VTE_PU, ACH_DEV_TAUX
			from geo_ordre O, geo_ordlig L
			where O.cli_ref = arg_cli_ref
			and O.cen_ref = arg_cen_ref
			and O.ord_ref = L.ord_ref
			and L.art_ref = arg_art_ref
			-- Suite Réunion du 20/12/2023 avec F. GAY, SLAM ne plus filter sur emballeur/expediteur
			-- and L.fou_code = ls_fou_code
			-- and L.propr_code = ls_prop_code
			and FACTURE_AVOIR ='F'
			and FLANNUL = 'N'
			and typ_ordre ='ORD'
			order by O.CREDAT desc
		)
		where rownum = 1;

		--select /*+ optimizer_features_enable('8.1.7) */  *
		--into ls_ACH_BTA_CODE, ls_ACH_DEV_CODE, ld_ACH_DEV_PU, ld_ACH_PU, ls_histo_VTE_BTA_CODE, ld_histo_VTE_PU, ld_ach_dev_taux from (
		--	select /*+ optimizer_features_enable('8.1.7) */ ACH_BTA_CODE, ACH_DEV_CODE, ACH_DEV_PU, ACH_PU, VTE_BTA_CODE, VTE_PU, ACH_DEV_TAUX
		--	from VIEW_EDI_LIGNE_ORDRE
		--	where cli_ref = arg_cli_ref
		--	and cen_ref = arg_cen_ref
		--	and art_ref = arg_art_ref
		--	order by CREDAT desc
		--)
		--where rownum = 1;

	exception when NO_DATA_FOUND then
		ls_ACH_BTA_CODE := 'KILO';
		ls_ACH_DEV_CODE := 'EUR';
		ld_ACH_DEV_PU	 := 0;
		ld_ACH_PU	:= 0;
		ld_ach_dev_taux	 := 1;
	--exception when others then
    --    msg := '%%%Erreur sur récupération des informations d''achats de la précédente commande : ' || arg_art_ref || ' ' || SQLERRM;
	--    res := 0;
        --return;
    end;
	if arg_canal_cde = 'EDI' then
		if ld_histo_VTE_PU <> arg_vte_pu and ld_histo_VTE_PU is not null then
			update geo_edi_ligne
			set alert_prix = 'Attention Prix Précédent = ' || ld_histo_VTE_PU
			where ref_edi_ordre = arg_ref_edi_ordre
			and ref_edi_ligne = arg_ref_edi_ligne;
			commit;
		end if;
		begin
			select /*+ optimizer_features_enable('8.1.7) */ AC.GEM_CODE
			into ls_gem_code
			FROM GEO_ARTICLE_COLIS AC
			where AC.ART_REF = arg_art_ref
			and AC.valide = 'O';
		exception when others then
			msg := '%%%Erreur sur récup GEM_CODE pour l''article : ' || arg_art_ref;
		    res := 0;
			return;
		end;
		CASE ls_gem_code
			WHEN 'PLX' THEN ls_vte_bta_code := 'KILO';
			WHEN 'PLX2' THEN ls_vte_bta_code := 'KILO';
			WHEN 'CAISSE' THEN ls_vte_bta_code := 'KILO';
			WHEN 'UCPLT' THEN  ls_vte_bta_code := 'COLIS';
			WHEN 'UCBARQ' THEN ls_vte_bta_code := 'BARQUE';
			WHEN 'UCSAC' THEN ls_vte_bta_code := 'SACHET';
			ELSE ls_vte_bta_code := 'KILO';
		END CASE;
	end if;

	ls_pal_code := '-';
	/*
	begin
		select O.cen_ref, E.pal_code
		into ls_cen_ref_client, ls_pal_code_entrep
		from geo_edi_ordre O, geo_entrep E
		where O.ref_edi_ordre = arg_ref_edi_ordre
		and O.cen_ref = E.cen_ref
		and E.valide = 'O';
	exception when others then
		msg := '%%%Erreur sur récup pal_code pour l''entrepôt pour l''ordre EDI: ' || arg_ref_edi_ordre || ' ' || SQLERRM;
		res := 0;
		return;
	end;
	*/

	if  length(arg_pal_code_entrep) > 0 and arg_pal_code_entrep is not null then
		ls_pal_code := arg_pal_code_entrep;
	end if;

	begin
		insert into GEO_STOCK_ART_EDI_BASSIN (
		EDI_ORD, EDI_LIG, CLI_REF, CAM_CODE, ART_REF, GTIN, FOU_CODE, BAC_CODE, QTE_RES, AGE, PROP_CODE, ACH_BTA_CODE, ACH_DEV_CODE, ACH_DEV_PU, ACH_PU, VTE_BTA_CODE, VTE_PU, VTE_PU_NET, ACH_DEV_TAUX,
		FLAG_HORS_BASSIN, PAL_CODE, STO_REF
		)
		values(
		arg_ref_edi_ordre, arg_ref_edi_ligne, arg_cli_ref, arg_cam_code, arg_art_ref, ls_code_prod_client, ls_fou_code, ls_bac_code_entrep, ll_qte_restant_stock, ls_age, ls_prop_code, ls_ACH_BTA_CODE, ls_ACH_DEV_CODE,
		ld_ACH_DEV_PU, ld_ACH_PU, ls_vte_bta_code, arg_vte_pu, ld_vte_pu_NET, ld_ach_dev_taux, ls_flag_hors_bassin, ls_pal_code, ''
		);
	exception when others then
        msg := '%%%ERREUR f_sauve_ord_bws insert GEO_STOCK_ART_EDI_BASSIN ref_edi_ordre: ' || to_char(arg_ref_edi_ordre) || ' ref_edi_ligne: ' || to_char(arg_ref_edi_ligne) || ' art_ref: ' || arg_art_ref || ' ' || SQLERRM;
		res := 0;
		rollback;
        return;
    end;

	ls_enr_bws_ecris := arg_art_ref || ';' || ls_prop_code || ';' || ls_fou_code || ';' || ls_bac_code_entrep ||';' || to_char(arg_ref_edi_ordre) || ';' || to_char(arg_ref_edi_ligne);
	arg_bws_ecris.extend();
	arg_bws_ecris(arg_bws_ecris.count()) := ls_enr_bws_ecris;

	commit;
    res := 1;
END F_SAUVE_ORD_BWS;
/
