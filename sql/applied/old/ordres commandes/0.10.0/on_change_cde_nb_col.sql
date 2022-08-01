-- on_change_cde_nb_col

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_CDE_NB_COL" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    gs_user GEO_USER.nom_utilisateur%type,
	res out number,
    msg out varchar2
) AS
    pal_nb_col number;
    ls_sco_code varchar2(50);
    ls_vte_bta varchar2(50);
    ls_ach_bta varchar2(50);
    ls_typ_ordre varchar2(50);
begin

    msg := '';
    res := 0;

    select ol.pal_nb_col, o.sco_code, ol.vte_bta_code, ol.ach_bta_code, o.typ_ordre
    into pal_nb_col, ls_sco_code, ls_vte_bta, ls_ach_bta, ls_typ_ordre
    from geo_ordlig ol
    left join geo_ordre o on o.ord_ref = ol.ord_ref
    where orl_ref = arg_orl_ref;

    declare
        cde_nb_col number;
    begin
        select ol.cde_nb_col
        into cde_nb_col
        from geo_ordlig ol
        where orl_ref = arg_orl_ref;

        if ls_sco_code = 'F' then
            if (cde_nb_col is not null and cde_nb_col <> 0) and pal_nb_col = 0 then
                update geo_ordlig
                set pal_nb_col = cde_nb_col
                where orl_ref = arg_orl_ref;
                commit;
            end if;
            
            if (cde_nb_col is null or cde_nb_col = 0) and pal_nb_col <> 0 then
                update geo_ordlig
                set pal_nb_col = 0
                where orl_ref = arg_orl_ref;
                commit;
            end if;

            If (ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR') or  (ls_vte_bta <> 'UNITE'  and ls_ach_bta <> 'UNITE')	then	
                of_repartition_palette(arg_orl_ref, ls_sco_code, gs_user, res, msg);
                return;
            End If;
        end if;
    end;

    msg := 'OK';
    res := 1;
    return;

end;