CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CALCUL_PEREQUATION" (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code in GEO_ORDRE.SOC_CODE%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_rc varchar(50);
	ll_count number;
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
    ld_frd_ord number;
	ld_marge_brute_orig_ligne NUMBER := 0;
    ld_marge_brute_4pc_ligne number := 0;
    ld_marge_brute_delta_ligne number := 0;
    ld_marge_brute_delta_ordre number := 0;
    ld_frdnet_lig number;
    ld_frdnet_ord number;
    ld_totpereq_lig number;
    ld_totpereq_ord number;
    ls_facture_avoir varchar(50) := '';
    ls_sco_code varchar(50);
    ls_orl_ref varchar(50);
    ls_cat_code varchar(50);
    ls_ori_code varchar(50);
    ls_var_code varchar(50);
    ls_tvt_code varchar(50);
    ll_mode_culture number;
    ls_list_orlref varchar(50);
    ls_perequation varchar(50);
    ll_k_frais number;
    ls_typ_ordre varchar(50);
    ls_ccw_code varchar(50);

    cursor C_ART_ORD (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select L.orl_ref, A.mode_culture, A.cat_code, A.ori_code, A.var_code, O.tvt_code, O.sco_code, A.ccw_code
        from geo_article_colis A, geo_ordlig L, geo_ordre O
        where L.ord_ref = ref_ordre 	and
        O.ord_ref = L.ord_ref			and
        A.art_ref  = L.art_ref;
BEGIN
    res := 0;
    msg := '';

    -- on test d'abord si il y a des variétés impliquées (geo_variet.perequ = 'O')
    select facture_avoir, sco_code, typ_ordre
    into ls_facture_avoir, ls_sco_code, ls_typ_ordre
    from geo_ordre
    where ord_ref = arg_ord_ref;

    --pour les avoirs pas de calcul de péréquation ni pour le secteur INDUSTRIE
    IF ls_facture_avoir ='A' or ls_sco_code ='IND' or arg_soc_code ='BUK' OR ls_typ_ordre ='UKN' Then
        res := 1;
        return;
    End IF;

    -- on test d'abord si il y a des variétés impliquées (geo_variet.perequ = 'O')
    ll_count := 0;
    OPEN C_ART_ORD (arg_ord_ref);
    LOOP
        fetch C_ART_ORD INTO ls_orl_ref, ll_mode_culture, ls_cat_code, ls_ori_code, ls_var_code, ls_tvt_code, ls_sco_code, ls_ccw_code;
        EXIT WHEN C_ART_ORD%notfound;

        f_recup_frais(ls_var_code, ls_ccw_code, ls_sco_code, ls_tvt_code, ll_mode_culture, ls_ori_code, ll_k_frais, msg);

        begin
            select perequation into ls_perequation
            from geo_attrib_frais
            where k_frais = ll_k_frais;

            EXCEPTION WHEN NO_DATA_FOUND THEN -- Permet de passer a la suite
                ls_perequation := 'N';
        end;

        if ls_perequation = 'O' then
            select coalesce(L.totvte,0), coalesce(L.totrem,0), coalesce(L.totres,0), coalesce(L.totfrd,0), coalesce(L.totach,0), coalesce(L.totmob,0), coalesce(L.tottrp,0), coalesce(L.tottrs,0), coalesce(L.totcrt,0), coalesce(L.totfad,0)
            into ld_vte_lig, ld_rem_lig, ld_res_lig, ld_frdnet_lig, ld_ach_lig, ld_mob_lig,	ld_trp_lig, ld_trs_lig, ld_crt_lig, ld_fad_lig
            from geo_ordlig L
            where L.orl_ref  = ls_orl_ref;

            ld_marge_brute_orig_ligne := ld_vte_lig - ld_rem_lig + ld_res_lig - ld_frdnet_lig - ld_ach_lig - ld_trp_lig - ld_trs_lig - ld_crt_lig - ld_fad_lig;
            ld_marge_brute_4pc_ligne := round((ld_vte_lig) * 0.04, 2);
            ld_marge_brute_delta_ligne := ld_marge_brute_orig_ligne - ld_marge_brute_4pc_ligne;
            ld_marge_brute_delta_ordre := ld_marge_brute_delta_ordre + ld_marge_brute_delta_ligne;
            ld_totpereq_ord := ld_marge_brute_delta_ordre;
            ld_frd_lig := ld_frdnet_lig + ld_marge_brute_delta_ligne;
            ld_totpereq_lig := ld_marge_brute_delta_ligne;

            begin
                update geo_ordlig
                set totfrd = ld_frd_lig,
                    totfrd_net = ld_frdnet_lig,
                    totpereq = ld_totpereq_lig
                where orl_ref = ls_orl_ref;
            EXCEPTION WHEN OTHERS THEN
                msg := 'pb sur update ORDLIG ordre ' || arg_ord_ref || ' ' || SQLERRM;
            return;
            end;

        end if;
    END LOOP;
    CLOSE C_ART_ORD;
    -- fin marketing

    COMMIT;

    -- on va ajuster les frais marketting au niveau de l'ordre par le cumul ajustements lignes
    begin
        select totfrd into ld_frdnet_ord from geo_ordre where ord_ref = arg_ord_ref;
        ld_frd_ord := ld_frdnet_ord + ld_marge_brute_delta_ordre;

        EXCEPTION WHEN NO_DATA_FOUND THEN
            msg := 'no data totfrd in geo_ordre';
            return;
    end;

    update geo_ordre
    set totfrd = ld_frd_ord, totfrd_net = ld_frdnet_ord, totpereq = ld_totpereq_ord
    where ord_ref = arg_ord_ref;

    commit;

    res := 1;
END;
/

