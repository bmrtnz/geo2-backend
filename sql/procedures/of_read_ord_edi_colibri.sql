CREATE OR REPLACE PROCEDURE GEO_ADMIN."OF_READ_ORD_EDI_COLIBRI" (
    arg_num_cde_edi IN number,
    arg_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
    arg_stock_type IN char,
    res out number,
    msg out clob
) AS
    ll_ref_edi_ligne number;
    ll_num_ligne number;
    ll_quantite_colis number;
    ld_prix_vente_edi number;

    ls_planif p_str_tab_type := p_str_tab_type();
    ls_null p_str_tab_type := p_str_tab_type();
    array_bws_ecris p_str_tab_type := p_str_tab_type();

    ls_ean_prod_client varchar2(50);
    ls_cli_ref varchar2(50);
    ls_cen_ref varchar2(50);
    ls_art_ref_client varchar2(50);
    ls_inc_code varchar2(50);
    ls_region_pref varchar2(50);
    ls_bac_code varchar2(50);
    ls_region_ent varchar2(50);
    ls_region varchar2(50);
    ls_art_ref varchar2(50);
    ls_plateforme_bws varchar2(50);
    ls_stock_plateforme varchar2(50);
    ls_stock_bassin varchar2(50);
    ls_stock_hors_bassin varchar2(50);
    ls_retour_sauve_planif varchar2(50);
    ls_ya_stock varchar2(50);
    ls_retour_sauve varchar2(50);
    ls_stock_station varchar2(50);
    ls_bw_stock varchar2(50);
	LS varchar2(20);
	i number;

	--Pour f_verif_bws
	ls_cli_code GEO_CLIENT.CLI_CODE%TYPE;
	ls_cen_code GEO_ENTREP.CEN_CODE%TYPE;
	ls_esp_code GEO_ARTICLE_COLIS.ESP_CODE%TYPE;
	ls_var_code GEO_ARTICLE_COLIS.VAR_CODE%TYPE;

	--Pour f_sauve_stock transféré à partir de f_controle_stock
	ls_VTE_BTA_CODE GEO_ORDLIG.VTE_BTA_CODE%TYPE := 'KILO';
	ld_VTE_PU GEO_ORDLIG.VTE_PU%TYPE;
	ls_canal_cde GEO_EDI_ORDRE.CANAL_CDE%TYPE;

	ls_pal_code_entrep GEO_ENTREP.PAL_CODE%TYPE;

begin
    msg := '';
    res := 0;
	LS := '~r~n';
	-- Vérification si l'ensemble des GTIN sont présents dans GEO_EDI_ART_CLI
	declare
	cursor C_CONTROLE_GTIN IS
		select /*+ optimizer_features_enable('8.1.7) */ L.ean_prod_client, L.code_interne_prod_client
		from geo_edi_ligne L, geo_edi_ordre O
		where O.ref_edi_ordre = arg_num_cde_edi
		and L.ref_edi_ordre = O.ref_edi_ordre
		and L.status <> 'D'
		and (L.ean_prod_client not in
			(select /*+ optimizer_features_enable('8.1.7) */ distinct E.gtin_colis_client from  GEO_EDI_ART_CLI E, GEO_ARTICLE_COLIS A
			where E.cli_ref = O.cli_ref and E.art_ref = A.art_ref and A.valide = 'O' and E.valide ='O' and  E.gtin_colis_client is not null)
			and L.code_interne_prod_client not in
			(select /*+ optimizer_features_enable('8.1.7) */ distinct E.art_ref_client from  GEO_EDI_ART_CLI E, GEO_ARTICLE_COLIS A
			where E.cli_ref = O.cli_ref and E.art_ref = A.art_ref and A.valide = 'O' and E.valide ='O' and E.art_ref_client is not null)
		);
	begin
		i := 0;
		open C_CONTROLE_GTIN;
		fetch C_CONTROLE_GTIN into ls_ean_prod_client, ls_art_ref_client;
		loop
			EXIT WHEN C_CONTROLE_GTIN%notfound;
			i := i + 1;
			if i = 1 then
				msg := '%%%ERREUR aucune référence(s) article(s) BW' || LS;
			end if;
			msg := msg || ' pour le GTIN article client : ' || ls_ean_prod_client || ' et code article client: ' || ls_art_ref_client || LS;
		fetch C_CONTROLE_GTIN into ls_ean_prod_client, ls_art_ref_client;

		end loop;
		close C_CONTROLE_GTIN;

		if msg <> '' then
			delete from GEO_STOCK_ART_EDI_BASSIN where edi_ord = arg_num_cde_edi;
			commit;
			res := 0;
			return;
		end if;

    end;
	-- Fin vérification

    declare
        cursor C_LIG_EDI_L IS

			select /*+ optimizer_features_enable('8.1.7) */ ref_edi_ligne, num_ligne, ean_prod_client, quantite_colis, prix_vente, cli_ref, cen_ref,
				code_interne_prod_client, inc_code, bac_code, art_ref, cli_code, cen_code, esp_code, var_code, vte_bta_code, canal_cde, pal_code_entrep
			from view_edi_art
			where ref_edi_ordre = arg_num_cde_edi;


            /*
			select L.ref_edi_ligne, L.num_ligne, L.ean_prod_client, L.quantite_colis,  L.prix_vente, O.cli_ref, O.cen_ref, L.code_interne_prod_client, O.inc_code, O.bac_code, E.art_ref
            from geo_edi_ligne L, geo_edi_ordre O, GEO_EDI_ART_CLI E, GEO_ARTICLE_COLIS A
            where O.ref_edi_ordre = arg_num_cde_edi
            and L.ref_edi_ordre = O.ref_edi_ordre
            and L.status <> 'D'
			and E.cli_ref = O.cli_ref
			and ( E.gtin_colis_client = L.ean_prod_client or E.art_ref_client = L.code_interne_prod_client)
			and E.art_ref = A.art_ref
			and A.valide = 'O'
			and E.valide ='O'
            order by ref_edi_ligne, num_ligne;
			*/

    begin
		select cen_ref, bac_code into ls_cen_ref, ls_region_pref
		from geo_edi_ordre
		where ref_edi_ordre = arg_num_cde_edi;

		f_recup_region_entrep(ls_cen_ref,res,msg,ls_region_ent);
		if res = 0 then
			--close C_LIG_EDI_L;
			return;
		end if;

		if ls_region_pref is null then -- Région préférentielle dans la commande
			ls_region := ls_region_ent;
		else
			f_verif_region_pref(ls_region_pref,res,msg,ls_region); -- return la région pref. désirée par le client
			if res = 0 then
				--close C_LIG_EDI_L;
				return;
			end if;
		end if;

        open C_LIG_EDI_L;
        fetch C_LIG_EDI_L into ll_ref_edi_ligne, ll_num_ligne, ls_ean_prod_client, ll_quantite_colis, ld_VTE_PU, ls_cli_ref, ls_cen_ref, ls_art_ref_client,
				ls_inc_code, ls_region_pref, ls_art_ref, ls_cli_code, ls_cen_code, ls_esp_code, ls_var_code, ls_VTE_BTA_CODE, ls_canal_cde, ls_pal_code_entrep;
        loop
            ls_art_ref_client := coalesce(ls_art_ref_client,'');
            ls_ean_prod_client := coalesce(ls_ean_prod_client,'');
			-- Nous avons des articles avec ce GTIN client
			ls_planif           := p_str_tab_type();
			array_bws_ecris     :=  p_str_tab_type();
			-- initialisation indicateur que l'on a trouvé du stock
			ls_ya_stock := 'N';

			 /*
			f_recup_region_entrep(ls_cen_ref,res,msg,ls_region_ent);
			if res = 0 then
				close C_LIG_EDI_L;
				return;
			end if;
			*/

			/*
			if ls_region_pref is null then -- Région préférentielle dans la commande
				ls_region := ls_region_ent;
			else
				f_verif_region_pref(ls_region_pref,res,msg,ls_region); -- return la région pref. désirée par le client
				if res = 0 then
					close C_LIG_EDI_L;
					return;
				end if;
			end if;
			*/

			f_verif_bws(arg_num_cde_edi, ll_ref_edi_ligne, ls_art_ref, ls_cli_code, ls_cen_code, ls_esp_code, ls_var_code, res, msg, ls_plateforme_bws); -- Return  la plateforme si insert effectué
			if res = 0 then
				close C_LIG_EDI_L;
				return;
			end if;

			if ls_plateforme_bws is not null then
				f_controle_stock(ls_plateforme_bws, ls_art_ref, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, 'BWS', array_bws_ecris, arg_stock_type, ll_quantite_colis, ls_cen_ref, ls_cli_ref, ls_ean_prod_client, ld_vte_pu, ls_vte_bta_code, ls_canal_cde, ls_art_ref_client, ls_pal_code_entrep, res, msg); -- Return 'OK' si insert effectué sinon 'KO' aucun insert

				if res = 0 then
					close C_LIG_EDI_L;
					return;
				end if;
				if res = 1 then
					ls_ya_stock := 'O';
				end if;
			end if;

			if ls_ya_stock = 'O' and arg_stock_type = 'S' then
				goto continue_label;
			end if;

			-- Vérification s'il existe une commande dans société BWS pour les entrepôts MOISSACPRES, TERRYPRES, et CHANTEPRES
			f_verif_ordre_bws(arg_num_cde_edi, ll_ref_edi_ligne, ls_art_ref, arg_cam_code, array_bws_ecris, ls_cli_ref, ls_cen_ref, ls_ean_prod_client, ld_vte_pu, ls_vte_bta_code, ls_canal_cde, ls_art_ref_client, ls_pal_code_entrep, res, msg);
			if res = 1 then
				ls_ya_stock := 'O';
			end if;

			if ls_ya_stock = 'O' and arg_stock_type = 'S' then
				goto continue_label;
			end if;

			-- Vérification s'il y a un SUIVI dans la table GEO_EDI_ART_PLANIF
			f_verif_planif(ls_region, ls_art_ref, ls_cen_ref, ls_pal_code_entrep, res, msg, ls_planif); -- Recherche dans la table planif avec la région définie ci-dessus.

			if ls_planif.count() > 0 then
				f_sauve_stock_planif(ls_planif, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, ls_cli_ref, ls_cen_ref, ls_ean_prod_client, ld_vte_pu, ls_vte_bta_code, ls_canal_cde, ls_art_ref_client, res, msg); -- Return 'OK' si insert effectué

				if res = 0 then
					close C_LIG_EDI_L;
					return;
				end if;
				if res = 1 then
					ls_ya_stock := 'O';
				end if;
			end if;

			if ls_ya_stock = 'O' and arg_stock_type = 'S' then
				goto continue_label;
			end if;

			if ls_region <> '''SE''' and ls_region <> '''SW'',''UDC''' and ls_region <> '''UDC''' and ls_region <> '''VDL''' and ls_region <> '%' then -- Le client souhaite une station particuliere
				f_controle_stock(ls_region, ls_art_ref, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, 'STATION', array_bws_ecris, arg_stock_type, ll_quantite_colis, ls_cen_ref, ls_cli_ref, ls_ean_prod_client, ld_vte_pu, ls_vte_bta_code, ls_canal_cde, ls_art_ref_client, ls_pal_code_entrep, res, msg); -- Return 'OK' si insert effectué sinon 'KO' aucun insert

				if res = 0 then
					close C_LIG_EDI_L;
					return;
				end if;

				if res = 1 then
					ls_ya_stock := 'O';
				end if;
			else
				f_controle_stock(ls_region, ls_art_ref, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, 'DANS_BASSIN', array_bws_ecris, arg_stock_type, ll_quantite_colis, ls_cen_ref, ls_cli_ref, ls_ean_prod_client, ld_vte_pu, ls_vte_bta_code, ls_canal_cde, ls_art_ref_client, ls_pal_code_entrep, res, msg); -- Return 'OK' si insert effectué sinon 'KO' aucun insert

				if res = 0 then
					close C_LIG_EDI_L;
					return;
				end if;

				if res = 1 then
					ls_ya_stock := 'O';
				end if;

				if ls_ya_stock = 'O' and arg_stock_type = 'S' then
					goto continue_label;
				end if;

				f_controle_stock(ls_region, ls_art_ref, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, 'HORS_BASSIN', array_bws_ecris, arg_stock_type, ll_quantite_colis, ls_cen_ref, ls_cli_ref, ls_ean_prod_client, ld_vte_pu, ls_vte_bta_code, ls_canal_cde, ls_art_ref_client, ls_pal_code_entrep, res, msg); -- Return 'OK' si insert effectué sinon 'KO' aucun insert

				if res = 0 then
					close C_LIG_EDI_L;
					return;
				end if;

				if res = 1 then
					ls_ya_stock := 'O';
				end if;

				if ls_ya_stock = 'O' and arg_stock_type = 'S' then
					goto continue_label;
				end if;
			end if;

			-- On a pas trouvé de stock pour le GTIN client. On insére une ligne sans fournisseur
			if ls_ya_stock = 'N' then
				f_sauve_stock('', arg_num_cde_edi, ll_ref_edi_ligne, ls_art_ref, arg_cam_code, 'SANS_STOCK', array_bws_ecris,'', 'N', '', ls_cli_ref, ls_cen_ref, ls_ean_prod_client, ld_vte_pu, ls_vte_bta_code, ls_canal_cde, ls_art_ref_client, res, msg);

				if res = 0 then
					close C_LIG_EDI_L;
					return;
				end if;
			end if;

			<<continue_label>> null;

			fetch C_LIG_EDI_L into ll_ref_edi_ligne, ll_num_ligne, ls_ean_prod_client, ll_quantite_colis, ld_vte_pu, ls_cli_ref, ls_cen_ref, ls_art_ref_client,
					ls_inc_code, ls_region_pref, ls_art_ref, ls_cli_code, ls_cen_code, ls_esp_code, ls_var_code, ls_VTE_BTA_CODE, ls_canal_cde, ls_pal_code_entrep;
           EXIT WHEN C_LIG_EDI_L%notfound;
        end loop;
        close C_LIG_EDI_L;
    exception when others then
        close C_LIG_EDI_L;
        res := 0;
        msg := '%%%ERREUR lecture commande EDI: ' || arg_num_cde_edi;
        return;
    end;

    commit;
    res := 1;

end;
/
