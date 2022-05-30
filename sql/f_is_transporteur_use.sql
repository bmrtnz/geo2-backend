CREATE OR REPLACE PROCEDURE F_IS_TRANSPORTEUR_USE(
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_trp_code IN GEO_TRANSP.TRP_CODE%TYPE,
    res OUT number,
    msg OUT varchar2,
    ret IN OUT boolean
)
AS
    ls_trp_code varchar2(50);
    CURSOR C_TRP IS
        SELECT TRP_CODE 
        FROM GEO_ORDLOG
        WHERE ORD_REF = arg_ord_ref;
BEGIN
    -- correspond à f_is_transporteur_use.pbl
    msg := '';
    res := 0;

    SELECT TRP_CODE 
    INTO ls_trp_code
    FROM GEO_ORDRE
    WHERE ORD_REF = arg_ord_ref;

    IF ls_trp_code is null Then
        ls_trp_code := '';
    end if;

    If ls_trp_code = arg_trp_code then
        ret := TRUE;
        return;
    end if;

    /*SELECT TRP_CODE 
    INTO 	:ls_trp_code
    FROM GEO_ORDLOG
    WHERE ORD_REF =:arg_ord_ref
    using SQLCA; */


    BEGIN
        for r in C_TRP
        loop
            IF ls_trp_code is null Then
                ls_trp_code := '';
            end if;
            If ls_trp_code = arg_trp_code then
                ret := TRUE;
                CLOSE C_TRP;
                return;
            End IF;
        end loop;
    END;

    /*
    IF isnull(ls_trp_code) Then ls_trp_code =''

    If ls_trp_code = arg_trp_code then return TRUE
    */
    ret := FALSE;
    res := 1;
    return;

end;
/
