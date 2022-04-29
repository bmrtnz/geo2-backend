CREATE OR REPLACE PROCEDURE F_DOCUMENT_ENVOI_COMINV(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
BEGIN
    -- correspond à f_document_envoi_cominv.pbl
    msg := '';
    res := 0;

    F_UPDATE_REM_SF(is_ord_ref, res, msg);

    res := 1;
end F_DOCUMENT_ENVOI_COMINV;
/
