CREATE OR REPLACE PROCEDURE GEO_ADMIN."F_CREATE_LIGNE_EDI_ESP" (
	arg_ref_edi_ligne IN GEO_EDI_LIGNE.REF_EDI_LIGNE%TYPE,
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_cli_ref IN GEO_CLIENT.CLI_REF%TYPE,
    arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
	arg_art_ref IN GEO_EDI_ART_CLI.ART_REF%TYPE,
	arg_priorite IN GEO_EDI_ART_CLI.PRIORITE%TYPE,
	arg_fou_code IN GEO_EDI_ART_CLI.FOU_CODE%TYPE,
	arg_vte_pu	IN GEO_EDI_ART_CLI.VTE_PU%TYPE,
	arg_ach_pu IN GEO_EDI_ART_CLI.ACH_PU%TYPE,
	arg_qte_art_cde IN GEO_EDI_LIGNE.QUANTITE_COLIS%TYPE,
	arg_ean_prod_client IN GEO_EDI_LIGNE.EAN_PROD_CLIENT%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_username IN varchar2,
	arg_i IN number,
    res out number,
    msg out varchar2
)
AS
    ls_arg_bassin GEO_ENT_TRP_BASSIN.BAC_CODE%TYPE;
    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;

    ls_sco_code GEO_ORDRE.SCO_CODE%TYPE;

    ll_ACH_PU GEO_ORDLIG.ach_pu%TYPE;
    ls_ACH_BTA_CODE GEO_ORDLIG.ach_bta_code%TYPE;
    ls_VTE_BTA_CODE GEO_ORDLIG.vte_bta_code%TYPE;
    ls_FOU_CODE GEO_ORDLIG.fou_code%TYPE;
    ls_PAL_CODE GEO_ORDLIG.pal_code%TYPE;
    ll_PAL_NB_COL GEO_ORDLIG.pal_nb_col%TYPE;
    ls_ACH_DEV_PU GEO_ORDLIG.ACH_DEV_PU%TYPE;
    ls_ACH_DEV_CODE GEO_ORDLIG.ACH_DEV_CODE%TYPE;
    ls_bac_code GEO_ORDLIG.BAC_CODE%TYPE;
    ls_ESP_CODE GEO_ORDLIG.ESP_CODE%TYPE;
    ls_PROP_CODE GEO_ORDLIG.PROPR_CODE%TYPE;

    ls_ORL_REF GEO_ORDLIG.ORL_REF%TYPE;
    ls_orl_lig varchar(10);
    ll_edi_ord number;

    ld_ACH_DEV_PU GEO_STOCK_ART_EDI_BASSIN.ACH_DEV_PU%TYPE;
    ld_VTE_PU GEO_STOCK_ART_EDI_BASSIN.VTE_PU%TYPE;
    ld_VTE_PU_NET GEO_STOCK_ART_EDI_BASSIN.VTE_PU_NET%TYPE;

    ls_soc_code GEO_ORDRE.SOC_CODE%TYPE;
    ls_cam_code GEO_ORDRE.CAM_CODE%TYPE;
    ls_cli_ref GEO_ENTREP.CLI_REF%TYPE;

    ld_remsf_tx GEO_CLIENT.REM_SF_TX%TYPE;
    ls_dev_code GEO_CLIENT.DEV_CODE%TYPE;

    ld_pmb_per_com GEO_ARTICLE_COLIS.U_PAR_COLIS%TYPE;
    ld_pdnet_client GEO_ARTICLE_COLIS.pdnet_client%TYPE;
    ld_col_tare GEO_COLIS.COL_TARE%TYPE;

    ll_cde_nb_pal number;
    ld_pds_net number;
    ld_pds_brut number;
    ld_ACH_QTE number;
    ld_vte_qte number;
    ls_TOTVTE varchar2(100);
    ls_TOTACH varchar2(100);
    ls_TOTMOB varchar2(100);

    ls_tvt_code GEO_ORDRE.TVT_CODE%TYPE;

    ll_article_mode_culture geo_article_colis.MODE_CULTURE%TYPE;
    ls_cat_code geo_article_colis.CAT_CODE%TYPE;
    ls_ori_code geo_article_colis.ORI_CODE%TYPE;
    ls_var_code geo_article_colis.VAR_CODE%TYPE;
	ls_grv_code geo_article_colis.GRV_CODE%TYPE;

    ld_frais_pu_mark geo_attrib_frais.FRAIS_PU%TYPE;
    ls_frais_unite_mark geo_attrib_frais.FRAIS_UNITE%TYPE;
    ld_accompte geo_attrib_frais.ACCOMPTE%TYPE;
    ls_perequation geo_attrib_frais.PEREQUATION%TYPE;

    ll_k_frais number;
    ld_frais_pu number;
    ls_frais_unite varchar2(200);
    ls_indbloq_ach_dev_pu char(1);
    ll_ach_dev_taux number;

    ls_trp_code geo_ent_trp_bassin.trp_code%TYPE;
    ls_trp_bta_code geo_ent_trp_bassin.TRP_BTA_CODE%TYPE;
    ld_trp_pu geo_ent_trp_bassin.TRP_PU%TYPE;
    ls_trp_dev_code geo_ent_trp_bassin.TRP_DEV_CODE%TYPE;
    ld_dev_tx geo_devise_ref.DEV_TX%TYPE;

    ls_sto_ref GEO_STOCK.STO_REF%TYPE;
    ll_qte_ini GEO_STOCK.QTE_INI%TYPE;
    ll_qte_res GEO_STOCK.QTE_RES%TYPE;

    ll_stm_ref number;
    ls_nordre GEO_ORDRE.NORDRE%TYPE;
    ls_cli_code GEO_ORDRE.CLI_CODE%TYPE;
    ls_stm_ref GEO_STOMVT.stm_ref%TYPE;
    ls_desc GEO_STOMVT.STM_DESC%TYPE;

    ll_qte_restante number;
    ll_stock_nb_resa number;
	ls_edi_pal_inter GEO_ENTREP.EDI_PAL_INTER%TYPE;
	ls_r varchar2(2);
	ls_ref_cli GEO_ORDRE.REF_CLI%TYPE;
	
	ll_qte_art_cde GEO_EDI_LIGNE.QUANTITE_COLIS%TYPE;
	ll_pal_nb_palinter GEO_ORDLIG.PAL_NB_PALINTER%TYPE;
	ld_uc_pdnet_garanti GEO_ARTICLE_COLIS.UC_PDNET_GARANTI%TYPE;
	ls_gem_code GEO_ARTICLE_COLIS.GEM_CODE%TYPE;
	ldate_depdatp date;
	
BEGIN
    res := 0;
    msg := '';
	
	ls_prop_code := arg_fou_code;

    select dev_code 
	into ls_soc_dev_code 
	from geo_societe 
	where soc_code = arg_soc_code;

	-- récup du bassin du fournisseur
	begin
		select bac_code
		into ls_bac_code
		from GEO_FOURNI
		where fou_code = arg_fou_code
		and valide = 'O';
	exception when no_data_found then
        msg := msg || 'Erreur récupération bassin de la station ERR:' || SQLERRM;
        return;
    end;
	
    -- Recup du secteur de l'ordre
    select sco_code, soc_code, cam_code 
	into ls_sco_code, ls_soc_code, ls_cam_code 
	from GEO_ORDRE 
	where GEO_ORDRE.ORD_REF = arg_ord_ref;

    -- Recup de ORL suivante
    select F_SEQ_ORL_SEQ() into ls_ORL_REF FROM DUAL;
	
    -- recherche du prochain numero de ligne dans nouvel ordre
    select TRIM(to_char(count(orl_ref)+1,'00')) 
	into ls_orl_lig 
	from GEO_ORDLIG 
	where GEO_ORDLIG.ORD_REF = arg_ord_ref;
	
    -- Recup du pal_code au niveau de l'entrepôt
    select PAL_CODE, EDI_PAL_INTER 
	into ls_pal_code, ls_edi_pal_inter 
	from GEO_ENTREP 
	where cen_ref = arg_cen_ref;
	
	-- Ouverture de calibre
	if arg_priorite > 1 then
		ll_qte_art_cde := 0;
	else
		ll_qte_art_cde := arg_qte_art_cde;
	end if;
	
	
	ld_vte_pu_net := round(arg_vte_pu, 4);

    select rem_sf_tx, dev_code 
	into ld_remsf_tx, ls_dev_code 
	from geo_client 
	where cli_ref = arg_cli_ref;

    -- détermination du nbre de pal au sol
    begin
        select
            case P.dim_code
                when '1' then case when CS.COL_XB IS NOT NULL AND CS.COL_XH IS NOT NULL THEN CS.COL_XB * CS.COL_XH ELSE C.COL_XB * C.COL_XH END
                when '8' then case when CS.COL_YB IS NOT NULL AND CS.COL_YH IS NOT NULL THEN CS.COL_YB * CS.COL_YH ELSE C.COL_YB * C.COL_YH END
                when '6' then case when CS.COL_ZB IS NOT NULL AND CS.COL_ZH IS NOT NULL THEN CS.COL_ZB * CS.COL_ZH ELSE C.COL_ZB * C.COL_ZH END
                END, AC.U_PAR_COLIS, AC.pdnet_client, C.col_tare, AC.GEM_CODE, AC.UC_PDNET_GARANTI, AC.ESP_CODE
		into ll_pal_nb_col, ld_pmb_per_com, ld_pdnet_client, ld_col_tare, ls_gem_code, ld_uc_pdnet_garanti, ls_esp_code
        FROM
            GEO_PALETT P,
            GEO_COLIS C,
            GEO_ARTICLE_COLIS AC,
            geo_colis_secteur CS
        where
                P.PAL_CODE  = ls_pal_code and
                C.COL_CODE  = AC.COL_CODE  and
                AC.ART_REF = arg_art_ref and
                AC.ESP_CODE = C.ESP_CODE AND
                CS.esp_code (+)= AC.esp_code and
                CS.col_code (+)= AC.col_code and
                CS.SCO_CODE (+)= ls_sco_code and
				AC.valide = 'O';
    exception when no_data_found then
        msg := msg || 'Impossible de déterminer le nombre de palettes au sol ' || SQLERRM;
        return;
    end;

	-- Déterminer si l'entrepôt nécessite une palette intermédiaire uniquement pour UCSAC de 1.5kg
	ll_pal_nb_palinter := 0;
	if ls_gem_code = 'UCSAC' and ld_uc_pdnet_garanti = 1.5 then
		ll_pal_nb_col := 48;
		if ls_edi_pal_inter = 'O' then
			ll_pal_nb_palinter := 1;
			ll_pal_nb_col := 24;
		end if;
	end if;

	if ll_pal_nb_col is not null and ll_pal_nb_col > 0 then
        ll_cde_nb_pal := ROUND( (ll_qte_art_cde / ll_pal_nb_col) + 0.5, 0);

        if ll_cde_nb_pal = 0 then
            ll_cde_nb_pal := 1;
        end if;
    end if;

    ld_pds_net := round(ld_pdnet_client * ll_qte_art_cde, 0);
    ld_pds_brut := round(ld_pds_net + (ld_col_tare * ll_qte_art_cde), 0);

    -- NEW
	CASE ls_gem_code
		WHEN 'PLX' THEN ls_vte_bta_code := 'KILO';
		WHEN 'PLX2' THEN ls_vte_bta_code := 'KILO';
		WHEN 'CAISSE' THEN ls_vte_bta_code := 'KILO';
		WHEN 'UCPLT' THEN  ls_vte_bta_code := 'COLIS';
		WHEN 'UCBARQ' THEN ls_vte_bta_code := 'BARQUE';
		WHEN 'UCSAC' THEN ls_vte_bta_code := 'SACHET';
		ELSE ls_vte_bta_code := 'KILO';
	END CASE;
	--FIN NEW
	
	-- MERCADONA tjs ACHAT et VENTE au KILO
	ls_vte_bta_code := 'KILO';
	ls_ach_bta_code := ls_vte_bta_code;
	
	ll_ach_pu := arg_ach_pu;
	ls_ach_dev_pu	:= ll_ach_pu;
	ls_ach_dev_code	:= 'EUR';
	ll_ach_dev_taux	:= 1;
	
	case
        when ls_ach_bta_code = 'COLIS' or ls_ach_bta_code = 'BARQUE' or ls_ach_bta_code = 'SACHET' or ls_ach_bta_code = 'UNITE' then
		    ld_ACH_QTE := ll_qte_art_cde;
        when ls_ach_bta_code = 'KILO' then
            ld_ACH_QTE := ld_pds_net;
        when ls_ach_bta_code = 'PAL' then
            ld_ACH_QTE := ll_cde_nb_pal;
        when ls_ach_bta_code = 'TONNE' then
            ld_ACH_QTE := round(ld_pds_net / 1000, 0);
        when ls_ach_bta_code = 'CAMION' then
            ld_ACH_QTE := 0;
        else
            ld_ACH_QTE := round(ll_qte_art_cde *ld_pmb_per_com, 0);
    end case;

    case
        when ls_vte_bta_code = 'COLIS' or ls_vte_bta_code = 'BARQUE' or ls_vte_bta_code = 'SACHET' or ls_vte_bta_code = 'UNITE' then
		    ld_vte_qte := ll_qte_art_cde;
        when ls_vte_bta_code = 'KILO' then
            ld_vte_qte := ld_pds_net;
        when ls_vte_bta_code = 'PAL' then
            ld_vte_qte := ll_cde_nb_pal;
        when ls_vte_bta_code = 'TONNE' then
            ld_vte_qte := round(ld_pds_net / 1000, 0);
        when ls_vte_bta_code = 'CAMION' then
            ld_vte_qte := 0;
        else
            ld_vte_qte := round(ll_qte_art_cde * ld_pmb_per_com, 0);
    end case;

    ls_TOTVTE := to_char(arg_vte_pu * ld_vte_qte);
    ls_TOTACH := to_char(arg_ach_pu * ld_ACH_QTE);
    ls_TOTMOB := '0';

    -- New gestion des frais marketing
    if ls_soc_code <> 'BUK' then
        select O.tvt_code, O.sco_code into ls_tvt_code, ls_sco_code from geo_ordre O where O.ord_ref = arg_ord_ref;
        select A.mode_culture, A.cat_code, A.ori_code, A.var_code
        into ll_article_mode_culture, ls_cat_code, ls_ori_code, ls_var_code
        from geo_article_colis A
        where A.art_ref  = arg_art_ref;

        f_recup_frais(ls_var_code, ls_cat_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code, ll_k_frais, msg);

        begin
            select frais_pu, frais_unite, accompte, perequation
            into ld_frais_pu_mark, ls_frais_unite_mark, ld_accompte, ls_perequation
            from geo_attrib_frais
            where k_frais = ll_k_frais;

            if ls_sco_code = 'RET' then
                raise VALUE_ERROR; -- raise exception for force set value in exception block
            end if;

            ld_frais_pu := ld_frais_pu_mark;
            ls_frais_unite := ls_frais_unite_mark;

            if ls_perequation ='O' then
                If ld_accompte is not null and ld_accompte > 0 then
                    ll_ach_pu 		:= ld_accompte;
                    ls_ach_dev_pu	:= to_char(ld_accompte);
                    ls_ach_dev_code	:= 'EUR';
                    ll_ach_dev_taux	:= 1;
                    ls_indbloq_ach_dev_pu := 'O';
                    ls_ach_bta_code	:= 'KILO';
                end if;
            end if;
        exception when others then
            ld_frais_pu := 0;
            ls_frais_unite := '';
            ls_indbloq_ach_dev_pu := 'N';
        end;
    else
        if ls_dev_code = ls_soc_dev_code then
            ll_ach_dev_taux := 1;
            ls_ach_dev_code := ls_soc_dev_code;
        else
            If ls_soc_dev_code is not null and ls_soc_dev_code <> '' Then
                ls_ach_dev_code := ls_soc_dev_code;
            else
                ls_ach_dev_code := 'EUR';
            End If;

            begin
                select dev_tx into ll_ach_dev_taux
                from geo_devise_ref
                where dev_code = ls_dev_code and
                        dev_code_ref= ls_ach_dev_code;

                ls_indbloq_ach_dev_pu := 'N';
            exception when others then
                msg := '%%%erreur lecture devise';
                return;
            end;
        end if;
    end if;
    -- fin new gestion marketing

	--Complément de la référence client par rapport à la variété
	-- FUJI et CHANTECLER: +4 jours
	-- ENVY: +5 jours
	begin
		select var_code,
		O.ref_cli,
		O.depdatp,
		grv_code
		into ls_var_code, ls_ref_cli, ldate_depdatp, ls_grv_code
		from geo_article_colis A, geo_ordre O
        where A.art_ref  = arg_art_ref
		and O.ord_ref = arg_ord_ref;
	exception when others then
		msg := '%%%erreur  création date de la référence client';
		return;
	end;
	
	CASE ls_grv_code
		WHEN 'FUJI' THEN ls_r := substr(to_char(to_date(ldate_depdatp + 5, 'dd/mm/yy')), 1, 2);
		WHEN 'CHANTE' THEN ls_r := substr(to_char(to_date(ldate_depdatp + 4, 'dd/mm/yy')), 1, 2);
		WHEN 'ENVY' THEN ls_r := substr(to_char(to_date(ldate_depdatp + 5, 'dd/mm/yy')), 1, 2);
		ELSE ls_r := substr(to_char(to_date(ldate_depdatp + 4, 'dd/mm/yy')), 1, 2);
	END CASE;
	
	if arg_i = 1 then
		ls_ref_cli := ls_ref_cli || ls_r;
		
		begin
			update geo_ordre set ref_cli = ls_ref_cli
			where ord_ref = arg_ord_ref;
		exception when others then
			msg := '%%%erreur mise à jour ref_cli geo_ordre, ord_ref:' || arg_ord_ref || ', ref_cli:' || ls_ref_cli;
			return;
		end;  
	end if;
	
    begin
        INSERT INTO GEO_ORDLIG (
            ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, PAL_NB_PALINTER, CDE_NB_COL, EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU,
            VTE_BTA_CODE, VTE_QTE, FOU_CODE, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU,
            FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX, REMHF_TX, ART_REF
            , ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, ART_REF_KIT, GTIN_COLIS_KIT, REF_EDI_LIGNE, FRAIS_UNITE, PROPR_CODE, INDBLOQ_ACH_DEV_PU, LIB_DLV, vte_pu_net
        ) VALUES (
         ls_ORL_REF, arg_ord_ref, ls_orl_lig , ls_PAL_CODE, ll_pal_nb_col ,ll_cde_nb_pal, ll_pal_nb_palinter, ll_qte_art_cde, 0, 0,0, 0, ll_ach_pu, ls_ach_dev_code, ls_ach_bta_code, ld_ACH_QTE, arg_vte_pu,
         ls_vte_bta_code, ld_vte_qte, arg_fou_code, ld_pds_brut, ld_pds_net, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0, 'N', 'N', 'N', 'N', 'N', 'O', ld_frais_pu, 'N', 'N',ls_BAC_CODE, 0, 0, arg_art_ref,
         ls_ESP_CODE, 0, ll_ach_dev_taux, ls_ach_dev_pu, arg_art_ref, arg_ean_prod_client, arg_ref_edi_ligne, ls_frais_unite, ls_PROP_CODE, ls_indbloq_ach_dev_pu,'', ld_vte_pu_net
        );
    exception when others then
        msg := '%%% Erreur à la création de la ligne d~''ordre: ' || SQLERRM;
        return;
    end;

    res := 1;
end F_CREATE_LIGNE_EDI_ESP;
/
