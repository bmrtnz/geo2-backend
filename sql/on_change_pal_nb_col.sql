-- on_change_pal_nb_col

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_PAL_NB_COL" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    gs_user GEO_USER.nom_utilisateur%type,
	res out number,
    msg out varchar2
) AS
    ls_pal_nb_col number;
    ls_sco_code varchar2(50);
    cde_nb_pal number;
    ll_nb_pal_calc number;
    ll_pal_nb_palinter number;
    ls_vte_bta varchar2(50);
    ls_ach_bta varchar2(50);
    ls_typ_ordre varchar2(50);
    ll_pal_nb_col number;
    ls_pal_code varchar2(50);
    ls_art_ref varchar2(50);
    ls_user_sco_code varchar2(50);
begin

    msg := '';
    res := 0;

    select ol.pal_code, ol.art_ref, ol.pal_nb_col, o.sco_code, ol.cde_nb_pal, ol.pal_nb_palinter, ol.vte_bta_code, ol.ach_bta_code, o.typ_ordre
    into ls_pal_code, ls_art_ref, ls_pal_nb_col, ls_sco_code, cde_nb_pal, ll_pal_nb_palinter, ls_vte_bta, ls_ach_bta, ls_typ_ordre
    from geo_ordlig ol
    left join geo_ordre o on o.ord_ref = ol.ord_ref
    where orl_ref = arg_orl_ref;

    select sco_code
    into ls_user_sco_code
    from geo_user
    where nom_utilisateur = gs_user;

    begin
        select
            case P.dim_code
                when '1' then case when CS.COL_XB IS NOT NULL AND CS.COL_XH IS NOT NULL THEN CS.COL_XB * CS.COL_XH ELSE C.COL_XB * C.COL_XH END
                when '8' then case when CS.COL_YB IS NOT NULL AND CS.COL_YH IS NOT NULL THEN CS.COL_YB * CS.COL_YH ELSE C.COL_YB * C.COL_YH END
                when '6' then case when CS.COL_ZB IS NOT NULL AND CS.COL_ZH IS NOT NULL THEN CS.COL_ZB * CS.COL_ZH ELSE C.COL_ZB * C.COL_ZH END
            END
        into
            ll_pal_nb_col
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
            CS.SCO_CODE (+)= ls_user_sco_code;
    end;

    if ls_sco_code <> 'F' then

        if ls_pal_nb_col is not null then
            ll_nb_pal_calc := cde_nb_pal;

            If cde_nb_pal = 0 or cde_nb_pal is null then
                ll_nb_pal_calc := 1;
            end if;

            if ll_pal_nb_palinter =  1  then
                If cde_nb_pal = 0 or cde_nb_pal is null then
                    ll_pal_nb_palinter := 1;
                Else
                    ll_pal_nb_palinter := 2;
                End If;
            End If;

            if ll_pal_nb_palinter =  0 or  ll_pal_nb_palinter is null then
                ll_pal_nb_palinter := 1;
            end if;

            update geo_ordlig
            set cde_nb_col = ls_pal_nb_col * ll_nb_pal_calc * ll_pal_nb_palinter
            where orl_ref = arg_orl_ref;
            commit;
        End IF;
    else
        If (ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR') or  (ls_vte_bta <> 'UNITE'  and ls_ach_bta <> 'UNITE')	then
            of_repartition_palette(arg_orl_ref, ls_sco_code, gs_user, res, msg);
            if (res <> 1) then return; end if;
        End If;
    End IF;

    declare
        cde_nb_col number;
    begin
        select ol.cde_nb_col
        into cde_nb_col
        from geo_ordlig ol
        where orl_ref = arg_orl_ref;

        if ls_sco_code = 'F' then
            if (cde_nb_col is not null and cde_nb_col <> 0) and ls_pal_nb_col = 0 then
                update geo_ordlig
                set pal_nb_col = ll_pal_nb_col
                where orl_ref = arg_orl_ref;
                commit;
            end if;

            if (cde_nb_col is null or cde_nb_col = 0) and ls_pal_nb_col <> 0 then
                update geo_ordlig
                set pal_nb_col = 0
                where orl_ref = arg_orl_ref;
                commit;
            end if;

            If (ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR') or  (ls_vte_bta <> 'UNITE'  and ls_ach_bta <> 'UNITE')	then
                of_repartition_palette(arg_orl_ref, ls_sco_code, gs_user, res, msg);
                if (res <> 1) then return; end if;
            End If;
        end if;
    end;

    msg := 'OK';
    res := 1;
    return;

end;
