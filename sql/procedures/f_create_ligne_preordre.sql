CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CREATE_LIGNE_PREORDRE(
	arg_ord_ref IN varchar2,
	arg_cen_ref IN varchar2,
	arg_nb_colis IN number,
	arg_nb_pal IN number,
	arg_pal_nb_col IN number,
	arg_pal_code IN varchar2,
	arg_palinter_nb IN number,
	arg_art_ref IN varchar2,
	arg_prop_code IN varchar2,
	arg_fou_code IN varchar2,
	arg_prix_vente IN number,
	arg_prix_achat IN number,
	arg_unite_achat IN varchar2,
	arg_unite_vente IN varchar2,
    res OUT number,
    msg OUT varchar2,
	ls_orl_ref OUT varchar2
)
AS
    ls_ORD_REF varchar2(50);
    ls_CDE_NB_COL varchar2(50);
    ls_EXP_NB_COL varchar2(50);
    ls_EXP_PDS_BRUT varchar2(50);
    ls_EXP_PDS_NET varchar2(50);
    ls_ACH_DEV_CODE varchar2(50);
    ls_VTE_QTE varchar2(50);
    ls_FOU_CODE varchar2(50);
    ls_ART_REF varchar2(50);
    ls_ESP_CODE varchar2(50);
    ll_ach_dev_taux number;
    ls_ACH_DEV_PU varchar2(50);
    ls_PAL_CODE varchar2(50);
    ls_ACH_BTA_CODE varchar2(50);
    ls_VTE_BTA_CODE varchar2(50);
    ls_PAN_CODE varchar2(50);
    ll_PAL_NB_COL number;
    ll_cde_nb_pal number;
    ls_TOTVTE varchar2(50);
    ls_TOTACH varchar2(50);
    ls_TOTMOB varchar2(50);
    ls_orl_lig varchar2(50);
    ll_EXP_PDS_NET number;
    ll_ACH_PU number;
    ll_VTE_PU number;
    ld_pmb_per_com number;
    ld_pdnet_client number;
    ld_col_tare number;
    ld_ACH_QTE number;
    ld_pds_net number;
    ld_pds_brut number;
    ld_frais_pu number;
    ls_BAC_CODE varchar2(50);
    ls_art_ref_kit varchar2(50);
    ls_sco_code varchar2(50);
    ls_histo_ord varchar2(50);
    ls_PROP_CODE varchar2(50);
    ls_sto_ref varchar2(50);
    ls_desc varchar2(50);
    ll_stm_ref number;
    ls_stm_ref varchar2(50);
    ls_stm_desc varchar2(50);
    ls_nordre varchar2(50);
    ls_cli_code varchar2(50);
    ls_sql varchar2(50);
    ls_frais_unite varchar2(50);
    ll_qte_ini number;
    ll_qte_res number;
    ll_qte_restante number;
    ll_stock_nb_resa number;
    ls_ean_cli varchar2(50);
    ll_qte_art_cde number;
    ld_prix_vente number;

    ls_null varchar2(50);
    ls_trp_code varchar2(50);
    ls_trp_bta_code varchar2(50);
    ls_trp_dev_code varchar2(50);
    ld_trp_pu number;
    ld_dev_tx number;
    lb_affect boolean;
    ls_cat_code varchar2(50);
    ls_ori_code varchar2(50);
    ls_var_code varchar2(50);
    ls_tvt_code varchar2(50);
    ll_mode_culture number;
    ls_perequation varchar2(50);
    ls_frais_unite_mark varchar2(50);
    ll_k_frais number;
    ll_article_mode_culture number;
    ld_accompte number;
    ld_prix_mini number;
    ld_frais_pu_mark number;
    ls_indbloq_ach_dev_pu varchar2(50);
    ls_gtin_uc varchar2(50);
    ls_gtin_colis varchar2(50);
    ls_gtin varchar2(50);
    ls_soc_code varchar2(50);
    ld_remsf_tx number;
    ld_vte_pu_net number;
    ld_vte_mt_net number;
    ls_societe_dev_code varchar2(50);
    ls_cli_ref varchar2(50);
    ls_dev_code varchar2(50);

BEGIN
    res := 0;
    msg := '';

    --Recup du secteur de l'ordre
    begin
        select sco_code, soc_code into ls_sco_code, ls_soc_code
		from GEO_ORDRE
		where GEO_ORDRE.ORD_REF = arg_ord_ref;
    exception when no_data_found then
        msg := msg || ' Impossible de recuperer le secteur de l''ordre avec la ref ' || arg_ord_ref || SQLERRM;
        res := 0;
        return;
    end;

    -- Recup de ORL suivante
    select F_SEQ_ORL_SEQ() into ls_ORL_REF FROM DUAL;
    --recherche du prochain numero de ligne dans nouvel ordre
    select TRIM(to_char(count(orl_ref)+1,'00')) into ls_orl_lig from GEO_ORDLIG where GEO_ORDLIG.ORD_REF = arg_ord_ref;

	--Recup du client au niveau de l'entrepôt
	begin
		select  CLI_REF into ls_cli_ref
		from GEO_ENTREP
		where cen_ref = arg_cen_ref;
	  exception when no_data_found then
        msg := msg || ' Impossible de recuperer la référence client pour l''entrepôt ' || arg_cen_ref || SQLERRM;
        res := 0;
        return;
    end;

	-- détermination du nbre de pal au sol
	ll_qte_art_cde := arg_nb_colis;


	--Tjs COLIS donc ll_qte_art_code
	ls_TOTVTE := to_char(arg_prix_vente * arg_nb_colis);

	ll_ACH_PU := arg_prix_achat;
	ls_TOTMOB := '0';

	-- Recup du bassin du propriétaire
	begin
		select bac_code into ls_bac_code
		from geo_fourni
		where fou_code = arg_prop_code;
	 exception when no_data_found then
        msg := msg || ' Impossible de recuperer le bassin du proproétaire ' || arg_prop_code || SQLERRM;
        res := 0;
        return;
    end;

	begin
		select dev_code into ls_societe_dev_code
		from geo_societe
		where soc_code = ls_soc_code;
	exception when no_data_found then
        msg := msg || ' Impossible de recuperer le code devise de la société ' || ls_soc_code || SQLERRM;
        res := 0;
        return;
    end;

	begin
		select rem_sf_tx, dev_code into ld_remsf_tx, ls_dev_code
		from geo_client
		where cli_ref = ls_cli_ref;
	exception when no_data_found then
        msg := msg || ' Impossible de recuperer le code devise de la société ' || ls_soc_code || SQLERRM;
        res := 0;
        return;
    end;

	if ld_remsf_tx is null then
		ld_remsf_tx := 0;
	end if;
	if ld_remsf_tx <> 0 then
		ld_vte_pu_net	:= round(arg_prix_vente - arg_prix_vente * ld_remsf_tx * 0.01, 4);
	else
		ld_vte_pu_net	:= round(arg_prix_vente, 4);
	end if;

	ls_vte_bta_code :=  arg_unite_vente;
	ls_ach_bta_code := arg_unite_achat;

    --Alimentation de la devise
    if ls_dev_code = ls_societe_dev_code  then
        ll_ach_dev_taux := 1;
        ls_ach_dev_code := ls_societe_dev_code;
    else
        If ls_societe_dev_code is not null and  ls_societe_dev_code <> '' Then
            ls_ach_dev_code := ls_societe_dev_code;
        else
            ls_ach_dev_code := 'EUR';
        end If;
    end if;

    --New gestion des frais marketing
    if ls_soc_code <> 'BUK' and ls_soc_code <> 'BWS' then
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

                if ls_sco_code <> 'RET' then
                        ld_frais_pu := ld_frais_pu_mark;
                        ls_frais_unite := ls_frais_unite_mark;
                        if ls_perequation ='O' then
                            If ld_accompte is not null and ld_accompte > 0 then
                                ll_ach_pu := ld_accompte;
                                ls_ach_dev_pu	:= to_char(ld_accompte);
                                ls_ach_dev_code	:= 'EUR';
                                ll_ach_dev_taux	:= 1;
                                ls_indbloq_ach_dev_pu := 'O';
                                ls_ach_bta_code	:= 'KILO';
                            end if;
                        end if;
                end if;
            exception when others then
                ld_frais_pu := 0;
                ls_frais_unite := '';
                ls_indbloq_ach_dev_pu := 'N';
            end;
    else
        if ls_dev_code <> ls_societe_dev_code  then
            begin
                select dev_tx into ll_ach_dev_taux
                from geo_devise_ref
                where dev_code = ls_dev_code and
                        dev_code_ref = ls_ach_dev_code;
            exception when others then
                msg := msg || '%%%erreur lecture devise';
                res := 0;
                return;
            end;
            ls_indbloq_ach_dev_pu := 'N';
        end if;
    end if;
    --fin marketing

	ls_TOTACH := to_char(ll_ACH_PU * arg_nb_colis);

	begin
		select esp_code,
			CASE WHEN GTIN_COLIS IS NOT NULL THEN GTIN_COLIS ELSE GTIN_COLIS_BW END,
			CASE WHEN GTIN_UC IS NOT NULL THEN GTIN_UC ELSE GTIN_UC_BW END,
			pdnet_client, col_tare
		into ls_esp_code, ls_gtin_uc, ls_gtin_colis, ld_pdnet_client, ld_col_tare
		from geo_article_colis
		where art_ref = arg_art_ref;
	exception when others then
		msg := msg || '%%%erreur récupération GTIN article';
		res := 0;
		return;
	end;

    if ls_gtin_uc is not null and length(ls_gtin_uc) > 0 then
        ls_gtin := ls_gtin_uc;
    else
        ls_gtin := ls_gtin_colis;
    end if;

	ld_pds_net	:= round(ld_pdnet_client * ll_qte_art_cde, 0);
	ld_pds_brut	:= round(ld_pds_net + (ld_col_tare * ll_qte_art_cde), 0);

    BEGIN
        INSERT INTO GEO_ORDLIG (
        ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, PAL_NB_PALINTER, PALINTER_CODE, CDE_NB_PAL, CDE_NB_COL, EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU,
        VTE_BTA_CODE, VTE_QTE, FOU_CODE, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU,
        FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX, REMHF_TX, ART_REF
        , ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, ART_REF_KIT, GTIN_COLIS_KIT, REF_EDI_LIGNE, FRAIS_UNITE, PROPR_CODE, INDBLOQ_ACH_DEV_PU, LIB_DLV, vte_pu_net
        ) VALUES (
            ls_ORL_REF, arg_ord_ref, ls_orl_lig , arg_pal_code, arg_pal_nb_col , arg_palinter_nb, arg_pal_code, arg_nb_pal, ll_qte_art_cde, 0, 0,0, 0, ll_ach_pu, ls_ach_dev_code, ls_ach_bta_code, ll_qte_art_cde, arg_prix_vente,
            ls_vte_bta_code, ll_qte_art_cde, arg_fou_code, ld_pds_brut, ld_pds_net, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0, 'N', 'N', 'N', 'N', 'N', 'O', ld_frais_pu, 'N', 'N',ls_BAC_CODE, 0, 0, arg_art_ref,
            ls_ESP_CODE, 0, ll_ach_dev_taux, ll_ach_pu, arg_art_ref, ls_gtin, '', ls_frais_unite, arg_prop_code, ls_indbloq_ach_dev_pu, '', ld_vte_pu_net
        );
    exception when others then
        rollback;
        res := 0;
        msg := 'Erreur à la création de la ligne d''ordre: ' || SQLERRM;
    END;

	res := 1;
    commit;

end;
/

