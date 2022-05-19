CREATE OR REPLACE PROCEDURE OF_VERIF_ARTICLE_IFCO (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_soc_code geo_ordre.soc_code%type;
    ls_ifco geo_entrep.ifco%type;
    ll_count number;

    CURSOR CT (ref_ordre geo_ordre.ord_ref%type)
    IS
        select A.gest_code
        from geo_ordlig L, geo_article_colis A
        where A.art_ref = L.art_ref
        and L.ord_ref = ref_ordre;
BEGIN
    -- correspond à of_verif_article_ifco.pbl
    msg := '';
    res := 0;

    select soc_code into ls_soc_code from geo_ordre where ord_ref = arg_ord_ref;

    begin
        select E.ifco into ls_ifco
        from  geo_entrep E, geo_ordre O
        where E.cen_ref = O.cen_ref
          and O.ord_ref = arg_ord_ref;

        if ls_ifco is not null then
            res := 1;
            return;
        end if;
    exception when no_data_found then
        msg := 'vérif IFCO recherche entrepôt a échoué';
        return;
    end;

    ll_count := 0;
    for r in CT(arg_ord_ref) loop
        if r.GEST_CODE = 'IFCO' then
            ll_count := ll_count + 1;
        end if;
    end loop;

    if ll_count <> 0 then
        msg := to_char(ll_count) || ' ligne(s) avec emballage IFCO pour un entrepôt NON IFCO';
        res := 1;
        return;
    end if;

    res := 1;
end OF_VERIF_ARTICLE_IFCO;
/
