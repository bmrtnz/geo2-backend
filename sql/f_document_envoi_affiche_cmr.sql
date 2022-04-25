CREATE OR REPLACE PROCEDURE F_DOCUMENT_ENVOI_AFFICHE_CMR(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_file_cmr GEO_ORDRE.FILE_CMR%TYPE;
BEGIN
    -- correspond à f_document_envoi_affiche_cmr.pbl
    msg := '';
    res := 0;

    -- Nothing to do

    res := 1;
end F_DOCUMENT_ENVOI_AFFICHE_CMR;
