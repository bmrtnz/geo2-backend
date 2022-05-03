CREATE OR REPLACE PROCEDURE F_DOCUMENT_ENVOI_SHIP_BUYCO(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
BEGIN
    -- correspond à f_document_envoi_shipment_buyco.pbl
    msg := '';
    res := 0;

    OF_SAUVE_ORDRE(is_ord_ref, res, msg);
    -- Res doit deja être égal a 1 -> Sinon sauvegarde pas bien passé

    --res := 1;
end F_DOCUMENT_ENVOI_SHIP_BUYCO;
/
