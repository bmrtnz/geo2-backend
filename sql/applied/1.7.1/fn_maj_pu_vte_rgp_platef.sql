CREATE OR REPLACE PROCEDURE GEO_ADMIN.FN_MAJ_PU_VTE_RGP_PLATEF(
    arg_ord_ref_grp varchar2,
    res OUT number,
    msg OUT varchar2
)
AS
    ld_mont_pu_tot_gbp number;
    ld_mont_pu_gbp number;
    ld_vte_pu_rgp_gbp number;
    ld_vte_pu_rgp number;
    ld_pds_net number;
    ld_pds_brut number;
    ld_ach_qte number;
    ld_vte_qte number;
    ll_cnt number;
    ldc_vte_dev_taux_GBP number;
    ldc_ach_dev_taux_GBP number;
    ld_mont_ach_pu_gbp number;
    ld_mont_ach_pu_tot_gbp number;
    ld_ach_pu_rgp_gbp number;
    ld_ach_pu_rgp number;

    ls_col_code varchar2(50);
    ll_article_mode_culture number;
    ls_ori_code varchar2(50);
    ld_prix_mini number;

    ls_art_ref_sav varchar2(50);


     ls_tvt_code varchar2(50);
     ls_sco_code varchar2(50);
     ls_var_code varchar2(50);
     ls_ccw_code varchar2(50);

     ll_k_frais  number;

     ld_accompte   number;


    cursor C_rgp_pu_vente is
    select R.GRP_RGP,L.CDE_NB_COL,O.dev_code, L.ORL_REF, R.PAL_NB_COL_ORIG*CDE_NB_PAL_ORIG as nb_col
    from GEO_GEST_REGROUP R,GEO_ORDLIG L, GEO_ORDRE O
    where R.ORD_REF_RGP =arg_ord_ref_grp  	and
            (R.FOU_CODE_ORIG in ('STEFLEMANS') OR O.CEN_CODE ='GROSSISTE') 	and
            R.ORD_REF_RGP = O.ORD_REF 			and
            R.ORD_REF_RGP = L.ORD_REF 			and
            R.ORL_REF_RGP = L.ORL_REF  			and
            (L.CDE_NB_COL	 IS NOT NULL OR L.CDE_NB_COL > 0 )
    order by R.GRP_RGP;

    cursor C_orig_pu_vente(rgp GEO_GEST_REGROUP.GRP_RGP%TYPE) is
    select    L.orl_ref, L.art_ref, L.cde_nb_pal, L.cde_nb_col, L.vte_bta_code, X.u_par_colis, X.pdnet_client, C.col_tare,L.VTE_PU*O.DEV_TX as pu_gbp,L.ach_bta_code,L.ACH_PU
    from     		GEO_GEST_REGROUP R, geo_ordlig L, geo_article X, geo_colis C,geo_ORDRE O
    where	    R.ORD_REF_RGP =arg_ord_ref_grp and
                    R.GRP_RGP = rgp and
                    R.ORD_REF_ORIG = O.ORD_REF and
                    R.ORD_REF_ORIG = L.ORD_REF and
                    R.ORL_REF_ORIG = L.ORL_REF and
                    X.art_ref = L.art_ref and
                    C.esp_code = X.esp_code and
                    C.col_code = X.col_code and
                    O.SOC_CODE='BUK';
BEGIN
    res := 0;
    msg := '';

    for v in C_rgp_pu_vente loop

        ld_mont_pu_tot_gbp := 0;
        ld_mont_ach_pu_tot_gbp := 0;

        -- v.GRP_RGP
        for h in C_orig_pu_vente(v.GRP_RGP) loop
            ld_pds_net			:= round(h.pdnet_client * h.cde_nb_col, 2);					-- poids net calculé
            ld_pds_brut			:= round(ld_pds_net + (h.col_tare * h.cde_nb_col), 2);	-- poids brut calculé
            IF h.u_par_colis =  0 or h.u_par_colis is null then
                h.u_par_colis := 1;
            end if;

            -- calcul nombre unité de vente
            case h.vte_bta_code
            when 'COLIS' then
                ld_vte_qte	:= h.cde_nb_col;
            when 'KILO' then
                ld_vte_qte	:= ld_pds_net;
            when 'PAL' then
                ld_vte_qte	:= h.cde_nb_pal;
            when 'TONNE' then
                ld_vte_qte	:= round(ld_pds_net / 1000, 0);
            when 'CAMION' then
                ld_vte_qte	:= 0;
            else
                ld_vte_qte	:= round(h.cde_nb_col * h.u_par_colis, 0);
            end case;

            If ld_vte_qte is null Then
                ld_vte_qte := 0;
            end if;
            If h.pu_gbp is null Then
                h.pu_gbp := 0;
            end if;

            ld_mont_pu_gbp := ld_vte_qte * h.pu_gbp;
            ld_mont_pu_tot_gbp := ld_mont_pu_tot_gbp + ld_mont_pu_gbp;


            -- calcul nombre unité de vente
            case h.ach_bta_code
            when 'COLIS' then
                ld_ach_qte	:= h.cde_nb_col;
            when 'KILO' then
                ld_ach_qte	:= ld_pds_net;
            when 'PAL' then
                ld_ach_qte	:= h.cde_nb_pal;
            when 'TONNE' then
                ld_ach_qte	:= round(ld_pds_net / 1000, 0);
            when 'CAMION' then
                ld_ach_qte	:= 0;
            else
                ld_ach_qte	:= round(h.cde_nb_col * h.u_par_colis, 0);
            end case;

            If ld_ach_qte is null Then
                ld_ach_qte := 0;
            end if;
            If h.ach_pu is null Then
                h.ach_pu := 0;
            end if;

            ld_mont_ach_pu_gbp := ld_ach_qte * h.ach_pu;
            ld_mont_ach_pu_tot_gbp := ld_mont_ach_pu_tot_gbp + ld_mont_ach_pu_gbp;

            ls_art_ref_sav := h.art_ref;

        end loop;

        IF v.CDE_NB_COL > 0  Then
            ld_vte_pu_rgp_gbp := ld_mont_pu_tot_gbp / v.CDE_NB_COL;
        Else
            IF v.nb_col > 0  Then
                        ld_vte_pu_rgp_gbp := ld_mont_pu_tot_gbp / v.nb_col;
            Else
                ld_vte_pu_rgp_gbp := null;
            ENd IF;
        End IF;


        /*	If v.dev_code = 'GBP' Then
                ld_vte_pu_rgp = ld_vte_pu_rgp_gbp
            ELse
                select dev_tx into :ldc_vte_dev_taux_GBP
                from geo_devise_ref
                where dev_code = 'GBP' and
                dev_code_ref ='EUR';

                ld_vte_pu_rgp = ld_vte_pu_rgp_gbp * ldc_vte_dev_taux_GBP
            End If

            ld_ach_pu_rgp_gbp = ld_mont_ach_pu_tot_gbp / v.CDE_NB_COL



            select dev_tx into :ldc_ach_dev_taux_GBP
            from geo_devise_ref
            where dev_code = 'GBP' and
                dev_code_ref ='EUR';

            ld_ach_pu_rgp = ld_ach_pu_rgp_gbp * ldc_ach_dev_taux_GBP
    */
            IF v.CDE_NB_COL > 0  Then
                ld_ach_pu_rgp_gbp := ld_mont_ach_pu_tot_gbp / v.CDE_NB_COL;
            Else
                IF v.nb_col > 0  Then
                    ld_ach_pu_rgp_gbp := ld_mont_ach_pu_tot_gbp / v.nb_col;
                Else
                    ld_ach_pu_rgp_gbp := null;
                ENd IF;
            End IF;

            If v.dev_code = 'GBP' Then
                ld_vte_pu_rgp := ld_ach_pu_rgp_gbp;
            ELse
                select dev_tx into ldc_vte_dev_taux_GBP
                from geo_devise_ref
                where dev_code = 'GBP' and
                dev_code_ref ='EUR';

                ld_vte_pu_rgp := ld_ach_pu_rgp_gbp * ldc_vte_dev_taux_GBP;
            End If;
/*
        select distinct A.col_code, A.mode_culture,O.ori_code , V.ach_pu_mini
        into ls_col_code, ll_article_mode_culture,ls_ori_code,ld_prix_mini
        from geo_article_colis A,  geo_origine O,geo_variet V
        where A.art_ref = ls_art_ref_sav and
                A.esp_code = O.esp_code and
                A.ori_code = O.ori_code and
                A.esp_code = V.var_code and
                A.var_code = V.var_code;*/

        select O.tvt_code, O.sco_code into ls_tvt_code,ls_sco_code from geo_ordre O where O.ord_ref = arg_ord_ref_grp;
    begin
        select A.col_code,A.mode_culture, A.ori_code, A.var_code, A.ccw_code
        into ls_col_code,ll_article_mode_culture, ls_ori_code, ls_var_code, ls_ccw_code
        from geo_article_colis A
        where A.art_ref  = ls_art_ref_sav;

        f_recup_frais(ls_var_code, ls_ccw_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code, ll_k_frais, msg);

       ld_ach_pu_rgp:=0;

        select accompte
        into  ld_accompte
        from geo_attrib_frais
        where k_frais = ll_k_frais;


        If ld_accompte > 0  and substr(ls_col_code,1,2) <>'CP'   then
          ld_ach_pu_rgp := ld_accompte;
         End If ;
            exception when no_data_found then
                    null; -- pass
                end;


        update GEO_ORDLIG
        set VTE_PU = ld_vte_pu_rgp,
                VTE_BTA_CODE ='COLIS',
                ACH_PU = ld_ach_pu_rgp,
                ACH_BTA_CODE ='KILO',
                ACH_DEV_CODE ='EUR',
                ACH_DEV_PU = ld_ach_pu_rgp,
                ACH_DEV_TAUX = 1
        where ORD_REF =arg_ord_ref_grp and
                    ORL_REF =v.ORL_REF;



    end loop;

    msg := 'OK';
    res := 1;

end;
/
