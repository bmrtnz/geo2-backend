CREATE OR REPLACE PROCEDURE OF_GENERE_ENVOIS (
	is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
	is_flu_code IN GEO_FLUX.FLU_CODE%TYPE,
	mode_auto IN char,
	arg_nom_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
	ib_ann_ordre IN char,
	res IN OUT number,
	msg IN OUT varchar2
)
AS
	is_soc_code GEO_SOCIETE.SOC_CODE%TYPE;
	is_cam_code GEO_CAMPAG.CAM_CODE%TYPE;
	is_nordre GEO_ORDRE.NORDRE%TYPE;
	is_per_codeass varchar2(6);
	is_per_codecom varchar2(6);
	is_cli_code varchar2(18);
	is_sco_code varchar2(3);
	is_trp_code varchar2(12);
	ls_ref_logistique_bw varchar2(70);
	ls_ref_document_bw varchar2(70);
	ls_code_expediteur varchar2(18);
	is_ref_logistique varchar2(70);
	is_ref_document varchar2(70);
	is_imprimante varchar2(35);
	ls_sql varchar(5000);
	co sys_refcursor;

	is_con_fluvar varchar2(6);
	is_moc_code varchar2(3);
	is_con_acces1 varchar2(70);
	is_con_tyt varchar2(1);
	is_con_tiers varchar2(18);
	is_con_ref varchar2(6);
	ls_con_access2 varchar2(70);
	is_con_prenom varchar2(35);
	is_con_nom varchar2(35);
	ls_con_dot varchar2(70);
	ls_con_map varchar2(70);
	is_tiers_bis_code varchar2(18);

	ll_count_exprimis number;
	pk_geo_envois GEO_ENVOIS.ENV_CODE%TYPE;

	cursor CG (ref_ordre GEO_ORDRE.ORD_REF%TYPE, code_flux GEO_FLUX.FLU_CODE%TYPE)
	is
		select K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1 , K.con_fluvar, K.con_prenom, K.con_nom
		from geo_contac K
		where K.con_tyt = 'T' and K.con_tiers in (select trp_code from geo_ordlog where ord_ref = ref_ordre group by trp_code)
		and K.flu_code = code_flux and K.valide = 'O';

	cursor CT (ref_ordre GEO_ORDRE.ORD_REF%TYPE, contact_tiers GEO_TRANSP.TRP_CODE%TYPE)
	is
		select grp_code from geo_ordlog where ord_ref = ref_ordre and trp_code = contact_tiers;

BEGIN

	begin
		select A.soc_code, A.cam_code, A.nordre, A.per_codeass, A.per_codecom, B.cli_code, A.sco_code,
			A.trp_code, A.ref_logistique, A.ref_document, C.code_expediteur
		into is_soc_code, is_cam_code, is_nordre, is_per_codeass, is_per_codecom, is_cli_code, is_sco_code,
			is_trp_code, ls_ref_logistique_bw, ls_ref_document_bw, ls_code_expediteur
		from geo_ordre A, geo_client B, geo_entrep C
		where A.ord_ref = is_ord_ref and A.cli_ref = B.cli_ref (+) and A.cen_ref = C.cen_ref (+);

		EXCEPTION WHEN NO_DATA_FOUND THEN
			IF mode_auto = 'O' then
		    	msg := 'Ordre ' || is_ord_ref || ' inconnu.';
			end if;
		    return;
	end;

	if is_soc_code is null then is_soc_code := ''; end if;
	if is_cam_code is null then is_cam_code := ''; end if;
	if is_nordre is null then is_nordre := ''; end if;
	if is_per_codeass is null then is_per_codeass := ''; end if;
	if is_per_codecom is null then is_per_codecom := ''; end if;
	if is_cli_code is null then is_cli_code := ''; end if;
	if is_sco_code is null then is_sco_code := '' ; end if;
	if ls_code_expediteur is null then ls_code_expediteur := ''; end if;

	if ls_ref_logistique_bw is not null then is_ref_logistique := ls_ref_logistique_bw || ' '; end if;
	if ls_ref_document_bw is not null then is_ref_document := ls_ref_document_bw || ' '; end if;

	select gu.imp_ref into is_imprimante
	from geo_user gu
	where gu.nom_utilisateur = arg_nom_utilisateur;

	--LLEF: Futur Envoi en automatique par mail au lieu de l'imprimante pour Flux CUSINV
	if is_flu_code = 'COMINV' or is_flu_code = 'PROFOR' or is_flu_code = 'BONLIV' or is_flu_code = 'CASINO' then
		is_con_fluvar := '';
		is_moc_code	:= 'IMP';
		is_con_acces1 := is_imprimante;
		is_con_tyt := 'C';
		is_con_tiers := is_cli_code;
		/* TODO MICROTEC
		il_row	= of_insert_envoi();		-- on crée l'envoi dans la dw  */
		return;
	end if;

	--LLEF: Futur Envoi en automatique par mail au lieu de l'imprimante pour Flux CUSINV
	if is_flu_code = 'CUSINV' then
		is_con_fluvar := '';
		is_moc_code	:= 'MAI';
		is_con_acces1 := '';
		is_con_tyt := 'C';
		is_con_tiers := is_cli_code;
		/* TODO MICROTEC
		il_row	= of_insert_envoi();		-- on crée l'envoi dans la dw  */

--		dw_table.object.acces1.visible = FALSE
--		dw_table.object.acces1_1.visible = TRUE
		return;
	end if;


	--LLEF: Envoi en automatique via FTP des déclarations douanières pour BOLLORE
	if is_flu_code = 'DECBOL'  then
		is_con_fluvar := '';
		is_moc_code	:= 'FTP';
		is_con_acces1 := '';
		is_con_tyt := 'C';
		is_con_tiers := is_cli_code;
		/* TODO MICROTEC
		il_row	= of_insert_envoi()		// on crée l'envoi dans la dw  */

--		dw_table.object.acces1.visible = TRUE
--		dw_table.object.acces1_1.visible = FALSE
		return;
	end if;

	if is_flu_code = 'BUYCO'  then
		is_con_fluvar := '';
		is_moc_code	:= 'FTP';
		is_con_acces1 := '';
		is_con_tyt := 'T';
		is_con_tiers := is_cli_code;
		/* TODO MICROTEC
		il_row	= of_insert_envoi()		// on crée l'envoi dans la dw  */
		return;
	end if;

	if ib_ann_ordre = 'N' then
		ls_sql := 'select K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1, K.con_acces2, K.con_fluvar, K.con_prenom, K.con_nom, K.con_dot, K.con_map ';
		ls_sql := ls_sql || '	from geo_contac K, geo_ordre O ';
		ls_sql := ls_sql || 'where	((K.con_tyt = ''C'' and K.con_tiers = O.cli_CODE and K.soc_code = O.soc_code) ';
		ls_sql := ls_sql || 'or (K.con_tyt = ''E'' and K.con_tiers = O.cen_code and K.soc_code = O.soc_code) ';
		ls_sql := ls_sql || 'or (K.con_tyt = ''T'' and K.con_tiers = O.trp_code) ';
		ls_sql := ls_sql || 'or (K.con_tyt = ''U'' and K.con_tiers = O.crt_code) ';
		ls_sql := ls_sql || 'or (K.con_tyt = ''S'' and K.con_tiers = O.trs_code)) ';
		ls_sql := ls_sql || 'and K.FLU_CODE = ''' || is_flu_code || '''	and K.valide = ''O'' and O.ord_ref = '''  || is_ord_ref ||''' ';

		ls_sql := ls_sql || 'union ';

		ls_sql := ls_sql ||'select K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1, K.con_acces2, K.con_fluvar, K.con_prenom, K.con_nom, K.con_dot, K.con_map ';
		ls_sql := ls_sql || 'from geo_contac K ';
		ls_sql := ls_sql ||'where K.flu_code = ''' || is_flu_code || ''' and K.valide = ''O'' and ';
		ls_sql := ls_sql ||'((K.con_tyt = ''F'' and K.con_tiers in (select fou_code from geo_ordlig where ord_ref = ''' || is_ord_ref || ''' group by fou_code))';
		If is_flu_code = 'MINI' then
			ls_sql := ls_sql || ' and not exists (select 1   from GEO_ORDLOG  where GEO_ORDLOG.ORD_REF =  ''' || is_ord_ref || ''' and  GEO_ORDLOG.VALIDE = ''O'' and GEO_ORDLOG.FLAG_EXPED_FOURNNI =''N'' and GEO_ORDLOG.FOU_CODE = K.con_tiers and ';
			ls_sql := ls_sql || ' exists (select 1 from GEO_ORDLIG where GEO_ORDLOG.ORD_REF = GEO_ORDLIG.ORD_REF and GEO_ORDLOG.FOU_CODE  = GEO_ORDLIG.FOU_CODE ))';
		End If;
		ls_sql := ls_sql || ' or ';
		--ls_sql +="(K.con_tyt = 'G' and K.con_tiers in (select grp_code from geo_ordlog where ord_ref = '"+is_ord_ref+"' group by grp_code))	or " //LLEF
		ls_sql := ls_sql || '(K.con_tyt in (''G'',''S'',''O'') and K.con_tiers in (select grp_code from geo_ordlog where ord_ref = ''' || is_ord_ref || ''' group by grp_code))	or '; --LLEF
		ls_sql := ls_sql || '(K.con_tyt = ''F'' and K.con_tiers in (select propr_code from geo_ordlig ';
		ls_sql := ls_sql || ' where ord_ref = ''' || is_ord_ref || ''' and propr_code <> fou_code and  ''' || is_flu_code || ''' <> ''ORDRE''';

		-- Bruno le 31/10/19
		-- Pour les minis n'envoyer que dans le cas ou détail est cloturé
		If is_flu_code = 'MINI' then
			ls_sql := ls_sql || ' and not exists (select 1   from GEO_ORDLOG  where GEO_ORDLOG.ORD_REF =  ''' || is_ord_ref || ''' and  GEO_ORDLOG.VALIDE =''O'' and GEO_ORDLOG.FLAG_EXPED_FOURNNI =''N'' and GEO_ORDLOG.FOU_CODE = GEO_ORDLIG.FOU_CODE and ';
			ls_sql := ls_sql || ' exists (select 1 from GEO_ORDLIG where GEO_ORDLOG.ORD_REF = GEO_ORDLIG.ORD_REF and GEO_ORDLOG.FOU_CODE  = GEO_ORDLIG.FOU_CODE ))';
		End If;
		ls_sql := ls_sql || ' group by propr_code)))';
		If is_flu_code = 'ORDRE' then
			ls_sql := ls_sql || 'union ';
			ls_sql := ls_sql || 'select ''P'', K.con_tiers, K.con_ref, K.moc_code, K.con_acces1, K.con_acces2, K.con_fluvar, K.con_prenom, K.con_nom, K.con_dot, K.con_map ';
			ls_sql := ls_sql || 'from geo_contac K ';
			ls_sql := ls_sql || 'where (K.con_tyt = ''F'' and K.con_tiers in ';
			ls_sql := ls_sql || '(select propr_code from geo_ordlig ';
			ls_sql := ls_sql || 'where ord_ref = ''' || is_ord_ref || ''' and propr_code <> fou_code  group by propr_code)) and ';
			ls_sql := ls_sql || 'flu_code= ''ACHMAR'' and K.valide = ''O''';
			--Deb LLEF Fiche palette si approche lieu de groupage
			ls_sql := ls_sql || 'union ';
			ls_sql := ls_sql ||'select ''F'', K.con_tiers, K.con_ref, K.moc_code, K.con_acces1, K.con_acces2, K.con_fluvar, K.con_prenom, K.con_nom, K.con_dot, K.con_map ';
			ls_sql := ls_sql ||'from geo_contac K ';
			ls_sql := ls_sql ||'where (K.con_tyt = ''F'' and K.con_tiers in ';
			ls_sql := ls_sql ||' (select fou_code from geo_ordlog   where ord_ref =''' || is_ord_ref || ''' and grp_code is not null and typ_fou =''F'')) and ';
			ls_sql := ls_sql ||' flu_code= ''PALGRP'' and K.valide = ''O'' ';
			--FIN LLEF
		End If;
	else --Cas annulation d'un ordre
		ls_sql := 'select E.tyt_code, E.tie_code, E.con_ref, E.moc_code, E.acces1, E.acces2, E.fluvar, K.con_prenom, K.con_nom, K.con_dot, K.con_map ';
		ls_sql := ls_sql || ' from geo_envois E, geo_contac K ';
		ls_sql := ls_sql || ' where ord_ref =''' || is_ord_ref || ''' and version_ordre = ''001'' ';
		ls_sql := ls_sql || ' and K.con_ref = E.con_ref  and K.valide = ''O'' and E.flu_code = ''ORDRE'' ';
	end if;

	open co for ls_sql;
	loop
		fetch co into is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, ls_con_access2, is_con_fluvar, is_con_prenom, is_con_nom, ls_con_dot, ls_con_map;
		exit when co%notfound;

		if ls_code_expediteur = is_con_tiers and ls_con_dot is not null then
			-- On prend une variation de flux alternative (cas de quercy prestation qui a deux logiciels)
			is_con_fluvar := ls_con_dot;
			is_con_nom := ls_con_map;
			is_con_acces1 := ls_con_access2;
		end if;

		/* TODO MICROTEC
		il_row	= of_insert_envoi() */

	end loop;
	close co;

	-- Ligne par défaut pour EXPRIMIS qque soit le transporteur
	--if is_flu_code = 'ORDRE' then
	if is_flu_code = 'ORDRE' and  ib_ann_ordre = 'N'	then
		-- On test s'il existe une station EXPRIMIS
		select count(*) into ll_count_exprimis from geo_ordre O, geo_ordlig OL, geo_contac C where O.ORD_REf = OL.ORD_REF AND OL.FOU_CODE = C.CON_TIERS AND C.CON_TYT = 'F' AND C.MOC_CODE = 'FTP' AND C.FLU_CODE = 'EXPRIM' AND O.ORD_REF = is_ord_ref;

		if ll_count_exprimis > 0 then
			is_con_fluvar := 'EXPRIM';
			is_moc_code	:= 'FTP';
			is_con_acces1 := 'EXPRIMIS';
			is_con_tyt := 'T';
			is_con_tiers := is_trp_code;
			/* TODO MICROTEC
			il_row	= of_insert_envoi()		// on crée l'envoi dans la dw */
		end if;
	end if;

	-- on traite à part le cas du transport d'approche (groupage)
	if ib_ann_ordre = 'N' then --LLEF annulation ordre

		open CG(is_ord_ref, is_flu_code);
		loop
			fetch CG into is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
			exit when CG%notfound;

			is_tiers_bis_code := '';

			--deb llef
			open CT(is_ord_ref, is_con_tiers);
			loop
				fetch CT into is_tiers_bis_code;
				exit when CT%notfound;

				/* TODO MICROTEC
				-- il faudra passer le pk_geo_envois en out
				il_row	= of_insert_envoi() */


				if is_moc_code <> 'FTP' then
					--dw_table.SetItem(il_row,'fluvar','APPROC')
					update geo_envois set fluvar = 'APPROC' where env_code = pk_geo_envois;
				else
					/*string ls_val
					ls_val = dw_table.GetItemString(il_row,'fluvar')
					if isnull(ls_val) then ls_val = ''
					dw_table.SetItem(il_row,'flu_code_compl','APPROC')*/

					update geo_envois set flu_code_compl = 'APPROC' where env_code = pk_geo_envois;
				end if;

			end loop;

			close CT;
			is_tiers_bis_code := '';
			--fin llef

		end loop;
		close CG;
	end if; --fin llef test annulation ordre


END OF_GENERE_ENVOIS;
/
