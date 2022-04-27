CREATE OR REPLACE PROCEDURE ON_CHANGE_ACH_DEV_CODE (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    arg_soc_code GEO_SOCIETE.soc_code%type,
    res out number,
    msg out varchar2
) AS
    ld_ach_dev_pu number;
    ls_dev_code GEO_ORDLIG.ACH_DEV_CODE%TYPE;
    ld_dev_taux GEO_ORDLIG.ACH_DEV_TAUX%TYPE;
    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;
    ld_ach_pu number;
begin
    -- correspond à on_change_ach_dev_code.pbl
    msg := '';
    res := 0;

    select ach_dev_code into ls_dev_code from GEO_ORDLIG where ORD_REF = arg_orl_ref;
    select dev_code into ls_soc_dev_code from GEO_SOCIETE where SOC_CODE = arg_soc_code;

    if ls_dev_code = ls_soc_dev_code  then
        ld_dev_taux := 1.0;
    else
        select dev_tx_achat into ld_dev_taux
        from geo_devise_ref
        where dev_code = ls_dev_code and
                dev_code_ref = ls_soc_dev_code;

        if ld_dev_taux is null then
            msg := 'le taux de cette devise n''est pas renseigné';
            ls_dev_code := ls_soc_dev_code;
            ld_dev_taux := 1.0;
            update geo_ordlig set ach_dev_code = ls_dev_code where ORL_REF = arg_soc_code;
        end if;
    end if;

    update GEO_ORDLIG set ach_dev_taux = ld_dev_taux where ORL_REF = arg_orl_ref;

    select ach_dev_pu, ach_dev_taux into ld_ach_dev_pu, ld_dev_taux from geo_ordlig where ORL_REF = arg_orl_ref;

    if ld_dev_taux is null or ld_dev_taux = 0 then
        msg := 'le taux de cette devise n''est pas renseigné';
        ls_dev_code := ls_soc_dev_code;
        ld_dev_taux := 1.0;

        update geo_ordlig set ach_dev_code = ls_dev_code, ACH_DEV_TAUX = ld_dev_taux where orl_ref = arg_orl_ref;
    end if;

    ld_ach_pu := ld_dev_taux * ld_ach_dev_pu;
    update GEO_ORDLIG set ach_pu = ld_ach_pu where ORL_REF = arg_orl_ref;

    res := 1;
END ON_CHANGE_ACH_DEV_CODE;
