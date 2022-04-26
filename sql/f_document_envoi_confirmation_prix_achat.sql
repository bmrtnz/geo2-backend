CREATE OR REPLACE PROCEDURE F_DOC_ENVOI_CONF_PRIX_ACHAT(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
BEGIN
    -- correspond à f_document_envoi_confirmation_prix_achat.pbl
    msg := '';
    res := 0;

    -- Nothing to do

    res := 1;
end F_DOC_ENVOI_CONF_PRIX_ACHAT;
