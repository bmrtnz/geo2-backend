-- f_get_qtt_per_bta

CREATE OR REPLACE PROCEDURE F_GET_QTT_PER_BTA (
    arg_art_ref IN geo_article.art_ref%TYPE,
    arg_bta_code IN geo_bastar.bta_code%TYPE,
    arg_nb_pal IN number,
    arg_nb_col IN number,
    arg_pds_net IN number,
    res IN OUT number,
    msg IN OUT varchar2,
    ld_qte OUT number
)
AS
    ls_button_name varchar2(50);
    ls_esp_code varchar2(50);
    ls_col_code varchar2(50);
    ls_bta_code varchar2(50);
    s_prepese varchar2(50);
    ld_pmb_per_col number := 0;
    ld_pdnet_client number := 0;
    ld_col_tare number := 0;
    ld_nb_piece number;
    ld_nb_pal number;
    ld_nb_col number;
    ld_pds_brut number;
    ld_pds_net number;
BEGIN
    msg := '';
    res := 0;

    select	
        X.u_par_colis, C.esp_code, C.col_code,  X.pdnet_client, C.col_tare, X.col_prepese
    into
        ld_pmb_per_col, ls_esp_code, ls_col_code, ld_pdnet_client, ld_col_tare, s_prepese
    from
        geo_article X, geo_colis C
    where
        X.art_ref = arg_art_ref and C.esp_code = X.esp_code and C.col_code = X.col_code;

    if ld_pmb_per_col is null then
        ld_pmb_per_col := 0;
    end if;
    ld_nb_piece	:= ld_pmb_per_col;

    ld_nb_pal			:= arg_nb_pal;			-- nbre palettes
    ld_nb_col			:= arg_nb_col;			-- nbre colis
    ld_pds_net			:= ld_pdnet_client * ld_nb_col;					-- poids net
    ld_pds_brut			:= ld_pds_net + (ld_col_tare * ld_nb_col);	-- poids brut
    ls_bta_code			:= arg_bta_code;			-- unité achat
        
    -- calcul nombre unité d'achat
    case arg_bta_code
        when 'COLIS' then
            ld_qte	:= ld_nb_col;
        when 'KILO' then
            if s_prepese = 'O' then
                ld_qte	:= ld_pds_net;
            else
                ld_qte	:= arg_pds_net;
            end if;
        when 'PAL' then
            ld_qte	:= ld_nb_pal;
        when 'TONNE' then
            if s_prepese = 'O' then
                ld_qte	:= ld_pds_net / 1000;
            else
                ld_qte	:= arg_pds_net / 1000;
            end if;
        when 'CAMION' then
            ld_qte	:= 0;
        else
            ld_qte	:= ld_nb_col * ld_nb_piece;
    end case;

    res := 1;
    msg := 'OK';
END;
/
