CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_INIT_LIGNE_LITIGE" (
    arg_list_ref IN clob,
    is_cur_lit_ref IN GEO_LITIGE.LIT_REF%TYPE,
    is_orl_lit IN varchar2,
    -- ib_redfac IN OUT boolean,
	res out number,
    msg out varchar2,
    ll_nb_ligne out number
) AS
	-- et en avant la musique
    ls_orl_ref varchar2(50);
    ls_orl_lig varchar2(50);
    ls_art_ref varchar2(50);
    ls_ach_bta_code varchar2(50);
    ls_vte_bta_code varchar2(50);
    ls_fou_code varchar2(50);
    ls_var_code varchar2(50);
    ls_cat_code varchar2(50);
    ls_cam_code varchar2(50);
    ls_col_code varchar2(50);
    ls_ACH_DEV_CODE varchar2(50);
    ls_VTE_DEV_CODE varchar2(50);
    ls_prop_code varchar2(50);
    ls_lib_dlv varchar2(50);
    lstr_litlig varchar2(50);
    ls_lil_ref varchar2(50);
    ls_var_ristourne varchar2(50);
    ls_mdd varchar2(50);

    ls_array_lig_ref p_str_tab_type := p_str_tab_type();

    ld_exp_nb_pal number;
    ld_exp_nb_col number;
    ldc_exp_pds_net number;
    ld_vte_pu number;
    ld_vte_qte number;
    ld_ach_pu number;
    ld_vach_qte number;
    ll_truc number;
    ld_ACH_DEV_TAUX number;
    ll_row number;
    ll_lil_ref number;
    ll_u_par_colis number;
    ll_ind number;
    ll_nb_elem number;
    ldc_ol_remhf_tx number;
    ldc_ol_remsf_tx number;
    ldc_od_remhf_tx number;
    ldc_od_remsf_tx number;
    ldc_od_rem_sf_tx_mdd number;

    lb_deja_ligne boolean;

begin

    msg := '';
    res := 0;
    ll_nb_ligne := 0;

    -- ll_nb_elem	= f_string2array(6, arg_list_ref, ls_array_lig_ref[])
    f_split(arg_list_ref, ',', ls_array_lig_ref);
    ll_nb_elem := ls_array_lig_ref.count;

    for ll_ind in 1 .. ll_nb_elem loop

        declare
            ld_ach_pu_dev number;
            is_ord_ref varchar(7);
            is_gen_code varchar(6);
        begin
            ls_orl_ref := ls_array_lig_ref(ll_ind);		-- pk geo_ordlig
            select L.orl_lig, L.art_ref, L.exp_nb_pal, L.exp_nb_col, L.exp_pds_net, L.ach_pu, L.ach_bta_code, L.vte_pu, L.vte_bta_code, L.fou_code,
                    X.var_code, X.cat_code, X.cam_code, X.col_code, L.ACH_DEV_CODE, L.ACH_DEV_TAUX, O.dev_code,L.propr_code,
                    L.lib_dlv,X.u_par_colis, O.ord_ref, L.var_ristourne, L.remhf_tx,  L.remsf_tx, O.remsf_tx,O.remhf_tx,O.rem_sf_tx_mdd,X.mdd,E.gen_code
            into ls_orl_lig, ls_art_ref, ld_exp_nb_pal, ld_exp_nb_col, ldc_exp_pds_net, ld_ach_pu, ls_ach_bta_code, ld_vte_pu, ls_vte_bta_code, ls_fou_code,
                ls_var_code, ls_cat_code, ls_cam_code, ls_col_code, ls_ACH_DEV_CODE, ld_ACH_DEV_TAUX, ls_VTE_DEV_CODE,ls_prop_code,
                ls_lib_dlv ,ll_u_par_colis,is_ord_ref, ls_var_ristourne, ldc_ol_remhf_tx, ldc_ol_remsf_tx,
                    ldc_od_remsf_tx, ldc_od_remhf_tx,ldc_od_rem_sf_tx_mdd,ls_mdd,is_gen_code
            from geo_ordlig L, geo_article X, geo_ordre O, geo_espece E
            where L.orl_ref	= ls_orl_ref and X.art_ref = L.art_ref and L.ord_ref = O.ord_ref and X.esp_code = E.esp_code;

        -- pas 2 fois la même ligne d'origine
            declare
                phantom char(1);
            begin
                select 'v' into phantom from geo_litlig where orl_ref = ls_orl_ref;
                lb_deja_ligne	:= true;
            exception when no_data_found then
                lb_deja_ligne	:= false;
            end;

            if lb_deja_ligne = false then	-- c'est une ligne originale, alors on fait le taf
                    -- on ajoute à la fin
                -- ll_row	= dw_geo_litlig.InsertRow(0)
                ll_nb_ligne := ll_nb_ligne + 1;

                if ld_ACH_DEV_TAUX is null or ld_ACH_DEV_TAUX = 0 then
                    ld_ACH_DEV_TAUX := 1;
                end if;
                ld_ach_pu_dev := ld_ach_pu / ld_ACH_DEV_TAUX;

        --			of_chrono_litlig(ll_row)
                insert into geo_litlig (
                    lil_ref,
                    orl_ref,
                    lit_ref,
                    -- ordlig_orl_lig,
                    -- ordlig_art_ref,
                    -- article_var_code,
                    -- article_cat_code,
                    -- article_cam_code,
                    -- article_col_code,
                    -- ordlig_fou_code,
                    -- ordlig_propr_code,
                    -- ordlig_exp_nb_pal,
                    -- ordlig_exp_nb_col,
                    -- ordlig_exp_pds_net,
                    cli_nb_pal,
                    cli_nb_col,
                    cli_pds_net,
                    cli_pu,
                    cli_bta_code,
                    cli_qte,
                    res_nb_pal,
                    res_nb_col,
                    res_pds_net,
                    res_pu,
                    res_bta_code,
                    res_qte,
                    -- article_u_par_colis,
                    orl_lit,
                    ind_env_inc,
                    res_dev_pu,
                    -- ordlig_ach_dev_taux,
                    -- ordre_dev_code,
                    -- ordlig_ach_dev_code,
                    res_dev_code,
                    -- ordlig_ach_dev_taux,
                    res_dev_taux,
                    -- ordlig_vte_pu,
                    -- ordlig_vte_bta_code,
                    -- ordlig_ach_pu,
                    -- ordlig_ach_dev_pu,
                    -- ordlig_ach_bta_code,
                    -- ordlig_var_ristourne,
                    -- ordlig_remhf_tx,
                    -- ordlig_remsf_tx,
                    -- ordre_remsf_tx,
                    -- ordre_remhf_tx,
                    -- ordre_rem_sf_tx_mdd,
                    -- article_mdd,
                    cli_ind_forf,
                    res_ind_forf
                ) values (
                    -- on initialise la nlle ligne
                    F_SEQ_LIL_NUM(),
                    ls_orl_ref,
                    is_cur_lit_ref,
                    -- ls_orl_lig,
                    -- ls_art_ref,
                    -- ls_var_code,
                    -- ls_cat_code,
                    -- ls_cam_code,
                    -- ls_col_code,
                    -- ls_fou_code,
                    -- ls_prop_code,
                    -- ld_exp_nb_pal,
                    -- ld_exp_nb_col,
                    -- ldc_exp_pds_net,
                    0,
                    0,
                    0,
                    ld_vte_pu,
                    ls_vte_bta_code,
                    0,
                    0,
                    0,
                    0,
                    ld_ach_pu,
                    ls_ach_bta_code,
                    0,
                    -- ll_u_par_colis,
                    is_orl_lit,
                    'N',
                    ld_ach_pu_dev,
                    -- ld_ACH_DEV_TAUX,
                    -- ls_VTE_DEV_CODE,
                    -- ls_ACH_DEV_CODE,
                    ls_ACH_DEV_CODE,
                    -- ld_ACH_DEV_TAUX,
                    ld_ACH_DEV_TAUX,
                    -- ld_vte_pu,
                    -- ls_vte_bta_code,
                    -- ld_ach_pu,
                    -- ld_ach_pu_dev,
                    -- ls_ach_bta_code,
                    -- ls_var_ristourne,
                    -- ldc_ol_remhf_tx,
                    -- ldc_ol_remsf_tx,
                    -- ldc_od_remsf_tx,
                    -- ldc_od_remhf_tx,
                    -- ldc_od_rem_sf_tx_mdd,
                    -- ls_mdd,
                    'N',
                    'N'
                );
                commit;
            end if;
        end;
    end loop;

    -- IF lb_deja_ligne = false Then
    --     If ib_redfac   = True Then
    --         cb_auto.triggerevent("clicked")
    --     End If
    -- End If

    res := 1;

end;
/

