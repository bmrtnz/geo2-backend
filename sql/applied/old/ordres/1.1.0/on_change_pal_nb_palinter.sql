CREATE OR REPLACE PROCEDURE ON_CHANGE_PAL_NB_PALINTER (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    arg_user GEO_USER.nom_utilisateur%type,
    res out number,
    msg out varchar2
) AS
    ls_art_ref GEO_ORDLIG.ART_REF%TYPE;
    ls_pal_code GEO_ORDLIG.PAL_CODE%TYPE;
    ls_palinter_code GEO_ORDLIG.PALINTER_CODE%TYPE;
    ls_sco_code GEO_CLIENT.SCO_CODE%TYPE;
    ld_pal_nb_palinter number;
    ll_pal_nb_col number;
    ll_nb_pal number;
    ll_nb_pal_calc number;
    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;
    ls_vte_bta GEO_ORDLIG.VTE_BTA_CODE%TYPE;
    ls_ach_bta GEO_ORDLIG.ACH_BTA_CODE%TYPE;
BEGIN
    -- correspond à on_change_pal_nb_palinter.pbl
    msg := '';
    res := 0;

    select art_ref, pal_code, PAL_NB_PALINTER into ls_art_ref, ls_pal_code, ld_pal_nb_palinter from geo_ordlig where ORL_REF = arg_orl_ref;

    if (ld_pal_nb_palinter > 0) then
        If ls_pal_code is not null Then
            select palinter_code into ls_palinter_code from GEO_ORDLIG where orl_ref = arg_orl_ref;

            If ls_palinter_code is null or ls_palinter_code = '' or ls_palinter_code = '-'  Then
                update GEO_ORDLIG set palinter_code = ls_pal_code where ORL_REF = arg_orl_ref;
            End IF;
        End IF;

        select palinter_code into ls_pal_code from GEO_ORDLIG where orl_ref = arg_orl_ref;
    else
        update GEO_ORDLIG set PALINTER_CODE = '-' where ORL_REF = arg_orl_ref;
    end if;

    select c.sco_code into ls_sco_code
    from geo_client c, geo_ordre o, geo_ordlig l
    where c.cli_ref = o.CLI_REF and o.ORD_REF = l.ORD_REF and l.ORL_REF = arg_orl_ref;

    select
        case P.dim_code
            when '1' then case when CS.COL_XB IS NOT NULL AND CS.COL_XH IS NOT NULL THEN CS.COL_XB * CS.COL_XH ELSE C.COL_XB * C.COL_XH END
            when '8' then case when CS.COL_YB IS NOT NULL AND CS.COL_YH IS NOT NULL THEN CS.COL_YB * CS.COL_YH ELSE C.COL_YB * C.COL_YH END
            when '6' then case when CS.COL_ZB IS NOT NULL AND CS.COL_ZH IS NOT NULL THEN CS.COL_ZB * CS.COL_ZH ELSE C.COL_ZB * C.COL_ZH END
            END into ll_pal_nb_col
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
            CS.SCO_CODE (+)= ls_sco_code;

    if (ld_pal_nb_palinter <> 0) then
        If ld_pal_nb_palinter = 1 Then
            ll_pal_nb_col := ll_pal_nb_col/2;
        Else
            ll_pal_nb_col := ll_pal_nb_col/ld_pal_nb_palinter;
        End If;
        update GEO_ORDLIG set PAL_NB_COL = ll_pal_nb_col where orl_ref = arg_orl_ref;
    else
        If ll_pal_nb_col is not null Then
            update GEO_ORDLIG set PAL_NB_COL = ll_pal_nb_col where orl_ref = arg_orl_ref;
        Else
            select PAL_NB_COL into ll_pal_nb_col from GEO_ORDLIG where orl_ref = arg_orl_ref;
        End IF;
    end if;

    select cde_nb_pal into ll_nb_pal from GEO_ORDLIG where ORL_REF = arg_orl_ref;
    ll_nb_pal_calc := ll_nb_pal;
    If ll_nb_pal = 0 or ll_nb_pal is null Then
        ll_nb_pal_calc := 1;
    end if;

    If ld_pal_nb_palinter = 1 Then
        If ll_nb_pal = 0 Then
            ld_pal_nb_palinter := 1;
        Else
            ld_pal_nb_palinter := 2;
        End  If;
    End If;

    If ld_pal_nb_palinter = 0 Then ld_pal_nb_palinter := 1; end if;

    if ls_sco_code <> 'F' then
        if  ll_pal_nb_col <> 0 and ll_pal_nb_col is not null then
            update geo_ordlig set cde_nb_col = ld_pal_nb_palinter * ll_nb_pal_calc * ll_pal_nb_col where ORL_REF = arg_orl_ref;
        end if;
    end if;

    if ls_sco_code = 'F' then
        select o.TYP_ORDRE, l.VTE_BTA_CODE, l.ACH_BTA_CODE into ls_typ_ordre, ls_vte_bta, ls_ach_bta
        from geo_ordre o, geo_ordlig l
        where o.ORD_REF = l.ord_ref and l.ORL_REF = arg_orl_ref;


        If (ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR') or (ls_vte_bta <> 'UNITE' and ls_ach_bta <> 'UNITE') then
            of_repartition_palette(arg_orl_ref, ls_sco_code, arg_user, res, msg);
        End If;
    end if;

    res := 1;
end ON_CHANGE_PAL_NB_PALINTER;
/

