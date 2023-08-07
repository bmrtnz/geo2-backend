CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CTRL_INSERT_BWS (
	arg_bws_ecris  IN P_STR_TAB_TYPE,
	arg_art_ref IN GEO_ARTICLE_COLIS.ART_REF%TYPE,
	arg_prop_code IN GEO_FOURNI.FOU_CODE%TYPE,
	arg_fou_code IN GEO_FOURNI.FOU_CODE%TYPE,
	arg_bac_code_station IN GEO_FOURNI.BAC_CODE%TYPE,
    arg_ref_edi_ordre IN GEO_EDI_ORDRE.REF_EDI_ORDRE%TYPE,
    arg_ref_edi_ligne IN GEO_CLIENT.CLI_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2,
    ls_retour_ctrl_insert_bws OUT varchar2
)
AS
	array_data P_STR_TAB_TYPE;
	array_null P_STR_TAB_TYPE;
	ls_art_ref GEO_ARTICLE_COLIS.ART_REF%TYPE;
	ls_prop_code GEO_FOURNI.FOU_CODE%TYPE;
	ls_fou_code GEO_FOURNI.FOU_CODE%TYPE;
	ls_bac_code_station GEO_FOURNI.BAC_CODE%TYPE;
	ll_edi_ordre GEO_EDI_LIGNE.REF_EDI_ORDRE%TYPE;
	ll_edi_ligne GEO_EDI_LIGNE.REF_EDI_LIGNE%TYPE;
	ls_bws varchar2(500);
		
		
BEGIN
    -- correspond à of_ctrl_insert_bws.pbl
    res := 0;
    msg := '';

	for i in 1 .. arg_bws_ecris.count
    loop
		array_data:= array_null;
		ls_bws := arg_bws_ecris(i);
		if ls_bws is not null then
			f_split(ls_bws, ';', array_data);
			ls_art_ref 				:= array_data(1);
			ls_prop_code			:= array_data(2);
			ls_fou_code				:= array_data(3);
			ls_bac_code_station		:= array_data(4);
			ll_edi_ordre			:= to_number(array_data(5));
			ll_edi_ligne			:= to_number(array_data(6));
			if ls_art_ref = arg_art_ref and ls_prop_code = arg_prop_code and ls_fou_code = arg_fou_code and ls_bac_code_station = arg_bac_code_station and ll_edi_ordre = arg_ref_edi_ordre and ll_edi_ligne = arg_ref_edi_ligne then
				ls_retour_ctrl_insert_bws := 'KO'; --Déjà ecris
			end if;
		end if;
	end loop;


    res := 1;
END F_CTRL_INSERT_BWS;
/
