CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CALCUL_QTE" (
    arg_ord_ref in GEO_ORDLIG.ORD_REF%TYPE,
    arg_orl_ref in GEO_ORDLIG.ORL_REF%TYPE,
    arg_pds_brut in out number,
    arg_pds_net in out number,
    arg_ach_qte in out number,
    arg_vte_qte in out number,
    res out number,
    msg out varchar2
)
AS
    ls_orl_ref varchar2(50);
    ls_art_ref varchar2(50);
    ls_ach_bta_code varchar2(50);
    ls_vte_bta_code varchar2(50);
    ld_cde_nb_pal number;
    ld_cde_nb_col number;
    ld_pmb_per_com number;
    ld_pdnet_client number;
    ld_col_tare number;
    ld_exp_nb_pal number;
    ld_exp_nb_col number;
    ld_nb_col number;
    ld_nb_pal number;
    ll_cnt number;
BEGIN
    select
        L.orl_ref, L.art_ref, L.cde_nb_pal, L.cde_nb_col, L.ach_bta_code, L.vte_bta_code, X.u_par_colis, X.pdnet_client, C.col_tare, L.exp_nb_col, L.exp_nb_pal
    into
        ls_orl_ref, ls_art_ref, ld_cde_nb_pal, ld_cde_nb_col, ls_ach_bta_code, ls_vte_bta_code, ld_pmb_per_com, ld_pdnet_client, ld_col_tare, ld_exp_nb_col, ld_exp_nb_pal
    from
        geo_ordlig L, geo_article X, geo_colis C
    where
            L.ord_ref = arg_ord_ref and
            L.orl_ref = arg_orl_ref and
            X.art_ref = L.art_ref and
            C.esp_code = X.esp_code and
            C.col_code = X.col_code;

    If ld_pmb_per_com = 0 or ld_pmb_per_com is null Then
        ld_pmb_per_com := 1;
    end if;

    ld_nb_col := ld_cde_nb_col;
    ld_nb_pal := ld_cde_nb_pal;

    arg_pds_net	 := round(ld_pdnet_client * ld_nb_col, 0); -- poids net calculé
    arg_pds_brut := round(arg_pds_net + (ld_col_tare * ld_nb_col), 0); -- poids brut calculé
    arg_pds_brut := round(arg_pds_net + (ld_col_tare * ld_nb_col), 0); -- poids brut calculé

    -- calcul nombre unité d'achat
    arg_ach_qte := case ls_ach_bta_code
       WHEN 'COLIS' THEN ld_nb_col
       when 'KILO' THEN arg_pds_net
       WHEN 'PAL' THEN ld_nb_pal
       WHEN 'TONNE' THEN round(arg_pds_net / 1000, 0)
       WHEN 'CAMION' THEN 0
       WHEN 'UNITE' THEN ld_nb_col
       ELSE round(ld_nb_col * ld_pmb_per_com, 0)
    END;

    -- calcul nombre unité de vente
    arg_vte_qte := case ls_vte_bta_code
       WHEN 'COLIS' THEN ld_nb_col
       WHEN 'KILO' THEN arg_pds_net
       WHEN 'PAL' THEN ld_nb_pal
       WHEN 'TONNE' THEN round(arg_pds_net / 1000, 0)
       WHEN 'CAMION' THEN 0
       WHEN 'UNITE' THEN ld_nb_col
       ELSE round(ld_nb_col * ld_pmb_per_com, 0)
    end;

    update geo_ordlig
    set
        cde_pds_brut = arg_pds_brut,
        cde_pds_net = arg_pds_net
    where orl_ref = arg_orl_ref;

    res := 1;
    EXCEPTION WHEN OTHERS THEN
        res := 0;
END;
