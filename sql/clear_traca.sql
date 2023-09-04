CREATE OR REPLACE PROCEDURE "GEO_ADMIN".CLEAR_TRACA(
    arg_orl_ref IN GEO_ORDLIG.ORL_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_ref_traca number;
    CURSOR CT2
    IS select ref_traca  from geo_traca_ligne where orl_ref = arg_orl_ref;
BEGIN
    msg := '';
    res := 0;

    OPEN CT2;
    LOOP
        FETCH CT2 into ls_ref_traca;
        EXIT WHEN CT2%notfound;

        begin
            delete from geo_traca_ligne where orl_ref = arg_orl_ref and ref_traca_ligne = ls_ref_traca;
            begin
                delete from GEO_TRACA_DETAIL_PAL where ref_traca = ls_ref_traca;
                commit;
            exception when others then
                msg := 'Erreur de suppression de geo_traca_detail_pal ref_traca:' || ls_ref_traca;
                res := 0;
                return;
            end;
        exception when others then
            msg := 'Erreur de suppression de geo_traca_ligne orl_ref:' || arg_orl_ref;
            res := 0;
            return;
        end;
    end loop;
    CLOSE CT2;

    res := 1;
end CLEAR_TRACA;
/

