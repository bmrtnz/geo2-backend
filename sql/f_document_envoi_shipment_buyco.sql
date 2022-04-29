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

    -- Nothing to do
    OF_SAUVE_ORDRE(is_ord_ref, res, msg);

    res := 1;
end F_DOCUMENT_ENVOI_SHIP_BUYCO;
/
