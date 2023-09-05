DROP PROCEDURE F_DOC_ENVOI_DEC_BOLLORE;

CREATE OR REPLACE PROCEDURE F_DOC_ENVOI_FACTURE_DOUANIERE(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
BEGIN
    -- correspond à f_document_envoi_declaration_bollore.pbl
    --
    -- MICROTEC : Vu avec Bruno le 18/08/2023
    -- Ce code était aussi exécuté pour le flux CUSINV
    -- Cette procédure a été renommé pour être plus cohérente et sera appeler pour les deux flux :
    -- CUSINV et DECBOL
    msg := '';
    res := 0;

    OF_SAUVE_ORDRE(is_ord_ref, res, msg);
    F_CALCUL_MARGE(is_ord_ref, res, msg);
    F_UPDATE_REM_SF(is_ord_ref, res, msg);
    OF_SAUVE_ORDRE(is_ord_ref, res, msg);
    F_CALCUL_FOB_V2(is_ord_ref, res, msg);
    -- Res doit deja être égal a 1 -> Sinon pas bien passé

    --res := 1;
end F_DOC_ENVOI_FACTURE_DOUANIERE;
/
