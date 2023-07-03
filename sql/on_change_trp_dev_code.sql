CREATE OR REPLACE PROCEDURE "GEO_ADMIN".ON_CHANGE_TRP_DEV_CODE(
    arg_ord_ref IN varchar2,
    arg_trp_dev_code IN varchar2,
    arg_soc_code IN varchar2,
    arg_trp_pu number,
    res OUT number,
    msg OUT varchar2
)
AS
    crt_trp_dev_code varchar2(50);
    soc_dev_code varchar2(50);
    ld_dev_taux number;
    ld_trp_dev_pu number;
BEGIN
    res := 0;
    msg := '';

    SELECT dev_code
    INTO soc_dev_code
    FROM GEO_SOCIETE
    WHERE soc_code = arg_soc_code;

    BEGIN
        SELECT trp_dev_code
        INTO crt_trp_dev_code
        FROM GEO_ORDRE
        WHERE ORD_REF = arg_ord_ref;
    exception when no_data_found then
        crt_trp_dev_code := null;
    END;

    if arg_trp_dev_code <> crt_trp_dev_code or crt_trp_dev_code is null then
        if arg_trp_dev_code =soc_dev_code then
            ld_dev_taux := 1.0;
        else
            begin
                select dev_tx_achat into ld_dev_taux
                from geo_devise_ref
                where dev_code = arg_trp_dev_code and
                            dev_code_ref=soc_dev_code;
            exception when no_data_found then
                msg := 'Erreur, le taux de cette devise n''est pas renseigné';
                res := 2;
                ld_dev_taux := 1.0;
                update geo_ordre set trp_dev_code = soc_dev_code where ord_ref = arg_ord_ref;
            end;
        end if;

        update geo_ordre set trp_dev_taux = ld_dev_taux where ord_ref = arg_ord_ref;

        BEGIN
            SELECT coalesce(trp_dev_taux,0)
            INTO ld_dev_taux
            FROM GEO_ORDRE
            WHERE ORD_REF = arg_ord_ref;

            if ld_dev_taux = 0 then
                msg := 'Erreur, le taux de cette devise n''est pas renseigné';
                res := 2;
                ld_dev_taux := 1.0;
            end if;

        END;
--
        ld_trp_dev_pu := arg_trp_pu / ld_dev_taux;
        update geo_ordre set trp_dev_pu = ld_trp_dev_pu where ord_ref = arg_ord_ref;

    end if;

    commit;

end;
/

