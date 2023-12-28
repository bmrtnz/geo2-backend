CREATE OR REPLACE PROCEDURE GEO_ADMIN.ON_CHANGE_ACH_DEV_PU (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    arg_soc_code GEO_SOCIETE.soc_code%type,
    res out number,
    msg out varchar2
) AS
    ls_dev_code GEO_ORDLIG.ACH_DEV_CODE%TYPE;
    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;
    ld_dev_taux number;
    ld_ach_dev_pu number;
    ld_ach_pu number;
begin
    -- correspond à on_change_ach_dev_pu.pbl
    msg := '';
    res := 0;

    select ach_dev_pu, ach_dev_code, ACH_DEV_TAUX into ld_ach_dev_pu, ls_dev_code, ld_dev_taux
    from geo_ordlig
    where orl_ref = arg_orl_ref;

    if ld_dev_taux is null or ld_dev_taux = 0 then
        msg := 'le taux de cette devise n''est pas renseigné';

        select dev_code into ls_soc_dev_code from GEO_SOCIETE where SOC_CODE = arg_soc_code;

        ls_dev_code := ls_soc_dev_code;
        ld_dev_taux := 1.0;

        update geo_ordlig set ach_dev_code = ls_dev_code, ACH_DEV_TAUX = ld_dev_taux where ORL_REF = arg_orl_ref;
    end if;

    ld_ach_pu := ld_dev_taux * ld_ach_dev_pu;
    update geo_ordlig set ach_pu = ld_ach_pu where ORL_REF = arg_orl_ref;

    res := 1;
END ON_CHANGE_ACH_DEV_PU;
/

