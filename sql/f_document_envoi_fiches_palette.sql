CREATE OR REPLACE PROCEDURE F_DOCUMENT_ENVOI_FICHES_PAL(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
BEGIN
    -- correspond à f_document_envoi_fiches_palette.pbl
    msg := '';
    res := 0;

    -- Nothing to do

    res := 1;
end F_DOCUMENT_ENVOI_FICHES_PAL;
/
