CREATE OR REPLACE PROCEDURE OF_GET_ARTICLE_BWS_NON_REF (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    oc_article_cursor in out SYS_REFCURSOR,
    res OUT number,
    msg OUT varchar2
)
AS
BEGIN
    -- correspond à of_get_article_bws_non_ref.pbl
    msg := '';
    res := 0;

    open oc_article_cursor for
        select L.art_ref from geo_ordlig L, geo_article A
        where L.art_ref = A.art_ref
            and L.ord_ref = is_ord_ref
            and nvl(A.bwstock,'N') <> 'O';

    res := 1;
END OF_GET_ARTICLE_BWS_NON_REF;
/
