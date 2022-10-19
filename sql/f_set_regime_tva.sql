CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_SET_REGIME_TVA" (
    arg_ord_ref in GEO_ORDLIG.ORD_REF%TYPE,
    arg_regime_tva in varchar2,
    res in out number,
    msg in out varchar2
)
AS
BEGIN
    res := 0;
    msg := msg;

    update geo_ordre set tvr_code = arg_regime_tva where ord_ref = arg_ord_ref;
    commit;

    res := 1;
END;
