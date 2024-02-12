CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_DECOMPTE_PALOX" (
    arg_nb_pallox IN number,
    arg_fou_code IN varchar2,
    arg_col_code IN varchar2,
    arg_cen_ref IN varchar2,
    arg_esp_code IN varchar2,
    arg_date_application IN DATE,
    arg_soc_code IN varchar2,
    res in out number,
    msg in out varchar2
) AS
    ll_count number;
BEGIN
    res := 0;

    SELECT count(*) INTO ll_count from GEO_PALCEN WHERE CEN_REF = arg_cen_ref AND FOU_CODE = arg_fou_code AND ESP_CODE = arg_esp_code AND COL_CODE = arg_col_code;

    if ll_count = 1 then
        UPDATE GEO_PALCEN SET QTE_INV = arg_nb_pallox, DATE_INV = arg_date_application WHERE  CEN_REF = arg_cen_ref AND FOU_CODE = arg_fou_code AND ESP_CODE = arg_esp_code AND COL_CODE = arg_col_code;
    else
        INSERT INTO GEO_PALCEN ( CEN_REF, SOC_CODE, FOU_CODE, ESP_CODE, COL_CODE, DATE_INV, QTE_INV )
        VALUES (  arg_cen_ref, arg_soc_code, arg_fou_code, arg_esp_code, arg_col_code, arg_date_application, arg_nb_pallox);
    end if;

    COMMIT;
    res := 1;

exception when others then
    rollback;
    msg := msg || ' ' || SQLERRM;
END;
/


