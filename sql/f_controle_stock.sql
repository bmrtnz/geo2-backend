CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CONTROLE_STOCK (
	arg_station_or_bassin IN varchar2,
	arg_art_ref IN GEO_ARTICLE_COLIS.ART_REF%TYPE,
    arg_ref_edi_ordre IN GEO_EDI_ORDRE.REF_EDI_ORDRE%TYPE,
    arg_ref_edi_ligne IN GEO_CLIENT.CLI_REF%TYPE,
    arg_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
	arg_type_recherche IN varchar2,
	arg_bws_ecris  IN OUT P_STR_TAB_TYPE,
	arg_recherche IN char, -- 'S': simplifié ou 'D' détaillé
    res IN OUT number,
    msg IN OUT varchar2
)
AS

	ll_qte_cde number;
	ls_unite_qtt GEO_EDI_LIGNE.UNITE_QTT%TYPE;
	ls_sto_ref GEO_STOCK.STO_REF%TYPE;
	ls_fou_code GEO_FOURNI.FOU_CODE%TYPE;
	ls_age GEO_STOCK.AGE%TYPE;
	ls_bac_code_station GEO_FOURNI.BAC_CODE%TYPE;
	ls_prop_code GEO_FOURNI.FOU_CODE%TYPE;
	ll_qte_restant_stock number;
	ls_cen_ref GEO_ENTREP.CEN_REF%TYPE;
	ls_entrep_pal_code GEO_ENTREP.PAL_CODE%TYPE;
	ls_station_pal_code GEO_STOCK.PAL_CODE%TYPE;
	ls_sql varchar2(600);
	ls_retour_ctrl_insert_bws varchar2(2);
	C_STOCK SYS_REFCURSOR;
	ll_ident_fou number;
	ls_a_traiter char;
	ls_alerte_pal char;
    found_stock boolean := false;
BEGIN
    -- correspond à of_controle_stock.pbl
    res := 0;
    msg := '';

	/*
		Valeurs de arg_type_recherche:
		-------------------------------------
			- 'BWS' ==> dans la plateforme référencé pour le client / entrepôt table GEO_EDI_BWS
			- 'DANS_BASSIN' ==> Dans le bassin de l'entrepôt
			- 'HORS_BASSIN' ==> Hors du bassin de l'entrepôt
			- 'STATION' ==> Dans la station souhaité cas 'MARTINOISE' client SCAFRUITS

		Valeurs de arg_station_or_bassin:
		---------------------------------------
			- une station/ plateforme: FOU_CODE
			- une région : BAC_CODE ou '%' dans le cas ou la valeur du bac_code dans GEO_DEPT est 'INDIVIS'
	*/

    begin
        select L.quantite_colis, L.unite_qtt, cen_ref
		into ll_qte_cde, ls_unite_qtt, ls_cen_ref
		from geo_edi_ligne L, geo_edi_ordre O
		where L.ref_edi_ordre = arg_ref_edi_ordre
		and L.ref_edi_ligne = arg_ref_edi_ligne
		and O.ref_edi_ordre = L.ref_edi_ordre;
    exception when others then
        msg := '%%%ERREUR read ordre EDI of_controle_stock ref_edi_ordre: ' || to_char(arg_ref_edi_ordre) || ' ref_edi_ligne: ' || to_char(arg_ref_edi_ligne);
		res := 0;
        return;
    end;

    begin
        select E.pal_code
		into ls_entrep_pal_code
		from geo_entrep E
		where cen_ref = ls_cen_ref
		and valide = 'O';
    exception when others then
        msg := '%%%ERREUR read entrepôt cen_ref = ' || ls_cen_ref || ' ref_edi_ordre: ' || to_char(arg_ref_edi_ordre) || ' ref_edi_ligne: ' || to_char(arg_ref_edi_ligne);
	    res := 0;
        return;
    end;

	ls_sql := '';
	ls_sql := ls_sql || ' select S.sto_ref, S.fou_code, S.qte_ini - S.qte_res, S.age, F.bac_code, S.prop_code, S.pal_code, F.ident_fou ';
	ls_sql := ls_sql || ' from GEO_STOCK S, GEO_FOURNI F ';
	ls_sql := ls_sql || ' where S.art_ref = ''' || arg_art_ref || '''';
	if arg_recherche = 'S' then
		ls_sql := ls_sql || ' and (qte_ini - qte_res) >= ' || ll_qte_cde; -- il faut que la quantité en stock soit suffisante pour la qté commandée
	else
		ls_sql := ls_sql || ' and (qte_ini - qte_res) > 0 and qte_res >= 0 '; --Case qte_res négative
	end if;
	ls_sql := ls_sql || ' and F.fou_code = S.fou_code ';
	CASE arg_type_recherche
		WHEN 'BWS' THEN ls_sql := ls_sql || ' and F.fou_code = ''' || arg_station_or_bassin || '''';
		WHEN 'STATION' THEN ls_sql := ls_sql || ' and F.fou_code = ''' || arg_station_or_bassin || '''';
		WHEN 'DANS_BASSIN' THEN
			if arg_station_or_bassin = '%' then
				ls_sql := ls_sql || ' and F.bac_code like ''' || arg_station_or_bassin || '''';
			else
				ls_sql := ls_sql || ' and F.bac_code in  (' || arg_station_or_bassin || ')';
			end if;
		WHEN  'HORS_BASSIN' THEN
			if arg_station_or_bassin = '%' then
				ls_sql := ls_sql || ' and F.bac_code like ''' || arg_station_or_bassin || '''';
			else
				ls_sql := ls_sql || ' and F.bac_code not in (' || arg_station_or_bassin || ')';
			end if;
	END CASE;
	ls_sql := ls_sql || ' and S.valide = ''O'' ';
	ls_sql := ls_sql || ' and F.valide = ''O'' ';
	--ls_sql := ls_sql || ' and S.pal_code like ''' || ls_pal_code  || '''' --Si code palette est NULL alors tous les codes palettes possible
	ls_sql := ls_sql || ' order by age desc ';

	OPEN C_STOCK FOR ls_sql;
        LOOP
            fetch C_STOCK into ls_sto_ref, ls_fou_code, ll_qte_restant_stock, ls_age, ls_bac_code_station, ls_prop_code, ls_station_pal_code, ll_ident_fou;
            EXIT WHEN C_STOCK%notfound;
			ls_retour_ctrl_insert_bws := 'OK';
			ls_alerte_pal := 'N';

			if ll_ident_fou = 4 or ll_ident_fou = 5 then -- "Plateforme import", "Plateforme France"
				ls_a_traiter := 'O';
				if ls_station_pal_code <> ls_entrep_pal_code and ls_entrep_pal_code is not null and  length(ls_entrep_pal_code) > 0 then
					ls_alerte_pal := 'O';
				end if;
			else
				if ls_station_pal_code = ls_entrep_pal_code or ls_entrep_pal_code is null or ls_entrep_pal_code = '' then
					ls_a_traiter := 'O';
				else
					ls_a_traiter := 'N';
				end if;
			end if;

			if ls_a_traiter = 'O' then
				if arg_type_recherche = 'DANS_BASSIN' then
                    f_ctrl_insert_bws(arg_bws_ecris, arg_art_ref, ls_prop_code, ls_fou_code, ls_bac_code_station, arg_ref_edi_ordre, arg_ref_edi_ligne, res, msg, ls_retour_ctrl_insert_bws);
				end if;
				if ls_retour_ctrl_insert_bws = 'OK' then
                    declare
                        save_res number;
                    begin
                        f_sauve_stock(ls_sto_ref, arg_ref_edi_ordre, arg_ref_edi_ligne, arg_art_ref, arg_cam_code, arg_type_recherche, arg_bws_ecris, ls_entrep_pal_code, ls_alerte_pal, save_res, msg);  -- Return 'OK' si insert effectué
                        if save_res = 0 then
                            res:= 0;
                            return;
                        else
                            res := 1;
                            found_stock := true;
                            EXIT when arg_recherche = 'S';
                        end if;
                    end;
				end if;
			end if;
		END LOOP;
    CLOSE C_STOCK;

    if not found_stock then res := 2; end if;

END F_CONTROLE_STOCK;
/
