CREATE OR REPLACE PROCEDURE OF_GET_ARTICLE_BWSTOC_NON_REF (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    oc_article_cursor in out SYS_REFCURSOR,
    res OUT number,
    msg OUT varchar2
)
AS
    CURSOR CT (ref_ordre GEO_ORDRE.ORD_REF%type)
        IS
        select L.art_ref from geo_ordlig L, geo_article A
        where L.art_ref = A.art_ref
          and L.ord_ref = ref_ordre
          and L.fou_code = 'BWSTOC'
          and nvl(A.bwstock,'N') <> 'O';
BEGIN
    -- correspond à of_get_article_bwstoc_non_ref.pbl
    msg := '';
    res := 0;

    open oc_article_cursor for CT(is_ord_ref);

    res := 1;
end OF_GET_ARTICLE_BWSTOC_NON_REF;
/
