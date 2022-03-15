-- on_change_demipal_ind

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_DEMIPAL_IND" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    gs_user GEO_USER.nom_utilisateur%type,
	res out number,
    msg out varchar2
) AS
    ll_demipal_ind number;
    ll_pal_nb_palinter number;
    ls_sco_code varchar2(50);
    ls_ord_ref varchar2(50);
    ls_vte_bta varchar2(50);
    ls_ach_bta varchar2(50);
    ls_typ_ordre varchar2(50);
begin

    msg := '';
    res := 0;

    select ol.demipal_ind, ol.pal_nb_palinter, o.sco_code, o.ord_ref, ol.vte_bta_code, ol.ach_bta_code, o.typ_ordre
    into ll_demipal_ind, ll_pal_nb_palinter, ls_sco_code, ls_ord_ref, ls_vte_bta, ls_ach_bta, ls_typ_ordre
    from geo_ordlig ol
    left join geo_ordre o on o.ord_ref = ol.ord_ref
    where orl_ref = arg_orl_ref;

    if ll_demipal_ind = 1 then
        if ll_pal_nb_palinter > 0 then
            update geo_ordlig
            set pal_nb_palinter = 0
            where orl_ref = arg_orl_ref;
            commit;
        else
            update geo_ordlig
            set pal_nb_col = pal_nb_col / 2
            where orl_ref = arg_orl_ref;
            commit;
        end if;
    else
        update geo_ordlig
        set pal_nb_col = pal_nb_col * 2
        where orl_ref = arg_orl_ref;
        commit;
    end if;
    
    if ls_sco_code = 'F' then
        -- Il faut le forcer pour que la donnée soit à jour
        update geo_ordlig
        set demipal_ind = ll_demipal_ind
        where orl_ref = arg_orl_ref;
        commit;
        update geo_ordre
        set typ_ordre = ls_typ_ordre
        where ord_ref = ls_ord_ref;
        commit;

        If (ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR') or  (ls_vte_bta <> 'UNITE'  and ls_ach_bta <> 'UNITE')	then																  
            of_repartition_palette(arg_orl_ref, ls_sco_code, gs_user, res, msg);
            return;
        End If;
    end if;

    msg := 'OK';
    res := 1;
    return;

end;