-- of_init_article(arg_art_ref)

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_INIT_ARTICLE" (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_art_ref in AVI_ART_GESTION.ART_REF%TYPE,
	arg_soc_code in geo_societe.SOC_CODE%TYPE,
	-- arg_sco_code in geo_SECCOM.SCO_CODE%TYPE,
	-- arg_typ_ordre in GEO_ORDRE.ORD_REF%TYPE,
	-- arg_ind_exclu_frais_pu in char,
    res out number,
    msg out varchar2
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
	ls_mode_culture varchar2(50);
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

	cur_orl_ref GEO_ORDLIG.orl_ref%type;
	soc_dev_code GEO_DEVISE.dev_code%type;
	is_cur_cli_ref GEO_CLIENT.cli_ref%type;
	is_dluo_client varchar2(50);

	CURSOR C_CERTIFS
	IS
		SELECT "GEO_CERTIFS_TIERS"."CERTIF"
		FROM GEO_CERTIFS_TIERS
		WHERE TIERS = is_cur_cli_ref
		AND TYP_TIERS = 'C'
		ORDER BY CERTIF;

begin

	res := 0;
	msg := '';

	select f_seq_orl_seq() into cur_orl_ref from dual;
	select dev_code into soc_dev_code from geo_societe where soc_code = arg_soc_code;
	select c.cli_ref, c.dluo, o.typ_ordre, o.ind_exclu_frais_pu, o.sco_code
	into is_cur_cli_ref, is_dluo_client, ls_typ_ordre, ls_ind_exclu_frais_pu, ls_sco_code
	from geo_ordre o
	left join geo_client c on o.cli_ref = c.cli_ref
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

	if (arg_art_ref <> '') OR (arg_art_ref IS NOT null) then

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
				res := 1;
				msg := SQLERRM;
				return;
		end;

		declare
			id_remsf number;
		begin
			-- case when ls_mdd = 'O' then
			-- 	id_remsf := id_remsf_tx_mdd;
			-- else
			-- 	id_remsf := id_remsf_tx;
			-- end if;
			insert into geo_ordlig (
				orl_ref,
				ord_ref,
				art_ref,
				esp_code
				-- remsf_tx,
				-- remhf_tx
			)
			values (
				cur_orl_ref,
				arg_ord_ref,
				ls_art_ref,
				ls_esp_code
				-- id_remsf,
				-- id_remhf_tx
			);
			commit;
		end;

		If arg_soc_code <> 'BUK' Then
						
			--New gestion des frais marketing
			select var_ristourne into ls_var_ristourne
			from geo_variet where var_code = ls_var_code;
			
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
			
			If arg_soc_code = 'BWS' THen
				ld_frais_pu := 0;
				ls_frais_unite := null;
			End If;
				
		End If;

		update geo_ordlig set
			frais_pu = ld_frais_pu,
			frais_unite = ls_frais_unite,
			var_ristourne = ls_var_ristourne
		where orl_ref = cur_orl_ref;
		commit;
		
		If ld_prix_mini is not null and ld_prix_mini > 0 and substr(ls_col_code,1,2) <>'CP' and arg_soc_code <> 'IMP' and arg_soc_code <> 'BUK' then
			update geo_ordlig set ach_dev_pu = ld_prix_mini where orl_ref = cur_orl_ref;
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
				
			update geo_ordlig set ach_dev_pu = 'O' where orl_ref = cur_orl_ref;
			commit;

			--vérification s'il existe un pu mini de renseigner pour les variétés club
			declare
				propr_code GEO_ORDLIG.propr_code%TYPE;
			begin
				select propr_code into propr_code from geo_ordlig where orl_ref = cur_orl_ref;
				if propr_code <> '' and propr_code is not null then
					ls_fou_code	:= propr_code;
				end if;
			end;
		
			-- fin llef
		ELse
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
			if is_dluo_client <> '' and is_dluo_client is not null then
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
				res := 1;
				msg := SQLERRM;
				return;
		end;
		
		ls_certifs :=  ls_certifs || ls_mode_culture;
		--On force la certif à BIO si le mode de culture est BIO SLAM le 02/06/2021
		if ls_mode_culture = '1' or ls_mode_culture ='2' or ls_mode_culture = '10' or ls_mode_culture ='14' or ls_mode_culture ='15' then --BIO
			ls_certifs := ls_certifs || ',12';
		end if;
		
		OPEN C_CERTIFS;
		loop
			fetch C_CERTIFS INTO ls_certifs_cli;
			EXIT WHEN C_CERTIFS%notfound;

			if (ls_certifs_cli = '12' and ls_mode_culture <> '1' and ls_mode_culture <> '2' and ls_mode_culture <> '10' and ls_mode_culture <>'14' and ls_mode_culture <>'15') or (ls_certifs_cli <> '12') then --BIO
				ls_certifs := ls_certifs || ',' || ls_certifs_cli;
			end if;

		end loop;
		CLOSE C_CERTIFS; 
		
		if ls_certifs is not null and ls_certifs <> '' then
			update geo_ordlig set list_certifs = ls_certifs where orl_ref = cur_orl_ref;
			commit;
		end if;

		-- ls_orl_ref := idw_lig_cde.object.orl_ref[arg_row]
		-- If ls_orl_ref ='' or isnull(ls_orl_ref) Then
		-- 	tab_ordre.commande.dw_ordlig_a.Post Event pfc_postinsertrow(arg_row)
		-- End IF

		--Fin LLEF
		of_init_artref_grp(cur_orl_ref, res, msg);
			
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

	msg := 'OK';
	return;
end;
