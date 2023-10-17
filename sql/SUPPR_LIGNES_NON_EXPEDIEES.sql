CREATE OR REPLACE PROCEDURE SUPPR_LIGNES_NON_EXPEDIEES (
    is_cur_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
) AS
    ll_cnt number;

BEGIN
    msg := '';
    res := 0;

    SELECT count(0)
    INTO
        ll_cnt
    FROM geo_ordlog G,
         geo_ordlig L
    WHERE G.ord_ref = L.ord_ref
      AND G.fou_code = L.fou_code
      AND L.ord_ref = is_cur_ord_ref
      AND G.FLAG_EXPED_FOURNNI = 'O'
      AND L.EXP_NB_COL = 0
      AND L.EXP_PDS_NET = 0
      AND L.ESP_CODE <> 'EMBALC';

    IF ll_cnt < 1 THEN
        res := 0;
        msg := 'Aucune ligne à zéro';
        return;
    ELSE
        BEGIN
            DELETE
            FROM geo_ordlig
            WHERE exists
                   (SELECT 1
                   FROM geo_ordlog G
                   WHERE geo_ordlig.ord_ref = is_cur_ord_ref
                     AND G.ord_ref = geo_ordlig.ord_ref
                     AND G.fou_code = geo_ordlig.fou_code
                     AND G.FLAG_EXPED_FOURNNI = 'O'
                     AND geo_ordlig.EXP_NB_COL = 0
                     AND geo_ordlig.EXP_PDS_NET = 0
                     AND geo_ordlig.ESP_CODE <> 'EMBALC');

        exception
            when others then
                msg := 'Erreur lors de la suppression des lignes à 0 : ' || SQLERRM;
                rollback;
                return;
        END;
        commit;
    END IF;

    msg := ll_cnt || ' ligne(s) supprimée(s)';
    res := 1;
    return;

END;
/