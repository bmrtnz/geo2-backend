CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_VERIF_BWS (
    arg_ref_edi_ordre IN GEO_EDI_ORDRE.REF_EDI_ORDRE%TYPE,
    arg_ref_edi_ligne IN GEO_EDI_LIGNE.REF_EDI_LIGNE%TYPE,
    arg_art_ref IN GEO_ARTICLE_COLIS.ART_REF%TYPE,
	arg_cli_code IN GEO_CLIENT.CLI_CODE%TYPE,
	arg_cen_code IN GEO_ENTREP.CEN_CODE%TYPE,
	arg_esp_code IN	GEO_ARTICLE_COLIS.ESP_CODE%TYPE,
	arg_var_code IN GEO_ARTICLE_COLIS.VAR_CODE%TYPE,
    res IN OUT number,
    msg IN OUT varchar2,
    ls_plateforme OUT GEO_EDI_BWS.PLAT_CODE%TYPE
)
AS


BEGIN
    -- correspond à of_verif_bws.pbl
    res := 0;
    msg := '';



	if arg_cli_code = '' or arg_cen_code = '' then
		msg := '%%%ERREUR récupération client/entrepôt f_verif_bws : ' || to_char(arg_ref_edi_ordre) || ' edi_ligne: ' || to_char(arg_ref_edi_ligne);
		res := 0;
		return;
	end if;

	if arg_esp_code ='' or arg_var_code = '' then
		msg := '%%%ERREUR récupération article of_verif_bws art_ref: ' || arg_art_ref || ' edi_ordre: ' + to_char(arg_ref_edi_ordre) + ' edi_ligne: ' + to_char(arg_ref_edi_ligne);
		res := 0;
		return;
	end if;

	/*
	begin
        select C.cli_code, E.cen_code
		into ls_cli_code, ls_cen_code
		from geo_edi_ordre O, geo_client C, geo_entrep E
		where ref_edi_ordre = arg_ref_edi_ordre
		and C.cli_ref = O.cli_ref
		and E.cen_ref = O.cen_ref
		and E.cli_ref = C.cli_ref
		and C.valide = 'O'
		and E.valide = 'O';
    exception when others then
        msg := '%%%ERREUR récupération client/entrepôt f_verif_bws : ' || to_char(arg_ref_edi_ordre) || ' edi_ligne: ' || to_char(arg_ref_edi_ligne);
		res := 0;
        return;
    end;


    begin
        select esp_code, var_code
		into ls_esp_code, ls_var_code
		from geo_article_colis
		where art_ref = arg_art_ref
		and valide = 'O';
    exception when others then
        msg := '%%%ERREUR récupération article of_verif_bws art_ref: ' || arg_art_ref || ' edi_ordre: ' + to_char(arg_ref_edi_ordre) + ' edi_ligne: ' + to_char(arg_ref_edi_ligne);
	    res := 0;
        return;
    end;
*/

	begin
        select /*+ optimizer_features_enable('8.1.7) */ plat_code
		into ls_plateforme
		from geo_edi_bws
		where cli_code = arg_cli_code
		and cen_code = arg_cen_code
		and esp_code = arg_esp_code
		and (var_code = arg_var_code or var_code = '%');
	exception when NO_DATA_FOUND then
		ls_plateforme := '';
    end;

    res := 1;
END F_VERIF_BWS;
/
