CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_RETURN_FORFAITS_TRP(
    arg_cen_code varchar2,
    arg_inc_code varchar2,
    arg_trp_pu number,
    arg_bta_code number,
    arg_dev_code number,
    res OUT number,
    msg OUT varchar2,
    li_ret OUT number
)
AS
    ls_gcl_code varchar2(50);
BEGIN
    res := 0;
    msg := '';

    select GCL_CODE into ls_gcl_code
    from  GEO_CLIENT C,GEO_ENTREP E
    where  	C.CLI_REF  = E.CLI_REF and
                E.CEN_CODE = arg_cen_code;

    If  ls_gcl_code is null OR ls_gcl_code='' THEN
        li_ret := 0;
        res := 1;
        return;
    ENd If;

    select count(*) into li_ret
    from GEO_FORFAITS_TRP
    where	GCL_CODE =ls_gcl_code and
                INC_CODE =arg_inc_code and
                VALIDE ='O';

    --If li_ret >0 Then
    --	select TRP_PU,TRP_UNITE,TRP_DEVISE into :arg_trp_pu,:arg_bta_code,:arg_dev_code
    --	from GEO_FORFAITS_TRP
    --	where	GCL_CODE =:ls_gcl_code and
    --				INC_CODE =:arg_inc_code and
    --				VALIDE ='O'
    --	using sqlca;
    --End If

    res := 1;

end;
/


CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_RETURN_FORFAITS_TRP(
    arg_cen_code varchar2,
    arg_inc_code varchar2,
    arg_trp_dev_pu number,
    arg_bta_code varchar2,
    arg_dev_code varchar2,
    res OUT number,
    msg OUT varchar2,
    li_ret OUT number
)
AS
    ls_soc_code varchar2(50);
    ls_sco_code varchar2(50);
    ls_gcl_code varchar2(50);
BEGIN
    res := 0;
    msg := '';


    select  C.GCL_CODE,C.SOC_CODE,C.SCO_CODE into ls_gcl_code,ls_soc_code, ls_sco_code
    from  GEO_CLIENT C,GEO_ENTREP E
    where  	C.CLI_REF  = E.CLI_REF and
                E.CEN_CODE =arg_cen_code;

    If  ls_gcl_code is null Then
        ls_gcl_code := '';
    ENd If;

    select count(*) into li_ret
    from GEO_FORFAITS_TRP
    where	GCL_CODE =ls_gcl_code and
                INC_CODE =arg_inc_code and
                VALIDE ='O';

    If li_ret= 0 then
        If ls_sco_code  = 'F' THEN
            ls_gcl_code :='NONDEF';
        End If;

        select count(*) into li_ret
        from GEO_FORFAITS_TRP
        where	GCL_CODE =ls_gcl_code and
                    INC_CODE =arg_inc_code and
                    VALIDE ='O';

    ENd IF;





    -- If li_ret >0 Then
        -- select TRP_UNITE,DEV_CODE,TRP_PU into arg_bta_code, arg_dev_code,arg_trp_dev_pu
        -- from GEO_FORFAITS_TRP
        -- where	GCL_CODE =ls_gcl_code and
        --             INC_CODE =arg_inc_code and
        --             VALIDE ='O';
    -- End If;

    res := 1;

end;
/
