CREATE OR REPLACE PROCEDURE GEO_ADMIN."F_GET_INSTRUCTION_LOGISTIQUE" (
    arg_cli_ref IN GEO_ORDRE.CLI_REF%TYPE,
    arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
    res out number,
    msg out varchar2,
    ls_ins_logistique in out varchar2
)
AS
    ls_ins_logistique_client varchar2(280);
    ls_ins_logistique_entrep varchar2(280);
begin

    res := 0;

    begin
        select  instructions_logistique into ls_ins_logistique_client
        from GEO_CLIENT
        where CLI_REF = arg_cli_ref;
    exception when no_data_found then
        ls_ins_logistique_client := '';
    end;

    begin
        select  instructions_logistique into ls_ins_logistique_entrep
        from GEO_ENTREP
        where CEN_REF = arg_cen_ref;
    exception when no_data_found then
        ls_ins_logistique_entrep := '';
    end;

    ls_ins_logistique := substr(ls_ins_logistique_client || ' ' || ls_ins_logistique_entrep,1,280);

    res := 1;
    return;

end;
/