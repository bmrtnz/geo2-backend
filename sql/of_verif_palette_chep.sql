CREATE OR REPLACE PROCEDURE OF_VERIF_PALETTE_CHEP (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOc_CODE%TYPE,
    is_cur_cen_code IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_cen_gest_code GEO_ENTREP.GEST_CODE%TYPE;
    ls_sco_code GEO_ORDRE.SCO_CODE%TYPE;
    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;
    ls_pal_gest_code GEO_PALETT.GEST_CODE%TYPE;
    ls_fou_code GEO_ORDLIG.FOU_CODE%TYPE;
    ls_ret_fou_code GEO_ORDLIG.FOU_CODE%TYPE;
    ll_count number;
    ll_count_ko_lpr number;
    cursor CT (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select P.gest_code,L.fou_code
        from geo_ordlig L, geo_palett P, geo_article A
        where P.pal_code = L.pal_code
        and A.art_ref = L.art_ref
        and L.ord_ref = ref_ordre
        and A.esp_code <> 'EMBALC';
BEGIN
    -- correspond à of_verif_palette_chep.pbl
    res := 0;
    msg := '';

    -- vérifie adéquation entrepôt et palettes CHEP ou non CHEP
    select E.gest_code,O.sco_code,O.typ_ordre
    into ls_cen_gest_code, ls_sco_code, ls_typ_ordre
    from  geo_entrep E, geo_ordre O
    where E.cen_ref = O.cen_ref
      and O.ord_ref = is_ord_ref;

    if substr(is_cur_cen_code, 1, 8) = 'PREORDRE' OR (is_soc_code = 'BWS' and ls_sco_code <> 'GB') OR (ls_typ_ordre='RGP') then
        return;
    end if;

    ll_count := 0;
    OPEN CT (is_ord_ref);
    loop
        fetch CT into ls_pal_gest_code, ls_fou_code;
        EXIT WHEN CT%notfound;

        if ls_cen_gest_code = 'CHEP' then
            if ls_pal_gest_code <> 'CHEP' AND ls_pal_gest_code <> 'LPR' then -- LLEF
                ll_count := ll_count + 1;
            end if;
            if (ls_fou_code = 'NOVAC' and ls_pal_gest_code = 'CHEP') or (ls_fou_code = 'CROQUEFRUIT' and ls_pal_gest_code = 'CHEP') then
                ll_count_ko_lpr := ll_count_ko_lpr + 1;
                ls_ret_fou_code := ls_fou_code;
            end if;
        else
            if ls_pal_gest_code = 'CHEP' then
                ll_count := ll_count + 1;
            end if;
        end if;
    end loop;
    close CT;

    if ll_count <> 0 then
        if ls_cen_gest_code = 'CHEP' then
            msg := to_char(ll_count) || ' ligne(s) sans palette bleue ou rouge pour un entrepôt CHEP';
            return;
        else
            msg := to_char(ll_count) || ' ligne(s) avec palette bleue pour un entrepôt NORMAL';
            return;
        end if;
    else
        If (ll_count_ko_lpr <> 0 and ls_cen_gest_code = 'CHEP') Then
            msg := to_char(ll_count_ko_lpr) + ' ligne(s) sans palette  rouge pour un entrepôt CHEP de la station ' || ls_ret_fou_code;
            return;
        end if;
    end if;

    res := 1;
end OF_VERIF_PALETTE_CHEP;
/
