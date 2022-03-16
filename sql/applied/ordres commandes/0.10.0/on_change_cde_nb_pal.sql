-- on_change_cde_nb_pal 
CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_CDE_NB_PAL" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    gs_sco_code in geo_seccom.SCO_CODE%TYPE,
	res out number,
    msg out varchar2
) AS
begin

    res := 0;
	msg := '';

    if gs_sco_code <> 'F' then
        
        declare
            data number;
            ls_pal_code varchar2(50);
            ls_pal_code_bis varchar2(50);
            ls_art_ref varchar2(50);
            ls_sco_code varchar2(50);
            ls_pal_nb_palinter number;
            cde_nb_col number;
            ll_pal_nb_col number;
            ll_pal_nb_col_old number;
            ls_pal_nb_col number;
            ll_pal_nb_palinter number;
        begin
            select ol.cde_nb_pal, ol.palinter_code, ol.pal_code, ol.art_ref, c.sco_code, ol.pal_nb_col, ol.pal_nb_palinter
            into data, ls_pal_code, ls_pal_code_bis, ls_art_ref, ls_sco_code, ls_pal_nb_col, ls_pal_nb_palinter
            from geo_ordlig ol
            left join geo_ordre o on o.ord_ref = ol.ord_ref
            left join geo_client c on c.cli_ref = o.cli_ref
            where ol.orl_ref = arg_orl_ref;

            if data is not null then

                If ls_pal_code is null or ls_pal_code= '-' Then
                    ls_pal_code := ls_pal_code_bis;
                end if;

                If ls_sco_code <> 'F' then
                    update geo_ordlig
                    set cde_nb_col = 0
                    where orl_ref = arg_orl_ref;
                    commit;
                    cde_nb_col := 0;
                end if;

                IF cde_nb_col is null Or cde_nb_col = 0 Then
                
                    If ls_pal_code is not null and ls_pal_code <> '' then 
                        /*
                        select CASE WHEN (GEO_PALETT.DIM_CODE ='1') THEN GEO_COLIS.COL_XB *GEO_COLIS.COL_XH
                            WHEN (GEO_PALETT.DIM_CODE= '8') THEN GEO_COLIS.COL_YB *GEO_COLIS.COL_YH
                            WHEN (GEO_PALETT.DIM_CODE ='6') THEN GEO_COLIS.COL_ZB * GEO_COLIS.COL_ZH
                            END into :ll_pal_nb_col
                        FROM  GEO_PALETT , GEO_COLIS, GEO_ARTICLE_COLIS
                        where   GEO_PALETT.PAL_CODE  =:ls_pal_code and 
                            GEO_COLIS.COL_CODE  = GEO_ARTICLE_COLIS.COL_CODE  and 
                            GEO_ARTICLE_COLIS.ART_REF =:ls_art_ref                 and 
                            GEO_ARTICLE_COLIS.ESP_CODE =GEO_COLIS.ESP_CODE
                        */
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

                    end if;
                    If ll_pal_nb_col is not null Then
                        ll_pal_nb_col_old :=  ls_pal_nb_col;
                        -- BAM le 14/11/18
                        -- ne pas modifier les colis si déjà saisi
                        IF ll_pal_nb_col_old > 0 then
                            ll_pal_nb_col := ll_pal_nb_col_old;
                        End If;
                        ls_pal_nb_col :=ll_pal_nb_col;
                    else
                        ll_pal_nb_col     := ls_pal_nb_col;
                    End if;
                    ll_pal_nb_palinter          := ls_pal_nb_palinter;

                    if ll_pal_nb_palinter =  0 or  ll_pal_nb_palinter is null   then
                        ll_pal_nb_palinter :=1;
                    Else
                        If  ll_pal_nb_palinter = 1 Then
                                        ll_pal_nb_palinter := 2 ;
                        End If;
                        
                        ll_pal_nb_col := ll_pal_nb_col/ ll_pal_nb_palinter;
                        update geo_ordlig
                        set pal_nb_col = ll_pal_nb_col
                        where orl_ref = arg_orl_ref;
                        commit;
                                    
                    End If;

                    If ll_pal_nb_col is null Then
                        ll_pal_nb_col := 0;
                    end if;
                    
                    If ll_pal_nb_col <> 0 Then
                        update geo_ordlig
                        set pal_nb_col = ll_pal_nb_col
                        where orl_ref = arg_orl_ref;
                        commit;
                    end If;

                    -- BAM le 24/08/16
                    -- Pour tout les secteurs sauf france on recalcule le nombre de colis
                                                    
                    if ll_pal_nb_col <> 0 and ll_pal_nb_col is not null then
                        update geo_ordlig
                        set cde_nb_col = data * ll_pal_nb_col * ll_pal_nb_palinter
                        where orl_ref = arg_orl_ref;
                        commit;
                    End IF;
                end if;
            End If;
            If  data = 0 or data is null then
                update geo_ordlig
                set demipal_ind = 0
                where orl_ref = arg_orl_ref;
                commit;
            end if;
        end;
    end if;

    res := 1;
	msg := 'OK';
	return;
    
END;