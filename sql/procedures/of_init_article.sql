CREATE OR REPLACE PROCEDURE "OF_INIT_ARTICLE" (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_art_ref in AVI_ART_GESTION.ART_REF%TYPE,
	arg_soc_code in geo_societe.SOC_CODE%TYPE,
    -- dans le cas d'une maj de ligne
	orl_ref_update in varchar2,
    res out number,
    msg out varchar2,
    new_orl_ref out varchar2,
    art_ass out varchar2
)
AS
	ls_art_ref varchar2(50);
	ls_esp_code varchar2(50);
	ls_var_code varchar2(50);
	ls_cat_code varchar2(50);
	ls_cam_code varchar2(50);
	ls_col_code varchar2(50);
	ls_mdd varchar2(50);
	ls_ccw_code varchar2(50);
	ls_frais_unite varchar2(50);
	ls_tem_code varchar2(50);
	ls_col_prepese varchar2(50);
	ls_rc varchar2(50);
	ld_frais_pu number;
	ld_col_pdnet number;
	ld_prix_mini number;
	ld_date_exp date;
	ld_date_liv date;
	ls_var_ristourne varchar2(50);
	ll_article_mode_culture number;
	ls_mode_culture_libelle varchar2(50);
	ls_ori_code varchar2(50);
	ls_ori_desc varchar2(50);
	ls_soc_code varchar2(50);
	ld_ach_dev_pu number;
	ld_dev_taux number;
	ld_ach_pu number;
	ls_dev_code varchar2(50);
	ls_fou_code varchar2(50);
	ls_ya_certif varchar2(50);
	ls_certifs_cli varchar2(50);
	ls_mode_culture number;
	ls_certifs varchar2(50);
	ls_sco_code varchar2(50);
	ls_cli_col_code varchar2(50);
	ls_typ_ordre varchar2(50);
	ls_ind_exclu_frais_pu varchar2(50);
	ls_ind_modif_detail varchar2(50);
	ld_pdnet_client number;
	ld_frais_pu_mark number;
	ld_accompte number;
	ls_frais_unite_mark varchar2(50);
	ls_perequation varchar2(50);
	ls_tvt_code varchar2(50);
	ll_k_frais number;
	ls_bloque varchar2(50);
	ls_orl_ref varchar2(50);
	ls_pal_code varchar2(50);
    ls_user GEO_ORDRE.MOD_USER%TYPE;
	ls_ach_dev_code varchar2(50);

	cur_orl_ref GEO_ORDLIG.orl_ref%type;
	soc_dev_code GEO_DEVISE.dev_code%type;
	is_cur_cli_ref GEO_CLIENT.cli_ref%type;
	is_cur_cen_ref GEO_ENTREP.cen_ref%type;
	is_dluo_client varchar2(50);

	initial_gtin GEO_ORDLIG.gtin_colis_kit%type;
	initial_kit GEO_ORDLIG.art_ref_kit%type;

	CURSOR C_CERTIFS
	IS
		SELECT CERTIF
		FROM GEO_CERTIFS_TIERS
		WHERE TIERS = is_cur_cli_ref
		AND TYP_TIERS = 'C'
		ORDER BY CERTIF;

begin

	res := 0;
	msg := '';

    if orl_ref_update is null then
	    select f_seq_orl_seq() into cur_orl_ref from dual;
        new_orl_ref := cur_orl_ref;
    else
        declare
            count_orl_ref number;
        begin
            select count(*)
            into count_orl_ref
            from geo_ordlig
            where ord_ref = arg_ord_ref and orl_ref = orl_ref_update;
            if count_orl_ref = 0 then
                msg := 'Référence de ligne inexistante dans cet ordre';
                res := 0;
                return;
            end if;
            cur_orl_ref := orl_ref_update;
            new_orl_ref := cur_orl_ref;

            select gtin_colis_kit,art_ref_kit
            into initial_gtin,initial_kit
            from geo_ordlig
            where orl_ref = cur_orl_ref;
        end;
    end if;

	select dev_code into soc_dev_code from geo_societe where soc_code = arg_soc_code;
	select c.cli_ref, c.dluo, o.cen_ref, o.typ_ordre, o.ind_exclu_frais_pu, o.sco_code, e.pal_code, o.MOD_USER, o.dev_code
	into is_cur_cli_ref, is_dluo_client, is_cur_cen_ref, ls_typ_ordre, ls_ind_exclu_frais_pu, ls_sco_code, ls_pal_code, ls_user, ls_ach_dev_code
	from geo_ordre o
	left join geo_client c on o.cli_ref = c.cli_ref
	left join geo_entrep e on o.cen_ref = e.cen_ref
	where ord_ref = arg_ord_ref;


	-- if arg_sco_code is not null then
	-- 	ls_sco_code := arg_sco_code;
	-- end if;
	-- if arg_typ_ordre is not null then
	-- 	ls_typ_ordre := arg_typ_ordre;
	-- end if;
	-- if arg_ind_exclu_frais_pu is not null then
	-- 	ls_ind_exclu_frais_pu := arg_ind_exclu_frais_pu;
	-- end if;

	if (arg_art_ref IS NOT null) then

		begin

			select A.art_ref, A.esp_code, A.var_code, A.cat_code,A.ccw_code, A.cam_code, A.col_code, A.mdd, A.col_pdnet, A.col_prepese,A.mode_culture,M.libelle,O.ori_code, O.ori_desc, A.ind_modif_detail, A.pdnet_client, A.tvt_code
			into ls_art_ref,  ls_esp_code, ls_var_code, ls_cat_code, ls_ccw_code, ls_cam_code, ls_col_code, ls_mdd, ld_col_pdnet, ls_col_prepese,ll_article_mode_culture,ls_mode_culture_libelle,
			ls_ori_code,ls_ori_desc	,ls_ind_modif_detail, ld_pdnet_client, ls_tvt_code
			from geo_article_colis A, geo_mode_culture M , geo_origine O
			where A.art_ref = arg_art_ref and
					A.mode_culture = M.ref and
					A.esp_code = O.esp_code and
					A.ori_code = O.ori_code ;

		exception
			when no_data_found then
				msg := SQLERRM;
				return;
		end;

        -- source : of_affecte_ordre_array_ligne
        --deb llef ne pas permettre la selection d'article pour certain entrepôt ayant un code emballage specifique
        declare
            ls_cli_col_code varchar2(50);
        begin
            select col_code
            into ls_cli_col_code
            from geo_entrep_not_embal
            where cli_ref=is_cur_cli_ref and (cen_ref=is_cur_cen_ref OR cen_ref='*') and col_code=ls_col_code and valide='O';
            msg := msg || ' article interdit: l''emballage ' ||  ls_cli_col_code || ' de l''article ' || ls_art_ref || ' n''est pas accepté par ce client/entrepôt';
            res := 0;
            return;
        exception when no_data_found then
            null;
        end;
        --fin llef

		declare
			id_remsf number;
            x_rem_hf_tx number;
            x_rem_sf_tx_mdd number;
            x_rem_sf_tx number;
		begin
            SELECT coalesce(rem_hf_tx,0), coalesce(rem_sf_tx_mdd,0), coalesce(rem_sf_tx,0)
            INTO x_rem_hf_tx, x_rem_sf_tx_mdd, x_rem_sf_tx
            FROM geo_client
            where cli_ref = is_cur_cli_ref;

            if ls_mdd = 'O' then
				id_remsf := x_rem_sf_tx_mdd;
			else
				id_remsf := x_rem_sf_tx;
			end if;

            if orl_ref_update is null then
                insert into geo_ordlig ( orl_ref, ord_ref )
                values ( cur_orl_ref, arg_ord_ref );
            end if;

            update geo_ordlig set
                art_ref = ls_art_ref,
                esp_code = ls_esp_code,
                pal_code = coalesce(ls_pal_code, '-'),
                ach_dev_code = coalesce(ls_ach_dev_code, 'EUR'),
                remsf_tx = id_remsf,
                remhf_tx = x_rem_hf_tx
            where orl_ref = cur_orl_ref;

			if ls_pal_code is not null then
                ON_CHANGE_PAL_CODE(cur_orl_ref, ls_user, ls_sco_code, res, msg);
                if res <> 1 then return; end if;
            end if;
			commit;
		end;


		select var_ristourne into ls_var_ristourne
		from geo_variet where var_code = ls_var_code;

		update geo_ordlig set
			var_ristourne = ls_var_ristourne
		where orl_ref = cur_orl_ref;
		commit;

		If arg_soc_code <> 'BUK' Then

			--New gestion des frais marketing


			f_recup_frais(ls_var_code, ls_ccw_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code, ll_k_frais, msg);

			begin
				select frais_pu, frais_unite, accompte, perequation
				into ld_frais_pu_mark, ls_frais_unite_mark, ld_accompte, ls_perequation
				from geo_attrib_frais
				where k_frais = ll_k_frais;

				ld_frais_pu := ld_frais_pu_mark;
				ls_frais_unite := ls_frais_unite_mark;
				if  ls_perequation =  'O' then
					ld_prix_mini := ld_accompte;
				else
					ld_prix_mini := 0;
				end if;
			EXCEPTION WHEN NO_DATA_FOUND THEN -- Permet de passer a la suite
				ld_frais_pu := 0;
				ld_prix_mini := 0;
			end;

			--fin marketing

			If arg_soc_code = 'BWS' or ls_sco_code ='RET' THen
				ld_frais_pu := 0;
				ls_frais_unite := null;
			End If;

		End If;

		update geo_ordlig set
			frais_pu = ld_frais_pu,
			frais_unite = ls_frais_unite
		where orl_ref = cur_orl_ref;
		commit;

		If ld_prix_mini is not null and ld_prix_mini > 0 and substr(ls_col_code,1,2) <>'CP' and arg_soc_code <> 'IMP' and arg_soc_code <> 'BUK' then
			update geo_ordlig set ach_dev_pu = ld_prix_mini, ach_dev_code = 'EUR' where orl_ref = cur_orl_ref;
			commit;

			If soc_dev_code ='EUR' THEN
				update geo_ordlig set ach_dev_taux = 1 where orl_ref = cur_orl_ref;
				commit;
			ELSE
				select  dev_tx into ld_dev_taux
				from geo_devise_ref
				where dev_code_ref = soc_dev_code and dev_code ='EUR';

				ld_ach_pu := ld_prix_mini*ld_dev_taux;

				update geo_ordlig set ach_dev_taux = ld_dev_taux where orl_ref = cur_orl_ref;
				commit;
			End IF;

            update geo_ordlig set indbloq_ach_dev_pu = 'O', ACH_BTA_CODE = 'KILO' where orl_ref = cur_orl_ref;
			commit;

			--vérification s'il existe un pu mini de renseigner pour les variétés club
			declare
				propr_code GEO_ORDLIG.propr_code%TYPE;
			begin
				select propr_code into propr_code from geo_ordlig where orl_ref = cur_orl_ref;
				if propr_code is not null then
					ls_fou_code	:= propr_code;
				end if;
			end;

			-- fin llef
		ELse
			--update geo_ordlig set indbloq_ach_dev_pu = 'N', ach_dev_pu = 0, ach_pu = 0 where orl_ref = cur_orl_ref;
			-- Bruno le 28/09/2023 ne pas mettre à zéro le prix
			update geo_ordlig set indbloq_ach_dev_pu = 'N' where orl_ref = cur_orl_ref;
			commit;
		End  If;

		-- frais à zéro pour secteur Industrie
		If 	(ls_ori_code <> 'F' and ls_ind_exclu_frais_pu ='O') or ls_sco_code ='IND' Then
			update geo_ordlig set frais_pu = 0 where orl_ref = cur_orl_ref;
			commit;
		End If;

		update geo_ordre set typ_ordre = ls_typ_ordre where ord_ref = arg_ord_ref;
		commit;

		declare
			ld_date_exp date;
			ld_date_liv date;
		begin
			if is_dluo_client is not null then
				-- si il y a des paramètres d'affichage de la dluo
				-- on récupère les date de départ et de livraison

					SELECT depdatp, livdatp
					into ld_date_exp, ld_date_liv
					FROM geo_ordre
					WHERE ord_ref = arg_ord_ref;

					if ld_date_liv is null then
						ld_date_liv := ld_date_exp;
					end if;

				-- et on récupère le libellé correspondant limité à 35
				f_genere_dluo(is_dluo_client, ld_date_exp, ld_date_liv, ls_rc, res, msg);
				ls_rc := substr(ls_rc,0,35);
				-- et on initialise le  champ correspondant dans la ligne
				update geo_ordlig set lib_dlv = ls_rc where orl_ref = cur_orl_ref;
				commit;
			end if;
		end;

		--Deb LLEF alimentation des champs de certification
		update geo_ordre set cli_ref = is_cur_cli_ref where ord_ref = arg_ord_ref;
		commit;

		begin
			select aam.mode_culture into ls_mode_culture
			from avi_art_gestion aag
			left join avi_art_mat_prem aam on aag.ref_mat_prem = aam.ref_mat_prem
			where art_ref = arg_art_ref;
		exception
			when no_data_found then
				msg := SQLERRM;
				return;
		end;

		ls_certifs := TO_CHAR(ls_mode_culture);
		--On force la certif à BIO si le mode de culture est BIO SLAM le 02/06/2021
		if ls_mode_culture = 1 or ls_mode_culture = 2 or ls_mode_culture = 10 or ls_mode_culture = 14 or ls_mode_culture = 15 then --BIO
			ls_certifs := ls_certifs || ',12';
		end if;

		for r in C_CERTIFS
		loop
			if (r.certif = 12 and ls_mode_culture <> 1 and ls_mode_culture <> 2 and ls_mode_culture <> 10 and ls_mode_culture <> 14 and ls_mode_culture <> 15) or (r.certif <> 12) then --BIO
				ls_certifs := ls_certifs || ',' || to_char(r.certif);
			end if;
		end loop;

		update geo_ordlig
		set list_certifs = ls_certifs
		where orl_ref = cur_orl_ref;
		commit;

        -- Push as last row (orl_lig)
        if orl_ref_update is null then
            declare
                last_num varchar2(2);
                push_num varchar2(2);
            begin
                SELECT COALESCE(max(orl_lig),'00')
                INTO last_num
                FROM GEO_ORDLIG
                WHERE ORD_REF = arg_ord_ref;

                push_num := trim(to_char(to_number(last_num)+1,'00'));

                update geo_ordlig
                set orl_lig = push_num
                where orl_ref = cur_orl_ref;
                commit;
            end;
        end if;

		-- ls_orl_ref := idw_lig_cde.object.orl_ref[arg_row]
		-- If ls_orl_ref ='' or isnull(ls_orl_ref) Then
		-- 	tab_ordre.commande.dw_ordlig_a.Post Event pfc_postinsertrow(arg_row)
		-- End IF

		--Fin LLEF

        -- of_init_artref_grp(cur_orl_ref, res, msg);

        -- Nouvelles regles GTIN du 11/2023
        declare
            new_gtin GEO_ORDLIG.gtin_colis_kit%type;
	        new_kit GEO_ORDLIG.art_ref_kit%type;
            cur_lig GEO_ORDLIG.orl_lig%type;
            cursor arts_kit(lig GEO_ORDLIG.orl_lig%type) is
                select orl_ref,gtin_colis_kit,art_ref_kit
                from geo_ordlig
                where ord_ref = arg_ord_ref
                AND art_ref_kit = initial_kit
                AND orl_lig > lig;
        begin
            if orl_ref_update is not null then
		        -- of_init_artref_grp(cur_orl_ref, res, msg);

                -- fetching new data
                select orl_lig,gtin_colis_kit,art_ref_kit
                into cur_lig,new_gtin,new_kit
                from geo_ordlig
                where orl_ref = cur_orl_ref;

                -- syncinc group
                for r in arts_kit(cur_lig) loop
                    update geo_ordlig set
                        art_ref_kit = new_kit,
                        gtin_colis_kit = new_gtin
                    where orl_ref = r.orl_ref;
                end loop;

                commit;
            end if;
        exception when others then
            res := 0;
            msg := msg || ' Erreur lors de l''assignation des GTIN pour le groupe d''articles' || SQLERRM;
            return;
        end;

		-- CHECK FRONT
		--DEBUT LLEF AUTOM. IND. TRANSP. + tri et emballage retrait
		-- if arg_soc_code ='UDC' and ls_sco_code = 'RET' then
			--on force l'unité de vente et d'achat à TONNE
			-- idw_lig_cde.setItem(arg_row,'vte_bta_code', 'TONNE')
			-- idw_lig_cde.setItem(arg_row,'ach_bta_code', 'TONNE')
		-- end if
		--FIN LLEF

		-- CHECK FRONT
		--DEBUT COMMANDE EDI
		--Blocage de la zone GTIN et PAL_CODE
		-- if is_edi_ord = true then
		-- 	idw_lig_cde.Object.geo_ordlig_gtin_colis_kit.Protect = 1
		-- 	idw_lig_cde.Object.pal_code.Protect = 1
		-- end if
		--Fin LLEF

	end if;

	declare
		-- art_ass varchar2(50);
		ls_new_orl_ref varchar2(50);
	begin
		of_get_article_associe(arg_art_ref, art_ass);
		-- if art_ass is not null then
		-- 	of_init_article(arg_ord_ref, art_ass, arg_soc_code, res, msg, ls_new_orl_ref);
		-- end if;
	exception when others then
		return;
	end;

	res := 1;
	msg := 'OK';
	return;
end;
/
