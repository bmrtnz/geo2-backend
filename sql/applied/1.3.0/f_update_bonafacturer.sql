CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_UPDATE_BONAFACTURER (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_flbaf GEO_ORDRE.FLBAF%TYPE;
BEGIN
    -- correspond à f_update_bonafacturer.pbl
    res := 0;
    msg := '';

    select FLBAF into ls_flbaf from geo_ordre where ord_ref = arg_ord_ref;

    case ls_flbaf
        when 'N' then
            begin
                update geo_ordre set flbaf = 'O' where ord_ref = arg_ord_ref;

                commit;
                res := 1;
            exception when others then
                rollback;
                msg := 'pb sur update geo_ordre.flbaf - prévenir l''informatique';
            end;
        when 'O' then
            msg := 'l''ordre est déja bon à facturer !';
        else
            msg := 'problème sur fonction f_update_bonafactuer - prévenir l''informatique - flbaf=' || ls_flbaf;
    end case;

END F_UPDATE_BONAFACTURER;
/

