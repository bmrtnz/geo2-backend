CREATE OR REPLACE PROCEDURE OF_VERIF_PALETTE_CHEP (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOc_CODE%TYPE,
    is_cur_cen_code IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ll_pos number:= 0;
    ls_cen_gest_code GEO_ENTREP.GEST_CODE%TYPE;
    ls_sco_code GEO_ORDRE.SCO_CODE%TYPE;
    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;
    ls_pal_gest_code GEO_PALETT.GEST_CODE%TYPE;
    ls_fou_code GEO_ORDLIG.FOU_CODE%TYPE;
    ls_ret_fou_code GEO_ORDLIG.FOU_CODE%TYPE;
    ls_ind_usint GEO_CLIENT.IND_USINT%TYPE;

    ll_count number;
    ll_count_ko_lpr number;
    ls_station_palette_rouge p_str_tab_type := p_str_tab_type();
    cursor CT (ref_ordre GEO_ORDRE.ORD_REF%type)
        IS
        select coalesce(P.gest_code,''),L.fou_code
        from geo_ordlig L, geo_palett P, geo_article A
        where P.pal_code = L.pal_code
          and A.art_ref = L.art_ref
          and L.ord_ref = ref_ordre
          and A.esp_code <> 'EMBALC';
BEGIN
    -- correspond à of_verif_palette_chep.pbl
    res := 0;
    msg := '';

    ls_station_palette_rouge.extend();
    ls_station_palette_rouge(1) := 'NOVAC';
    ls_station_palette_rouge.extend();
    ls_station_palette_rouge(2) := 'CROQUEFRUIT';



    -- vérifie adéquation entrepôt et palettes CHEP ou non CHEP
    begin
        select E.gest_code,O.sco_code,O.typ_ordre,C.ind_usint
        into ls_cen_gest_code, ls_sco_code, ls_typ_ordre,ls_ind_usint
        from  geo_entrep E, geo_ordre O,geo_client C
        where O.ord_ref = is_ord_ref and
                O.cen_ref = E.cen_ref  and
                O.cli_ref = C.cli_ref;
    exception when others then
        msg := 'vérif CHEP recherche entrepôt a échoué ' || SQLERRM;
        return;
    end;

    if ls_ind_usint = 'O' OR (is_soc_code = 'BWS'  and ls_sco_code <> 'GB') OR ls_sco_code='PAL' or
       is_soc_code = 'QUP'  or is_soc_code = 'IMP' or is_soc_code = 'IUK'or is_soc_code = 'SpA'  or ls_sco_code = 'IND' OR ls_typ_ordre ='RGP' then
        return;
    end if;

    ll_count := 0;
    ll_count_ko_lpr := 0;
    OPEN CT (is_ord_ref);
    loop
        fetch CT into ls_pal_gest_code, ls_fou_code;
        EXIT WHEN CT%notfound;

        if ls_cen_gest_code = 'CHEP' then
            if (ls_pal_gest_code <> 'CHEP' AND ls_pal_gest_code <> 'LPR') OR ls_pal_gest_code IS NULL then -- LLEF
                ll_count := ll_count + 1;
            end if;
            for ll_int in 1 .. ls_station_palette_rouge.count loop
                    if (ls_station_palette_rouge(ll_int) = ls_fou_code and ls_pal_gest_code = 'CHEP') then
                        ll_count_ko_lpr := ll_count_ko_lpr + 1;
                        -- ls_ret_fou_code := ls_fou_code;
                        ll_pos := ll_int;
                    end if;
                end loop;
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
            msg := to_char(ll_count_ko_lpr) || ' ligne(s) sans palette  rouge pour un entrepôt CHEP de la station ' || ls_station_palette_rouge(ll_pos);
            return;
        end if;
    end if;

    res := 1;
end OF_VERIF_PALETTE_CHEP;
/
