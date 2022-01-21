CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CALCUL_MARGE" (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_fou_code varchar2(50);
    ld_trp_pu number;
    ld_trs_pu number;
    ld_crt_pu number;
    ld_remsf_tx number;
    ld_remhf_tx number;
    ld_dev_tx number;
	ls_trp_bta_code varchar2(50);
	ls_trs_bta_code varchar2(50);
    ls_crt_bta_code varchar2(50);
	ls_var_ristourne varchar2(50);
    ld_exp_nb_pal number;
    ld_exp_nb_col number;
    ld_exp_pds_net number;
    ld_exp_pds_brut number;
    ld_ach_pu number;
    ld_ach_qte number;
    ld_vte_pu number;
    ld_vte_qte number;
    ld_marge_kilo_previ number;
    ld_marge_pcent_previ number;
    ld_frais_pu_lig number;
    ld_frais_pu_ord number;
	ls_frais_unite_lig varchar2(50);
    ls_frais_unite_ord varchar2(50);
	ls_facture_avoir varchar2(50);
    -- variables de calcul au niveau de chaque ligne
    ld_vte_lig number;
    ld_rem_lig number;
    ld_res_lig number;
    ld_frd_lig number;
    ld_ach_lig number;
    ld_mob_lig number;
    ld_trp_lig number;
    ld_trs_lig number;
    ld_crt_lig number;
    ld_fad_lig number;
    -- variables de cumul des lignes
    ld_vte_lig_tot number;
    ld_rem_lig_tot number;
    ld_res_lig_tot number;
    ld_frd_lig_tot number;
    ld_ach_lig_tot number;
    ld_mob_lig_tot number;
    ld_trp_lig_tot number;
    ld_trs_lig_tot number;
    ld_crt_lig_tot number;
    ld_fad_lig_tot number;
    -- variables de calcul au niveau de l'entête (ordre)
    ld_vte_ord number;
    ld_rem_ord number;
    ld_res_ord number;
    ld_frd_ord number;
    ld_ach_ord number;
    ld_mob_ord number;
    ld_trp_ord number;
    ld_trs_ord number;
    ld_crt_ord number;
    ld_fad_ord number;
    -- variables de totalisation définitive au niveau de l'entete (ordre)
    ld_vte_ord_tot number;
    ld_rem_ord_tot number;
    ld_res_ord_tot number;
    ld_frd_ord_tot number;
    ld_ach_ord_tot number;
    ld_mob_ord_tot number;
    ld_trp_ord_tot number;
    ld_trs_ord_tot number;
    ld_crt_ord_tot number;
    ld_tot_exp_nb_pal number;
    ld_tot_exp_nb_col number;
    ld_tot_exp_pds_net number;
    ld_tot_exp_pds_brut number;
    ld_tot_exp_nb_pal_sol number;
    ld_fad_ord_tot number;
    ld_tot_cde_nb_pal number;
    ld_tot_pal_nb_sol number;
    ld_pal_nb_sol number;
    ld_pal_nb_PB100X120 number;
    ld_pal_nb_PB80X120 number;
    ld_pal_nb_PB60X80 number;
    ld_log_tot_exp_nb_pal number;
    ld_log_tot_cde_nb_pal number;
    -- indicateurs de recalcul nécessaire au niveau des lignes
    ya_trp number(1) := 0; -- indicateur de recalcul des lignes sur transport au camion (unité)
    ya_trs number(1) := 0; -- idem transit
    ya_crt number(1) := 0; -- idem courtage
    ya_frd number(1) := 0; -- idem frais marketing
    ya_fad number(1) := 0; -- frais additionnels sur l'ordre
    -- Compteur de ligne
    ll_nb_lignes number;
    ll_demipal_ind number;
    li_trp_entier number;
    ldc_trp number;
    ldc_trp_decimal number := 0;

    type tabNumber is table of number;
    ldc_tab_trp_repartition tabNumber := tabNumber();
    ls_orl_ref varchar2(50);
    ls_orl_ref_old varchar2(50);
    ldc_nb_pal_soldanslecamion number;
    ldc_nb_pal_soldanslecamion_tot number := 0;
    li_nb_pal_soldanslecamion_tot number;
    ldc_frais_plateforme number;
    ld_lig_frais_plateforme number :=0;
    ld_ord_frais_plateforme_ordre number := 0;
    li_row number := 0;
    li_row_tot number := 0;

    type tabString is table of varchar2(50);
    ls_tab_orl_ref tabString := tabString();
    ll_nb_pal_soldanslecamion_tot varchar2(50);
    ll_nb_pal_soldanslecamion_exp varchar2(50);
    li_tab_trp_repartition_exp tabString := tabString();
    ls_flag_cloture_lig varchar2(50);
    ls_typordre varchar2(50);

    -- CURSOR
    cursor C1 (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select sum(exp_nb_pal), sum(cde_nb_pal), fou_code
        from geo_ordlig where ord_ref = ref_ordre
        group by ord_ref, fou_code;

    cursor CLIG (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select L.fou_code,
               case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_pal else L.cde_nb_pal end as exp_nb_pal,
               case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_col else L.cde_nb_col end as exp_nb_col,
               case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_pds_net else L.cde_pds_net end as exp_pds_net,
               case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_pds_brut else L.cde_pds_brut end as exp_pds_brut,
               L.vte_pu, L.ach_pu, ach_qte,
               L.vte_qte, L.totvte, L.totrem, L.totres, L.totfrd, L.totach, L.totmob, L.tottrp, L.tottrs, L.totcrt,
               L.var_ristourne, L.frais_pu, L.frais_unite, L.orl_ref, LO.FLAG_EXPED_FOURNNI, L.totfad
        from geo_ordlig L, geo_ordlog LO
        where L.ord_ref = ref_ordre
          and L.ORD_REF = LO.ORD_REF
          and L.FOU_CODE = LO.FOU_CODE;

    cursor CLIG2 (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_pal else L.cde_nb_pal end  as exp_nb_pal,
               case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_col else L.cde_nb_col end as exp_nb_col,
               case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_pds_brut else L.cde_pds_brut end as exp_pds_brut,
               case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_pds_net else L.cde_pds_net end as exp_pds_net,
               totvte, tottrp, tottrs, totcrt, totfrd, totfad,demipal_ind, orl_ref ,
               case  when PAL_NB_COL = 0  or PAL_NB_COL  is null then  case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_pal else L.cde_nb_pal end
                     else
                         case when DEMIPAL_IND = 1 then case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_col else L.cde_nb_col end / (PAL_NB_COL * 2)
                              else case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_col else L.cde_nb_col end / PAL_NB_COL/ (PAL_NB_PALINTER+1)
                         END
                   END
        from geo_ordlig L, geo_ordlog LO
        where L.ord_ref = ref_ordre
          and L.ORD_REF = LO.ORD_REF
          and L.FOU_CODE = LO.FOU_CODE;

    cursor CLIG3 (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_pds_net else L.cde_pds_net end as exp_pds_net, L.orl_ref
        from geo_ordlig L, geo_ordlog LO
        where L.ord_ref = ref_ordre  and
            exists (select 1
                    from GEO_ESPECE
                    where GEO_ESPECE.ESP_CODE = L.ESP_CODE and
                            GEO_ESPECE.GEN_CODE in ('F','L'))	and
                L.ORD_REF = LO.ORD_REF  and
                L.FOU_CODE = LO.FOU_CODE;
BEGIN
    msg := '';
    res := 0;

    -- on récupère les éléments de calcul de l'ordre
    select trp_bta_code, trp_pu, trs_bta_code, trs_pu, crt_bta_code, crt_pu, remsf_tx, remhf_tx, dev_tx,
           frais_pu, frais_unite, frais_plateforme, typ_ordre
    into ls_trp_bta_code, ld_trp_pu, ls_trs_bta_code, ld_trs_pu, ls_crt_bta_code, ld_crt_pu, ld_remsf_tx, ld_remhf_tx, ld_dev_tx,
        ld_frais_pu_ord, ls_frais_unite_ord , ldc_frais_plateforme, ls_typordre
    from geo_ordre
    where ord_ref = arg_ord_ref;

    If ls_typordre = 'UKN' THEN
        msg := 'OK';
        return;
    end if;

    -- petit nettoyage
    if ls_trp_bta_code is null then ls_trp_bta_code := ''; end if;
    if ls_trs_bta_code is null then ls_trs_bta_code := ''; end if;
    if ls_crt_bta_code is null then ls_crt_bta_code := ''; end if;
    if ld_trp_pu is null then ld_trp_pu := 0; end if;
    if ld_trs_pu is null then ld_trs_pu := 0; end if;
    if ld_crt_pu is null then ld_crt_pu := 0; end if;
    if ld_remsf_tx is null then ld_remsf_tx := 0; end if;
    if ld_remhf_tx is null then ld_remhf_tx := 0; end if;
    if ld_dev_tx is null then ld_dev_tx := 0; end if;
    if ld_frais_pu_ord is null then ld_frais_pu_ord := 0; end if;
    if ls_frais_unite_ord is null then ls_frais_unite_ord := ''; end if;

    -- on calcule le chiffre d'affaires, les objectifs de marge par fournisseur et les frais au niveau des lignes
    OPEN CLIG (arg_ord_ref);
    LOOP
        FETCH CLIG INTO ls_fou_code, ld_exp_nb_pal, ld_exp_nb_col, ld_exp_pds_net, ld_exp_pds_brut, ld_vte_pu, ld_ach_pu, ld_ach_qte,
            ld_vte_qte, ld_vte_lig, ld_rem_lig, ld_res_lig, ld_frd_lig, ld_ach_lig, ld_mob_lig,
            ld_trp_lig, ld_trs_lig, ld_crt_lig,
            ls_var_ristourne, ld_frais_pu_lig, ls_frais_unite_lig, ls_orl_ref, ls_flag_cloture_lig, ld_fad_lig;
        EXIT WHEN CLIG%notfound;

        select marge_kilo_previ, marge_pcent_previ
        into ld_marge_kilo_previ, ld_marge_pcent_previ
        from geo_fourni
        where geo_fourni.fou_code = ls_fou_code;

        if ls_fou_code is null or ls_fou_code = '' then
            msg := 'calcul impossible si il manque des fournisseurs';
            return;
        end if;

        if ld_exp_nb_pal is null then ld_exp_nb_pal := 0; end if;
        if ld_exp_nb_col is null then ld_exp_nb_col := 0; end if;
        if ld_exp_pds_net is null then ld_exp_pds_net := 0; end if;
        if ld_exp_pds_brut is null then ld_exp_pds_brut := 0; end if;
        if ld_ach_pu is null then ld_ach_pu := 0; end if;
        if ld_ach_qte is null then ld_ach_qte := 0; end if;
        if ld_vte_pu is null then ld_vte_pu := 0; end if;
        if ld_vte_qte is null then ld_vte_qte := 0; end if;
        if ld_frais_pu_lig is null then ld_frais_pu_lig := 0; end if;
        if ld_marge_kilo_previ is null then ld_marge_kilo_previ := 0; end if;
        if ld_marge_pcent_previ is null then ld_marge_pcent_previ := 0; end if;
        if ls_frais_unite_lig is null then ls_frais_unite_lig := 'KILO'; end if;

        if  ls_flag_cloture_lig <> 'O' then  -- pour récupérer l'achat qte et vente qté
            f_calcul_qte(arg_ord_ref, ls_orl_ref, ld_exp_pds_brut ,ld_exp_pds_net, ld_ach_qte, ld_vte_qte, res, msg);
            if res <> 1 then
                return;
            end if;
        end if;

        -- calculs au niveau ligne
        ld_vte_lig := round(ld_vte_qte * ld_vte_pu * ld_dev_tx, 2); -- vente brute en euros

        if ls_var_ristourne = 'O' then
            ld_rem_lig := round((ld_vte_lig * (ld_remsf_tx + ld_remhf_tx)) / 100, 2);
        else
            ld_rem_lig := 0;
        end if;

        ld_ach_lig := round(ld_ach_qte * ld_ach_pu, 2);
        ld_mob_lig := round((ld_vte_lig * ld_marge_pcent_previ / 100) + (ld_marge_kilo_previ * ld_exp_pds_net), 2);
        ld_vte_ord := ld_vte_ord + ld_vte_lig;
        ld_ach_ord := ld_ach_ord + ld_ach_lig;
        ld_mob_ord := ld_mob_ord + ld_mob_lig;
        ld_rem_ord := ld_rem_ord + ld_rem_lig;

        -- calcul frais marketing niveau ligne
        ld_frd_lig := CASE ls_frais_unite_lig
            WHEN 'COLIS' THEN round(ld_exp_nb_col * ld_frais_pu_lig, 2)
            WHEN 'PAL' THEN round(ld_exp_nb_pal * ld_frais_pu_lig, 2)
            WHEN 'KILO' THEN round(ld_exp_pds_net * ld_frais_pu_lig, 2)
            WHEN 'TONNE' THEN round(ld_exp_pds_net * ld_frais_pu_lig /1000, 2)
            WHEN 'PCENT' THEN round(ld_vte_lig * ld_frais_pu_lig / 100, 2)
            WHEN 'UNITE' THEN round(ld_frais_pu_lig, 2)
            ELSE round(ld_vte_qte * ld_frais_pu_lig, 2)
        END;

        update geo_ordlig
        set totvte= ld_vte_lig, totrem = ld_rem_lig, totres = 0, totfrd = ld_frd_lig, totach = ld_ach_lig,
            totmob = ld_mob_lig, tottrp = 0, tottrs = 0, totcrt = 0, totfad = 0
        where orl_ref = ls_orl_ref;
    END LOOP;
    CLOSE CLIG;
    COMMIT; -- pour éviter de se prendre les pieds dans la moquette sur éventuel recalcul des lignes (voir plus bas)

    -- on récupère les cumuls de quantités et montant des lignes de l'ordre
    select  sum(case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_pal else L.cde_nb_pal end) as exp_nb_pal,
            sum(case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_col else L.cde_nb_col end) as exp_nb_col,
            sum( case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_pds_net else L.cde_pds_net end ) as exp_pds_net,
            sum( case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_pds_brut else L.cde_pds_brut end) as exp_pds_brut,
            sum(totvte), sum(totrem), sum(totres), sum(totfrd), sum(totach), sum(totmob),
            sum(cde_nb_pal),
            round((sum(case LO.FLAG_EXPED_FOURNNI when 'O' then L.exp_nb_pal else L.cde_nb_pal end) - sum(DEMIPAL_IND)) * 0.5)
    into ld_tot_exp_nb_pal, ld_tot_exp_nb_col, ld_tot_exp_pds_net, ld_tot_exp_pds_brut,
        ld_vte_lig_tot, ld_rem_lig_tot, ld_res_lig_tot, ld_frd_lig_tot, ld_ach_lig_tot, ld_mob_lig_tot,
        ld_tot_cde_nb_pal, ld_tot_exp_nb_pal_sol
    from geo_ordlig L, geo_ordlog LO
    where L.ord_ref = arg_ord_ref
      and L.ORD_REF = LO.ORD_REF
      and L.FOU_CODE = LO.FOU_CODE
    group by L.ord_ref;

    -- petit nettoyage
    if ld_tot_exp_nb_pal is null then ld_tot_exp_nb_pal := 0; END IF;
    if ld_tot_exp_nb_col is null then ld_tot_exp_nb_col := 0; END IF;
    if ld_tot_exp_pds_net is null then ld_tot_exp_pds_net := 0; END IF;
    if ld_tot_exp_pds_brut is null then ld_tot_exp_pds_brut := 0; END IF;
    if ld_vte_lig_tot is null then ld_vte_lig_tot := 0; END IF;
    if ld_rem_lig_tot is null then ld_rem_lig_tot := 0; END IF;
    if ld_res_lig_tot is null then ld_res_lig_tot := 0; END IF;
    if ld_frd_lig_tot is null then ld_frd_lig_tot := 0; END IF;
    if ld_ach_lig_tot is null then ld_ach_lig_tot := 0; END IF;
    if ld_mob_lig_tot is null then ld_mob_lig_tot := 0; END IF;

    -- on calcule au niveau de l'ordre
    ld_tot_exp_pds_net  := round(ld_tot_exp_pds_net, 0);
    ld_tot_exp_pds_brut	:= round(ld_tot_exp_pds_brut, 0);

    -- calcul transport
    ld_trp_ord := CASE ls_trp_bta_code
        WHEN 'COLIS' THEN round(ld_tot_exp_nb_col * ld_trp_pu, 2)
        WHEN 'PAL' THEN round(ld_tot_exp_nb_pal_sol * ld_trp_pu, 2)
        WHEN 'KILO' THEN round(ld_tot_exp_pds_net * ld_trp_pu, 2) -- brut et pas net (...)
        WHEN 'TONNE' THEN round(ld_tot_exp_pds_net * ld_trp_pu / 1000, 2) -- brutet non pas net (...)
        ELSE round(ld_trp_pu, 2) -- on considère que c'est au camion
    END;
    if ld_trp_ord <> 0 then
        ya_trp := 1; -- recalcul nécessaire pour les lignes
    end if;

    -- calcul transit
    ld_trs_ord := CASE ls_trs_bta_code
        WHEN 'COLIS' THEN round(ld_tot_exp_nb_col * ld_trs_pu, 2)
        WHEN 'PAL' THEN round(ld_tot_exp_nb_pal * ld_trs_pu, 2)
        WHEN 'KILO' THEN round(ld_tot_exp_pds_net * ld_trs_pu, 2)
        WHEN 'TONNE' THEN round(ld_tot_exp_pds_net * ld_trs_pu / 1000, 2)
        ELSE round(ld_trs_pu, 2)
    END;
    if ld_trs_ord <> 0 then
        ya_trs := 1; -- recalcul nécessaire pour les lignes
    end if;

    -- calcul courtage
    ld_crt_ord := CASE ls_crt_bta_code
        WHEN 'COLIS' THEN round(ld_tot_exp_nb_col * ld_crt_pu, 2)
        WHEN 'PAL' THEN round(ld_tot_exp_nb_pal * ld_crt_pu, 2)
        WHEN 'KILO' THEN round(ld_tot_exp_pds_net * ld_crt_pu, 2)
        WHEN 'TONNE' THEN round(ld_tot_exp_pds_net * ld_crt_pu / 1000, 2)
        WHEN 'PCENT' THEN round(ld_vte_lig_tot * ld_crt_pu / 100, 2)
        ELSE round(ld_crt_pu, 2)
    END;
    if ld_crt_ord <> 0 then
        ya_crt := 1; -- recalcul nécessaire pour les lignes
    end if;

    -- calcul frais marketing au niveau de l'ordre
    ld_frd_ord := case ls_frais_unite_ord
        WHEN 'COLIS' THEN round(ld_tot_exp_nb_col * ld_frais_pu_ord, 2)
        WHEN 'PAL' THEN round(ld_tot_exp_nb_pal * ld_frais_pu_ord, 2)
        WHEN 'KILO' THEN round(ld_tot_exp_pds_net * ld_frais_pu_ord, 2)
        WHEN 'TONNE' THEN round(ld_tot_exp_pds_net * ld_frais_pu_ord / 1000, 2)
        WHEN 'PCENT' THEN round(ld_vte_lig_tot * ld_frais_pu_ord / 100, 2)
        ELSE round(ld_frais_pu_ord, 2) -- c'est un montant à l'ordre
    end;
    if ld_frd_ord <> 0 then
        ya_frd := 1; -- recalcul nécessaire pour les lignes
    end if;

    -- frais additionnels ordre
    select sum(round(montant * dev_tx, 2)) into ld_fad_ord_tot
    from geo_ordfra
    where ord_ref = arg_ord_ref;

    if ld_fad_ord_tot is null then ld_fad_ord_tot := 0; END IF;
    if ld_fad_ord_tot <> 0 then
        ya_fad := 1;
    end if;

    -- affectation des totaux définitifs de l'ordre (pour plus de clarté sur l'origine de ces totaux)
    ld_vte_ord_tot	:= ld_vte_lig_tot;	-- cumul lignes
    ld_rem_ord_tot	:= ld_rem_lig_tot;	-- cumul lignes
    ld_res_ord_tot	:= ld_res_lig_tot;	-- cumul lignes
    ld_frd_ord_tot	:= ld_frd_lig_tot + ld_frd_ord;	-- cumul lignes + calcul entete := frais totaux de l'ordre
    ld_ach_ord_tot	:= ld_ach_lig_tot;	-- cumul lignes
    ld_mob_ord_tot	:= ld_mob_lig_tot;	-- cumul lignes
    ld_trp_ord_tot	:= ld_trp_ord;	-- calcul entete
    ld_trs_ord_tot	:= ld_trs_ord;	-- calcul entete
    ld_crt_ord_tot	:= ld_crt_ord;	-- calcul entete

    if ld_vte_ord_tot < 0 then
        ls_facture_avoir := 'A';
    else
        if ld_vte_ord_tot = 0 and ld_ach_ord_tot < 0 then
            -- Cas des litiges vente a zéro mais avoir fournisseur, pour les minis il faut que l'ordre soit identifié comme un avoir
		    ls_facture_avoir := 'A';
        else
            ls_facture_avoir := 'F';
        end if;
    end if;

    -- actualise ordre
    -- totalise les palettes au sol et palettes bleures issue de la logistique (24/08/12)
    select sum(pal_nb_sol), sum(pal_nb_PB100X120), sum(pal_nb_PB80X120), sum(pal_nb_PB60X80)
    into ld_pal_nb_sol, ld_pal_nb_PB100X120, ld_pal_nb_PB80X120, ld_pal_nb_PB60X80
    from geo_ordlog where ord_ref = arg_ord_ref; -- 24/08/12 ajout totalisations palettes de toute sorte

    update geo_ordre set
        facture_avoir = ls_facture_avoir,
        totpal = ld_tot_exp_nb_pal, totcol = ld_tot_exp_nb_col,
        totpdsnet = ld_tot_exp_pds_net, totpdsbrut = ld_tot_exp_pds_brut,
        totvte = ld_vte_ord_tot, totrem = ld_rem_ord_tot, totres = ld_res_ord_tot,
        totfrd = ld_frd_ord_tot, totach = ld_ach_ord_tot, totmob = ld_mob_ord_tot,
        tottrp = ld_trp_ord_tot, tottrs = ld_trs_ord_tot, totcrt = ld_crt_ord_tot,
        totfad = ld_fad_ord_tot, tot_cde_nb_pal	= ld_tot_cde_nb_pal, tot_exp_nb_pal	= ld_tot_exp_nb_pal,
        pal_nb_sol = ld_pal_nb_sol, pal_nb_PB100X120 = ld_pal_nb_PB100X120,
        pal_nb_PB80X120 = ld_pal_nb_PB80X120, pal_nb_PB60X80 = ld_pal_nb_PB60X80
    where ord_ref = arg_ord_ref;

    commit;

    OPEN C1 (arg_ord_ref);
    LOOP
        FETCH C1 INTO ld_log_tot_exp_nb_pal, ld_log_tot_cde_nb_pal, ls_fou_code;
        EXIT WHEN C1%notfound;

        update geo_ordlog
        set tot_cde_nb_pal = ld_log_tot_cde_nb_pal, tot_exp_nb_pal = ld_log_tot_exp_nb_pal
        where ord_ref = arg_ord_ref and fou_code = ls_fou_code;
    END LOOP;
    COMMIT;
    CLOSE C1;

    -- On compte les lignes du litige
    ll_nb_lignes := 0;

    -- y a t'il répartition du transport/transit/courtage au niveau des lignes ?
    if ya_trp = 1 or ya_trs = 1 or ya_crt = 1 or ya_frd = 1 or ya_fad = 1 or ldc_frais_plateforme > 0  then
        open CLIG2 (arg_ord_ref);
        LOOP
            fetch CLIG2 into ld_exp_nb_pal, ld_exp_nb_col, ld_exp_pds_brut, ld_exp_pds_net,
                ld_vte_lig, ld_trp_lig, ld_trs_lig, ld_crt_lig, ld_frd_lig, ld_fad_lig,
                ll_demipal_ind, ls_orl_ref, ldc_nb_pal_soldanslecamion;
            EXIT WHEN CLIG2%notfound;

            ls_orl_ref_old := ls_orl_ref;
            ll_nb_lignes := ll_nb_lignes + 1;
            ld_trp_lig := 0;
            ld_trs_lig := 0;
            ld_crt_lig := 0;
            ld_fad_lig := 0;

            if ld_frd_lig is null then ld_frd_lig := 0; end if;

            -- on ne remet pas à zero les frais ligne (on va y ajouter le prorata du calcul entete)
            if ya_trp = 1 then
                if ls_trp_bta_code = 'COLIS' THEN
                    ld_trp_lig := round(ld_exp_nb_col * ld_trp_pu, 2);
                ELSIF ls_trp_bta_code = 'PAL' THEN
                    if ldc_nb_pal_soldanslecamion > 0 then
                        li_row_tot := li_row_tot + 1;

                        ldc_tab_trp_repartition.extend(1);
                        ldc_tab_trp_repartition(li_row_tot) := ldc_nb_pal_soldanslecamion;
                       
                        ldc_nb_pal_soldanslecamion_tot := ldc_nb_pal_soldanslecamion_tot + ldc_nb_pal_soldanslecamion;
                       
                        ls_tab_orl_ref.extend(1);
					    ls_tab_orl_ref(li_row_tot) := ls_orl_ref;
					   
					    li_tab_trp_repartition_exp.extend(1); 
					    li_tab_trp_repartition_exp(li_row_tot) := ld_exp_nb_pal;
                    end if;

                    ll_nb_pal_soldanslecamion_exp := ll_nb_pal_soldanslecamion_exp + ld_exp_nb_pal;
                ELSIF ls_trp_bta_code = 'KILO' THEN
                    ld_trp_lig	:= round(ld_exp_pds_net * ld_trp_pu, 2); -- net et non pas brut (...)
                ELSIF ls_trp_bta_code = 'TONNE' THEN
                    ld_trp_lig := round(ld_exp_pds_net * ld_trp_pu / 1000, 2); -- net et non pas brut (...)
                ELSE
                    if ld_tot_exp_pds_net <> 0 THEN
                        ld_trp_lig := round(ld_trp_ord_tot * ld_exp_pds_net / ld_tot_exp_pds_net, 2);
                    end if;
                end if;
            end if;
            if ya_trs = 1 then
                if ls_trs_bta_code = 'COLIS' THEN
                    ld_trs_lig := round(ld_exp_nb_col * ld_trs_pu, 2);
                ELSIF ls_trs_bta_code = 'PAL' THEN
                    ld_trs_lig := round(ld_exp_nb_pal * ld_trs_pu, 2);
                ELSIF ls_trs_bta_code = 'KILO' THEN
                    ld_trs_lig := round(ld_exp_pds_net * ld_trs_pu, 2);
                ELSIF ls_trs_bta_code = 'TONNE' THEN
                    ld_trs_lig := round(ld_exp_pds_net * ld_trs_pu / 1000, 2);
                ELSIF ls_trs_bta_code = 'PCENT' THEN
                    ld_trs_lig := round(ld_vte_lig * ld_trs_pu / 100, 2);
                ELSE
                    if ld_tot_exp_pds_net > 0 then
                        ld_trs_lig := round(ld_trs_ord_tot * ld_exp_pds_net / ld_tot_exp_pds_net, 2);
                    end if;
                end if;
            end if;
            if ya_crt = 1 then
                if ls_crt_bta_code = 'COLIS' THEN
                    ld_crt_lig := round(ld_exp_nb_col * ld_crt_pu, 2);
                ELSIF ls_crt_bta_code = 'PAL' THEN
                    ld_crt_lig := round(ld_exp_nb_pal * ld_crt_pu, 2);
                ELSIF ls_crt_bta_code = 'KILO' THEN
                    ld_crt_lig := round(ld_exp_pds_net * ld_crt_pu, 2);
                ELSIF ls_crt_bta_code = 'TONNE' THEN
                    ld_crt_lig := round(ld_exp_pds_net * ld_crt_pu / 1000, 2);
                ELSIF ls_crt_bta_code = 'PCENT' THEN
                    ld_crt_lig := round(ld_vte_lig * ld_crt_pu / 100, 2);
                ELSE
                    if ld_tot_exp_pds_net > 0 then
                        ld_crt_lig := round(ld_crt_ord_tot * ld_exp_pds_net / ld_tot_exp_pds_net, 2);
                    end if;
                end if;
            end if;
            if ya_frd = 1 then
                if ls_frais_unite_ord = 'COLIS' THEN
                    ld_frd_lig := ld_frd_lig + round(ld_exp_nb_col * ld_frais_pu_ord, 2);
                ELSIF ls_frais_unite_ord = 'PAL' THEN
                    ld_frd_lig := ld_frd_lig + round(ld_exp_nb_pal * ld_frais_pu_ord, 2);
                ELSIF ls_frais_unite_ord = 'KILO' THEN
                    ld_frd_lig := ld_frd_lig + round(ld_exp_pds_net * ld_frais_pu_ord, 2);
                ELSIF ls_frais_unite_ord = 'TONNE' THEN
                    ld_frd_lig := ld_frd_lig + round(ld_exp_pds_net * ld_frais_pu_ord / 1000, 2);
                ELSIF ls_frais_unite_ord = 'PCENT' THEN
                    ld_frd_lig := ld_frd_lig + round(ld_vte_lig * ld_frais_pu_ord / 100, 2);
                ELSE
                    if ld_tot_exp_pds_net > 0 then
                        ld_frd_lig := ld_frd_lig + round(ld_frd_ord_tot * ld_exp_pds_net / ld_tot_exp_pds_net, 2);
                    end if;
                end if;
            end if;
            -- frais additionnels
            if ya_fad = 1 then
                if ld_vte_lig_tot <> 0 then
                    -- Répartition des frais en fonction de la vente
			        ld_fad_lig := round(ld_fad_ord_tot * ld_vte_lig / ld_vte_lig_tot, 2);
                else
                    -- Si le montant de la vente totale est nul (peu probable mais on sait jamais) les frais annexes ne sont pas répartis
                    -- sur les lignes mais uniquement sur la premère
                    if ll_nb_lignes = 1 then ld_fad_lig	:= ld_fad_ord_tot; end if;
                end if;
            end if;

            -- actualisation de la ligne
            update geo_ordlig
            set tottrp = ld_trp_lig, tottrs = ld_trs_lig, totcrt = ld_crt_lig, totfrd = ld_frd_lig, totfad = ld_fad_lig
            where orl_ref = ls_orl_ref;
        END LOOP;
        CLOSE CLIG2;

        -- REPARTION PAR LIGNE DE LA VOLUMETRIE DES PALETTES DANS LE CAMION
        If li_row_tot > 0 Then
            ll_nb_pal_soldanslecamion_tot := ceil(ldc_nb_pal_soldanslecamion_tot);
            If ll_nb_pal_soldanslecamion_exp >= ll_nb_pal_soldanslecamion_tot then
                ld_trp_ord_tot := round(ll_nb_pal_soldanslecamion_tot * ld_trp_pu, 2);
                If ldc_nb_pal_soldanslecamion_tot = 0 then ldc_nb_pal_soldanslecamion_tot := 1; end if;

                for li_row in 1..li_row_tot loop
                    If li_row <> li_row_tot Then
                        ld_trp_lig := round((ldc_tab_trp_repartition(li_row)/ldc_nb_pal_soldanslecamion_tot)*  ld_trp_ord_tot, 2);
                        ld_trp_lig_tot := ld_trp_lig_tot + ld_trp_lig;
                    Else
                        ld_trp_lig := ld_trp_ord_tot - ld_trp_lig_tot;
                    End If;

                    update GEO_ORDLIG
                    SET  tottrp = ld_trp_lig
                    where orl_ref = ls_tab_orl_ref(li_row);
                end loop;
            else
                for li_row in 1..li_row_tot loop
                    ld_trp_lig := round(li_tab_trp_repartition_exp(li_row) * ld_trp_pu, 2);

                    update GEO_ORDLIG
                    SET  tottrp = ld_trp_lig
                    where orl_ref = ls_tab_orl_ref(li_row);
                end loop;
                ld_trp_ord_tot := round(ll_nb_pal_soldanslecamion_exp*ld_trp_pu, 2);
            end if;

            update GEO_ORDRE
            SET  tottrp = ld_trp_ord_tot
            where ord_ref = arg_ord_ref;
            COMMIT;
        end if;

        If ldc_frais_plateforme > 0 Then
            open CLIG3 (arg_ord_ref);

            LOOP
                fetch CLIG3 into ld_exp_pds_net, ls_orl_ref;
                EXIT WHEN CLIG3%notfound;

                IF ld_exp_pds_net > 0 Then
                    ld_lig_frais_plateforme := round(ld_exp_pds_net * ldc_frais_plateforme, 2);
                    ld_ord_frais_plateforme_ordre := ld_ord_frais_plateforme_ordre + ld_lig_frais_plateforme;

                    update geo_ordlig
                    set totfad = totfad + ld_lig_frais_plateforme,
                        totfrais_plateforme  = ld_lig_frais_plateforme
                    where orl_ref = ls_orl_ref;
                end if;
            end loop;
            CLOSE CLIG3;
        end if;

        update geo_ordre
        set 	totfrais_plateforme = ld_ord_frais_plateforme_ordre,
               totfad = totfad + ld_ord_frais_plateforme_ordre
        where ord_ref = arg_ord_ref;
        COMMIT;
    end if;

    res := 1;
    msg := 'OK';

    EXCEPTION
        WHEN NO_DATA_FOUND THEN null;
end;
