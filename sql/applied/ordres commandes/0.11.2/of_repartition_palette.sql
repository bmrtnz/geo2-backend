--f_repartition_palette(ord_ref)

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_REPARTITION_PALETTE" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    gs_sco_code GEO_SECCOM.sco_code%type,
    gs_user GEO_USER.nom_utilisateur%type,
	res out number,
    msg out varchar2
) AS
    ls_ord_ref varchar2(50);
    ls_pal_code varchar2(50);
    ls_sco_code varchar2(50);
    ls_art_ref GEO_ARTICLE.art_ref%type;
    ls_palett_dim_code varchar2(1);
    ll_nb_col_pal number;
    ls_pal_nb_col number;
    ls_fou_code varchar2(50);
begin

    msg := '';
    res := 0;

    select ol.pal_code, c.sco_code, ol.pal_nb_col, ol.fou_code, o.ord_ref, ol.art_ref
    into ls_pal_code, ls_sco_code, ls_pal_nb_col, ls_fou_code, ls_ord_ref, ls_art_ref
    from geo_ordlig ol
    left join geo_ordre o on o.ord_ref = ol.ord_ref
    left join geo_client c on c.cli_ref = o.cli_ref
    where ol.orl_ref = arg_orl_ref;

    if ls_sco_code = 'F' then

        SELECT
            P.dim_code,
            case P.dim_code
                when '1' then case when CS.COL_XB IS NOT NULL AND CS.COL_XH IS NOT NULL THEN CS.COL_XB * CS.COL_XH ELSE C.COL_XB * C.COL_XH END 
                when '8' then case when CS.COL_YB IS NOT NULL AND CS.COL_YH IS NOT NULL THEN CS.COL_YB * CS.COL_YH ELSE C.COL_YB * C.COL_YH END
                when '6' then case when CS.COL_ZB IS NOT NULL AND CS.COL_ZH IS NOT NULL THEN CS.COL_ZB * CS.COL_ZH ELSE C.COL_ZB * C.COL_ZH END
            end as nb_col_pal
        INTO 
            ls_palett_dim_code, ll_nb_col_pal
        FROM
            geo_colis C,   
            geo_article A,
            geo_palett P,
            geo_colis_secteur CS    
        WHERE
            A.art_ref  = ls_art_ref and  
            C.esp_code = A.esp_code and  
            C.col_code = A.col_code and
            CS.esp_code (+)= A.esp_code and  
            CS.col_code (+)= A.col_code and
            CS.SCO_CODE (+)= gs_sco_code AND 
            P.PAL_CODE = ls_pal_code;
        
        update geo_palett
        set dim_code = ls_palett_dim_code
        where pal_code = ls_pal_code;
        commit;

        ls_pal_nb_col := ll_nb_col_pal;
        if ls_pal_nb_col is not null then
            update geo_ordlig
            set pal_nb_col = ls_pal_nb_col
            where orl_ref = arg_orl_ref;
        end if;

        update geo_ordlig
        set nb_colis_manquant = 0
        where orl_ref = arg_orl_ref;
        
        commit;

        -- If gs_user.geo_client ='2' Then
        --     idw_lig_cde.SetItem(al_row,'geo_user_geo_client',2)	
        -- End If

        -- idw_lig_cde.modify('cde_nb_pal.protect="1"')

    end if;

    --if ls_pal_nb_col <> 0 then
        
        If ls_fou_code is null or ls_fou_code  = '' then
            msg := 'OK';
            res := 1;
            return;
        end if;

        declare
            ll_row number;
            nb_pal_th number := 0;
            nb_pal_dispo number := 0;
            nb_pal number;
            ld_pal_nb_col number;
            cursor cur_ols is
                select
                    orl_ref,
                    fou_code,
                    pal_nb_col,
                    demipal_ind,
                    pal_nb_palinter,
                    cde_nb_col,
                    cde_nb_pal
                from geo_ordlig
                where ord_ref = ls_ord_ref;
        begin

            for r in cur_ols
            loop

                If  ls_fou_code = r.fou_code Then
                    
                    ld_pal_nb_col := r.pal_nb_col;
                    if r.demipal_ind = 1 then
                        -- On charge des demi palettes gerbable, donc autant de palettes au sol mais on declare ensuite la moitié au trp
                        ld_pal_nb_col := ld_pal_nb_col * 1;
                    end if;
                    
                    if r.pal_nb_palinter > 0 then
                        ld_pal_nb_col := ld_pal_nb_col * (r.pal_nb_palinter + 1);
                    end if;
                    
                    if ld_pal_nb_col <> 0 then
                        -- Compte le nombre de palettes en décimal en tenant compte de la place qui reste sur le dernier calcul théorique qui est forcément <=0 
                        nb_pal_th := nb_pal_dispo + (r.cde_nb_col / ld_pal_nb_col);
                        -- Compte le nombre en valeur entière
                        nb_pal := nb_pal_dispo + (r.cde_nb_col / ld_pal_nb_col);
                    else
                        nb_pal_th := 0;
                        nb_pal := 0;
                    end if;
                    
                    -- On regarde si il reste une partie de palette occupée
                    nb_pal_th := nb_pal_th - nb_pal;
                    -- Si une partie de palette est occupée
                    if nb_pal_th > 0 then 
                        -- On ajoute une palette au sol
                        nb_pal := nb_pal + 1;
                        -- On garde la place qui reste sur cette palette
                        nb_pal_th := nb_pal_th - 1;
                    end if;
                    nb_pal_dispo := nb_pal_th;

                    update geo_ordlig
                    set cde_nb_pal = CEIL(nb_pal)
                    where orl_ref = r.orl_ref;
                    commit;

                end if;
            end loop;
        exception when others then
            res := 0;
            return;
        end;
    --end if;

    msg := 'OK';
    res := 1;
    return;

end;