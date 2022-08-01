-- on_change_pal_code

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_PAL_CODE" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    arg_user GEO_USER.nom_utilisateur%type,
    arg_sco_code in varchar2,
	res out number,
    msg out varchar2
) AS
    ls_art_ref varchar2(50);
    ls_pal_code varchar2(50);
    ll_pal_nb_col number;
    ll_dim_code varchar2(50);
    ls_sco_code varchar2(50);
    ls_typ_ordre varchar2(50);
    ls_vte_bta varchar2(50);
    ls_ach_bta varchar2(50);
begin

    msg := '';
    res := 0;

    select ol.pal_code, ol.art_ref, o.sco_code, ol.vte_bta_code, ol.ach_bta_code, o.typ_ordre
    into ls_pal_code, ls_art_ref, ls_sco_code, ls_vte_bta, ls_ach_bta, ls_typ_ordre
    from geo_ordlig ol
    left join geo_ordre o on o.ord_ref = ol.ord_ref
    where orl_ref = arg_orl_ref;

    If ls_pal_code is not null then 
        /*
        select
            CASE WHEN (GEO_PALETT.DIM_CODE ='1') THEN GEO_COLIS.COL_XB *GEO_COLIS.COL_XH
                WHEN (GEO_PALETT.DIM_CODE= '8') THEN GEO_COLIS.COL_YB *GEO_COLIS.COL_YH
                WHEN (GEO_PALETT.DIM_CODE ='6') THEN GEO_COLIS.COL_ZB * GEO_COLIS.COL_ZH
            END,
            GEO_PALETT.DIM_CODE
        into
            :ll_pal_nb_col, :ll_dim_code
        FROM
            GEO_PALETT , GEO_COLIS, GEO_ARTICLE_COLIS
        where
            GEO_PALETT.PAL_CODE  =:ls_pal_code and 
            GEO_COLIS.COL_CODE  = GEO_ARTICLE_COLIS.COL_CODE  and 
            GEO_ARTICLE_COLIS.ART_REF =:ls_art_ref and 
            GEO_ARTICLE_COLIS.ESP_CODE =GEO_COLIS.ESP_CODE;
        */
        select
            case P.dim_code
                when '1' then case when CS.COL_XB IS NOT NULL AND CS.COL_XH IS NOT NULL THEN CS.COL_XB * CS.COL_XH ELSE C.COL_XB * C.COL_XH END 
                when '8' then case when CS.COL_YB IS NOT NULL AND CS.COL_YH IS NOT NULL THEN CS.COL_YB * CS.COL_YH ELSE C.COL_YB * C.COL_YH END
                when '6' then case when CS.COL_ZB IS NOT NULL AND CS.COL_ZH IS NOT NULL THEN CS.COL_ZB * CS.COL_ZH ELSE C.COL_ZB * C.COL_ZH END
            END,
            P.DIM_CODE
        into
            ll_pal_nb_col, ll_dim_code
        FROM
            GEO_PALETT P,
            GEO_COLIS C,
            GEO_ARTICLE_COLIS AC,
            geo_colis_secteur CS    
        where
            P.PAL_CODE  = ls_pal_code and 
            C.COL_CODE  = AC.COL_CODE  and 
            AC.ART_REF = ls_art_ref and 
            AC.ESP_CODE = C.ESP_CODE AND
            CS.esp_code (+)= AC.esp_code and  
            CS.col_code (+)= AC.col_code and
            CS.SCO_CODE (+)= arg_sco_code;
    end if;

    if ls_sco_code = 'F' then
        update geo_ordlig
        set pal_nb_col = ll_pal_nb_col
        where orl_ref = arg_orl_ref;
        update geo_palett
        set dim_code = ll_dim_code
        where pal_code = ls_pal_code;
        commit;

        If (ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR') or  (ls_vte_bta <> 'UNITE'  and ls_ach_bta <> 'UNITE')	then																  
            of_repartition_palette(arg_orl_ref, ls_sco_code, arg_user, res, msg);
        End If;
    end if;

    msg := 'OK';
    res := 1;
    return;

end;