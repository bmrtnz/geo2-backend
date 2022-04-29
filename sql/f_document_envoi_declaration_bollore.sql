CREATE OR REPLACE PROCEDURE F_DOC_ENVOI_DEC_BOLLORE(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
BEGIN
    -- correspond à f_document_envoi_declaration_bollore.pbl
    msg := '';
    res := 0;

    OF_SAUVE_ORDRE(is_ord_ref, res, msg);
    F_CALCUL_MARGE(is_ord_ref, res, msg);
    F_UPDATE_REM_SF(is_ord_ref, res, msg);
    OF_SAUVE_ORDRE(is_ord_ref, res, msg);
    F_CALCUL_FOB_V2(is_ord_ref, res, msg);
    -- Res doit deja être égal a 1 -> Sinon pas bien passé

    --res := 1;
end F_DOC_ENVOI_DEC_BOLLORE;
/
