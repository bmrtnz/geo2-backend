CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_AJUST_PALLOX(
	gs_soc_code IN varchar2,
    arg_cen_code IN varchar2,
    arg_fou_code IN varchar2,
    arg_col_code IN varchar2,
    arg_esp_code varchar2,
    arg_nb_pallox IN number,
    arg_date_application IN date,
    arg_commentaire IN varchar2,
	arg_cli_code IN varchar2,
    res OUT number,
    msg OUT varchar2
)
AS
    arg_cen_ref varchar2(50);
	arg_station_raisoc varchar2(50);

    -- On crée un ordre de régulation
    ls_ord_ref varchar2(50);
    ls_orl_ref varchar2(50);
    ls_orx_ref varchar2(50);
    ls_ref varchar2(50);
    ls_transp varchar2(50);
    ls_nordre varchar2(50);
    ls_cmr varchar2(50);
    ls_bon_retour varchar2(50);
BEGIN
    res := 0;
    msg := '';

	select cen_ref into arg_cen_ref
	from geo_entrep
	where cen_code = arg_cen_code;

	select raisoc into arg_station_raisoc
	from geo_fourni
	where fou_code = arg_fou_code;

    ls_cmr := arg_commentaire;
    ls_bon_retour := 'Ajustement palox';
    ls_ref := substr( ls_bon_retour || '/' || ls_cmr,1,70);
    ls_transp := '-';

    f_create_ordre(gs_soc_code, arg_cli_code, arg_cen_code, ls_transp, ls_ref, true, true, arg_date_application, res, msg, ls_ord_ref);
    if res = 0 then
        rollback;
        return;
    else
        SELECT nordre INTO ls_nordre FROM GEO_ORDRE WHERE ord_ref = ls_ord_ref;
    end if;

    f_create_ligne_retour_palox(ls_ord_ref, arg_fou_code, arg_col_code, -arg_nb_pallox, true, res, msg, ls_orl_ref);
    if res = 0 then
        rollback;
        return;
    end if;

    f_create_lgt_retour_palox(ls_ord_ref, arg_fou_code, ls_ref, res, msg, ls_orx_ref);
    if res = 0 then
        rollback;
        return;
    end if;

    INSERT INTO GEO_PALCEL
        ( CEN_REF, SOC_CODE, FOU_CODE, ESP_CODE, COL_CODE, ORL_REF, NORDRE, DEPDATP, DATE_CREATION, QTE, CMR, STATION_RAISOC, BON_RETOUR, REF_CLI )
    VALUES
        (  arg_cen_ref, gs_soc_code, arg_fou_code, arg_esp_code, arg_col_code, ls_orl_ref, ls_nordre, CURRENT_DATE, CURRENT_DATE, arg_nb_pallox, ls_cmr, arg_station_raisoc , ls_bon_retour, ls_ref);

    commit;
    res := 1;

exception when others then
    rollback;
    msg := 'Info ' || SQLERRM;
end;
/

