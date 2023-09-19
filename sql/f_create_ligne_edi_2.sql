CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CREATE_LIGNE_EDI_2" (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_cli_ref IN GEO_CLIENT.CLI_REF%TYPE,
    arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
    arg_ref_edi_ligne IN geo_edi_ligne.ref_edi_ligne%TYPE,
    arg_bassin IN GEO_ENT_TRP_BASSIN.BAC_CODE%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_username IN varchar2,
    res out number,
    msg out varchar2
)
AS
    ls_arg_bassin GEO_ENT_TRP_BASSIN.BAC_CODE%TYPE;
    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;

    ls_sco_code GEO_ORDRE.SCO_CODE%TYPE;
    ls_ORD_REF GEO_ORDRE.ORD_REF%TYPE;

    ls_ean_cli geo_edi_ligne.ean_prod_client%TYPE;
    ll_qte_art_cde geo_edi_ligne.quantite_colis%TYPE;
    ld_prix_vente geo_edi_ligne.prix_vente%TYPE;

    ls_ART_REF GEO_ORDLIG.art_ref%TYPE;
    ll_ACH_PU GEO_ORDLIG.ach_pu%TYPE;
    ls_ACH_BTA_CODE GEO_ORDLIG.ach_bta_code%TYPE;
    --ll_VTE_PU GEO_ORDLIG.vte_pu%TYPE;
    ls_VTE_BTA_CODE GEO_ORDLIG.vte_bta_code%TYPE;
    ls_FOU_CODE GEO_ORDLIG.fou_code%TYPE;
    ls_PAL_CODE GEO_ORDLIG.pal_code%TYPE;
    --ls_PAN_CODE GEO_ORDLIG.pan_code%TYPE;
    ll_PAL_NB_COL GEO_ORDLIG.pal_nb_col%TYPE;
    --ls_ACH_DEV_TAUX GEO_ORDLIG.ACH_DEV_TAUX%TYPE;
    ls_ACH_DEV_PU GEO_ORDLIG.ACH_DEV_PU%TYPE;
    ls_ACH_DEV_CODE GEO_ORDLIG.ACH_DEV_CODE%TYPE;
    ls_BAC_CODE GEO_ORDLIG.BAC_CODE%TYPE;
    ls_ESP_CODE GEO_ORDLIG.ESP_CODE%TYPE;
    --ls_art_ref_kit GEO_ORDLIG.ART_REF_KIT%TYPE;
    ls_PROP_CODE GEO_ORDLIG.PROPR_CODE%TYPE;

    ls_ORL_REF GEO_ORDLIG.ORL_REF%TYPE;
    ls_orl_lig varchar(10);
    ll_edi_ord number;

    ld_ACH_DEV_PU GEO_STOCK_ART_EDI_BASSIN.ACH_DEV_PU%TYPE;
    ld_VTE_PU GEO_STOCK_ART_EDI_BASSIN.VTE_PU%TYPE;
    ld_VTE_PU_NET GEO_STOCK_ART_EDI_BASSIN.VTE_PU_NET%TYPE;

    ls_soc_code GEO_ORDRE.SOC_CODE%TYPE;
    ls_cam_code GEO_ORDRE.CAM_CODE%TYPE;
    ls_cli_ref GEO_ENTREP.CLI_REF%TYPE;

    ld_remsf_tx GEO_CLIENT.REM_SF_TX%TYPE;
    ls_dev_code GEO_CLIENT.DEV_CODE%TYPE;

    ld_pmb_per_com GEO_ARTICLE_COLIS.U_PAR_COLIS%TYPE;
    ld_pdnet_client GEO_ARTICLE_COLIS.pdnet_client%TYPE;
    ld_col_tare GEO_COLIS.COL_TARE%TYPE;

    ll_cde_nb_pal number;
    ld_pds_net number;
    ld_pds_brut number;
    ld_ACH_QTE number;
    ld_vte_qte number;
    ls_TOTVTE varchar2(100);
    ls_TOTACH varchar2(100);
    ls_TOTMOB varchar2(100);

    ls_tvt_code GEO_ORDRE.TVT_CODE%TYPE;

    ll_article_mode_culture geo_article_colis.MODE_CULTURE%TYPE;
    ls_cat_code geo_article_colis.CAT_CODE%TYPE;
    ls_ori_code geo_article_colis.ORI_CODE%TYPE;
    ls_var_code geo_article_colis.VAR_CODE%TYPE;
    --ls_ccw_code geo_article_colis.CCW_CODE%TYPE;

    ld_frais_pu_mark geo_attrib_frais.FRAIS_PU%TYPE;
    ls_frais_unite_mark geo_attrib_frais.FRAIS_UNITE%TYPE;
    ld_accompte geo_attrib_frais.ACCOMPTE%TYPE;
    ls_perequation geo_attrib_frais.PEREQUATION%TYPE;

    ll_k_frais number;
    ld_frais_pu number;
    ls_frais_unite varchar2(200);
    ls_indbloq_ach_dev_pu char(1);
    ll_ach_dev_taux number;

    ls_trp_code geo_ent_trp_bassin.trp_code%TYPE;
    ls_trp_bta_code geo_ent_trp_bassin.TRP_BTA_CODE%TYPE;
    ld_trp_pu geo_ent_trp_bassin.TRP_PU%TYPE;
    ls_trp_dev_code geo_ent_trp_bassin.TRP_DEV_CODE%TYPE;
    ld_dev_tx geo_devise_ref.DEV_TX%TYPE;

    ls_sto_ref GEO_STOCK.STO_REF%TYPE;
    ll_qte_ini GEO_STOCK.QTE_INI%TYPE;
    ll_qte_res GEO_STOCK.QTE_RES%TYPE;

    ll_stm_ref number;
    ls_nordre GEO_ORDRE.NORDRE%TYPE;
    ls_cli_code GEO_ORDRE.CLI_CODE%TYPE;
    ls_stm_ref GEO_STOMVT.stm_ref%TYPE;
    ls_desc GEO_STOMVT.STM_DESC%TYPE;

    ll_qte_restante number;
    ll_stock_nb_resa number;
BEGIN
    -- corresponds à f_create_ligne_edi.pbl
    res := 0;
    msg := '';

    select dev_code into ls_soc_dev_code from geo_societe where soc_code = arg_soc_code;

    ls_ORD_REF := arg_ord_ref;
    ls_arg_bassin := arg_bassin;

    select ean_prod_client, quantite_colis, prix_vente, ref_edi_ordre
    into ls_ean_cli, ll_qte_art_cde, ld_prix_vente, ll_edi_ord
    from geo_edi_ligne where ref_edi_ligne = arg_ref_edi_ligne;

    select S.fou_code, S.art_ref, S.bac_code, A.esp_code, S.prop_code,  S.ACH_BTA_CODE,  S.ACH_DEV_CODE,  S.ACH_DEV_PU,  S.ACH_PU,  S.VTE_BTA_CODE,  S.VTE_PU,  S.VTE_PU_NET
    into ls_fou_code, ls_art_ref, ls_bac_code, ls_esp_code, ls_prop_code, ls_ACH_BTA_CODE, ls_ACH_DEV_CODE, ld_ACH_DEV_PU, ll_ACH_PU, ls_VTE_BTA_CODE, ld_VTE_PU, ld_VTE_PU_NET
    from GEO_STOCK_ART_EDI_BASSIN S, GEO_ARTICLE_COLIS A
    where edi_lig = arg_ref_edi_ligne
      and edi_ord = ll_edi_ord
      and cli_ref = arg_cli_ref
      and gtin = ls_ean_cli
      and choix = 'O'
      and A.art_ref = S.art_ref;

    -- Recup du secteur de l'ordre
    select sco_code, soc_code, cam_code into ls_sco_code, ls_soc_code, ls_cam_code from GEO_ORDRE where GEO_ORDRE.ORD_REF = arg_ord_ref;

    -- On insert la ligne article générique art_ref = '000000'
    -- Recup de ORL suivante
    select F_SEQ_ORL_SEQ() into ls_ORL_REF FROM DUAL;
    -- recherche du prochain numero de ligne dans nouvel ordre
    select TRIM(to_char(count(orl_ref)+1,'00')) into ls_orl_lig from GEO_ORDLIG where GEO_ORDLIG.ORD_REF = arg_ord_ref;
    -- Recup du pal_code au niveau de l'entrepôt
    select PAL_CODE, CLI_REF into ls_pal_code, ls_cli_ref from GEO_ENTREP where cen_ref = arg_cen_ref;

    -- Tjs COLIS donc ll_qte_art_code
    -- Récupération du prix de vente de la dernière cde EDI si <> 0
    if ld_VTE_PU > 0 then
        ld_prix_vente := ld_vte_pu;
    end if;

    select rem_sf_tx, dev_code into ld_remsf_tx, ls_dev_code from geo_client where cli_ref = ls_cli_ref;
    if ld_remsf_tx is null then
        ld_remsf_tx := 0;
    end if;
    if ld_remsf_tx <> 0 then
        ld_vte_pu_net := round(ld_prix_vente - ld_prix_vente * ld_remsf_tx * 0.01, 4);
    else
        ld_vte_pu_net := round(ld_prix_vente, 4);
    end if;

    -- détermination du nbre de pal au sol
    begin
        select
            case P.dim_code
                when '1' then case when CS.COL_XB IS NOT NULL AND CS.COL_XH IS NOT NULL THEN CS.COL_XB * CS.COL_XH ELSE C.COL_XB * C.COL_XH END
                when '8' then case when CS.COL_YB IS NOT NULL AND CS.COL_YH IS NOT NULL THEN CS.COL_YB * CS.COL_YH ELSE C.COL_YB * C.COL_YH END
                when '6' then case when CS.COL_ZB IS NOT NULL AND CS.COL_ZH IS NOT NULL THEN CS.COL_ZB * CS.COL_ZH ELSE C.COL_ZB * C.COL_ZH END
                END, AC.U_PAR_COLIS, AC.pdnet_client, C.col_tare  into ll_pal_nb_col, ld_pmb_per_com, ld_pdnet_client, ld_col_tare
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
    exception when no_data_found then
        msg := msg || 'Impossible de déterminer le nombre de palettes au sol ' || SQLERRM;
        return;
    end;

    if ll_pal_nb_col is not null and ll_pal_nb_col > 0 then
        ll_cde_nb_pal := ROUND( ll_qte_art_cde / ll_pal_nb_col, 1);

        if ll_cde_nb_pal = 0 then
            ll_cde_nb_pal := 1;
        end if;
    end if;

    ld_pds_net := round(ld_pdnet_client * ll_qte_art_cde, 0);
    ld_pds_brut := round(ld_pds_net + (ld_col_tare * ll_qte_art_cde), 0);

    case
        when ls_ach_bta_code = 'COLIS' or ls_ach_bta_code = 'BARQUE' or ls_ach_bta_code = 'SACHET' or ls_ach_bta_code = 'UNITE' then
		    ld_ACH_QTE := ll_qte_art_cde;
        when ls_ach_bta_code = 'KILO' then
            ld_ACH_QTE := ld_pds_net;
        when ls_ach_bta_code = 'PAL' then
            ld_ACH_QTE := ll_cde_nb_pal;
        when ls_ach_bta_code = 'TONNE' then
            ld_ACH_QTE := round(ld_pds_net / 1000, 0);
        when ls_ach_bta_code = 'CAMION' then
            ld_ACH_QTE := 0;
        else
            ld_ACH_QTE := round(ll_qte_art_cde *ld_pmb_per_com, 0);
    end case;

    case
        when ls_vte_bta_code = 'COLIS' or ls_vte_bta_code = 'BARQUE' or ls_vte_bta_code = 'SACHET' or ls_vte_bta_code = 'UNITE' then
		    ld_vte_qte := ll_qte_art_cde;
        when ls_vte_bta_code = 'KILO' then
            ld_vte_qte := ld_pds_net;
        when ls_vte_bta_code = 'PAL' then
            ld_vte_qte := ll_cde_nb_pal;
        when ls_vte_bta_code = 'TONNE' then
            ld_vte_qte := round(ld_pds_net / 1000, 0);
        when ls_vte_bta_code = 'CAMION' then
            ld_vte_qte := 0;
        else
            ld_vte_qte := round(ll_qte_art_cde *ld_pmb_per_com, 0);
    end case;

    ls_TOTVTE := to_char(ld_prix_vente * ld_vte_qte);
    ls_TOTACH := to_char(ll_ACH_PU * ld_ACH_QTE);
    ls_TOTMOB := '0';

    -- New gestion des frais marketing
    if ls_soc_code <> 'BUK' then
        select O.tvt_code, O.sco_code into ls_tvt_code, ls_sco_code from geo_ordre O where O.ord_ref = arg_ord_ref;
        select A.mode_culture, A.cat_code, A.ori_code, A.var_code
        into ll_article_mode_culture, ls_cat_code, ls_ori_code, ls_var_code
        from geo_article_colis A
        where A.art_ref  = ls_art_ref;

        f_recup_frais(ls_var_code, ls_cat_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code, ll_k_frais, msg);

        begin
            select frais_pu, frais_unite, accompte, perequation
            into ld_frais_pu_mark, ls_frais_unite_mark, ld_accompte, ls_perequation
            from geo_attrib_frais
            where k_frais = ll_k_frais;

            if ls_sco_code = 'RET' then
                raise VALUE_ERROR; -- raise exception for force set value in exception block
            end if;

            ld_frais_pu := ld_frais_pu_mark;
            ls_frais_unite := ls_frais_unite_mark;

            if ls_perequation ='O' then
                If ld_accompte is not null and ld_accompte > 0 then
                    ll_ach_pu 		:= ld_accompte;
                    ls_ach_dev_pu	:= to_char(ld_accompte);
                    ls_ach_dev_code	:= 'EUR';
                    ll_ach_dev_taux	:= 1;
                    ls_indbloq_ach_dev_pu := 'O';
                    ls_ach_bta_code	:= 'KILO';
                end if;
            end if;
        exception when others then
            ld_frais_pu := 0;
            ls_frais_unite := '';
            ls_indbloq_ach_dev_pu := 'N';
        end;
    else
        if ls_dev_code = ls_soc_dev_code then
            ll_ach_dev_taux := 1;
            ls_ach_dev_code := ls_soc_dev_code;
        else
            If ls_soc_dev_code is not null and ls_soc_dev_code <> '' Then
                ls_ach_dev_code := ls_soc_dev_code;
            else
                ls_ach_dev_code := 'EUR';
            End If;

            begin
                select dev_tx into ll_ach_dev_taux
                from geo_devise_ref
                where dev_code = ls_dev_code and
                        dev_code_ref= ls_ach_dev_code;

                ls_indbloq_ach_dev_pu := 'N';
            exception when others then
                msg := '%%%erreur lecture devise';
                return;
            end;
        end if;
    end if;
    -- fin marketing

    begin
        INSERT INTO GEO_ORDLIG (
            ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL, EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU,
            VTE_BTA_CODE, VTE_QTE, FOU_CODE, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU,
            FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX, REMHF_TX, ART_REF
            , ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, ART_REF_KIT, GTIN_COLIS_KIT, REF_EDI_LIGNE, FRAIS_UNITE, PROPR_CODE, INDBLOQ_ACH_DEV_PU, LIB_DLV, vte_pu_net
        ) VALUES (
         ls_ORL_REF, arg_ord_ref, ls_orl_lig , ls_PAL_CODE, ll_pal_nb_col ,ll_cde_nb_pal, ll_qte_art_cde, 0, 0,0, 0, ll_ach_pu, ls_ach_dev_code, ls_ach_bta_code, ld_ACH_QTE, ld_prix_vente,
         ls_vte_bta_code, ld_vte_qte, ls_fou_code, ld_pds_brut, ld_pds_net, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0, 'N', 'N', 'N', 'N', 'N', 'O', ld_frais_pu, 'N', 'N',ls_BAC_CODE, 0, 0, ls_art_ref,
         ls_ESP_CODE, 0, ll_ach_dev_taux, ls_ach_dev_pu, ls_art_ref, ls_ean_cli, arg_ref_edi_ligne, ls_frais_unite, ls_PROP_CODE, ls_indbloq_ach_dev_pu,'', ld_vte_pu_net
        );

        -- Mise à jour du référentiel EDI table GEO_STOCK_ART_EDI
        update GEO_STOCK_ART_EDI_BASSIN
        set ach_pu = ll_ach_pu, ach_dev_pu = ls_ach_dev_pu, ach_bta_code = ls_ach_bta_code, ach_dev_code = ls_ach_dev_code, ach_dev_taux = ll_ach_dev_taux,
            vte_pu_net = ld_vte_pu_net, vte_pu = ld_prix_vente
        where edi_lig = arg_ref_edi_ligne
          and gtin = ls_ean_cli
          and art_ref = ls_art_ref
          and cli_ref = ls_cli_ref
          and cam_code = ls_cam_code;

        -- DEB TRANSPORT PAR DEFAUT
        -- Vérification que le bassin de la station est en phase avec le bassin du transport par défaut souhaité par l'entrepôt
        -- Table GEO_ENT_TRP_BASSIN
        if ls_arg_bassin = '' or ls_arg_bassin is null  then
            begin
                select trp_code, trp_bta_code, trp_pu, trp_dev_code, dev_tx into ls_trp_code, ls_trp_bta_code, ld_trp_pu, ls_trp_dev_code, ld_dev_tx
                from geo_ent_trp_bassin , geo_devise_ref
                where cen_ref = arg_cen_ref and
                        bac_code = ls_bac_code and
                        dev_code = trp_dev_code and
                        dev_code_ref = ls_ach_dev_code;

                update geo_ordre set trp_code = ls_trp_code, trp_dev_pu = ld_trp_pu, trp_dev_code = ls_trp_dev_code, trp_bta_code = ls_trp_bta_code, dev_tx = ld_dev_tx
                where ord_ref = arg_ord_ref;

                ls_arg_bassin := ls_bac_code;
            exception when others then null;
            end;
        end if;

        -- FIN TRANSPORT PAR DEFAUT
        -- résa stock
        if ls_FOU_CODE is not null and ls_fou_code <> '' then
            begin
                select STO_REF, QTE_INI, QTE_RES into ls_sto_ref, ll_qte_ini, ll_qte_res from GEO_STOCK where FOU_CODE = ls_FOU_CODE AND ART_REF = ls_ART_REF AND VALIDE='O';

                select seq_stm_num.nextval into ll_stm_ref from dual;
                select nordre, cli_code into ls_nordre, ls_cli_code from GEO_ORDRE WHERE ORD_REF = arg_ord_ref;

                ls_stm_ref	:= to_char(ll_stm_ref,'000000');
                ls_desc :=  'ordre ' || ls_nordre || '/' || ls_cli_code;
                -- voir trigger GEO_STOMVT_BEF_INS qui actualise aussi geo_stock ainsi que les champs manquant de stomvt
                begin
                    insert into geo_stomvt (stm_ref, sto_ref, nom_utilisateur, mvt_type, mvt_qte, ord_ref, art_ref, orl_ref, stm_desc)
                    values(ls_stm_ref, ls_sto_ref, arg_username, 'R', ll_qte_art_cde, arg_ord_ref, ls_ART_REF, ls_ORL_REF, ls_desc);

                    ll_qte_restante := ll_qte_ini - ll_qte_res - ll_qte_art_cde;
                    if ll_qte_restante > 0 then
                        ll_stock_nb_resa := 1;
                    else
                        ll_stock_nb_resa := -1;
                    end if;

                    update GEO_ORDLIG SET stock_nb_resa = ll_stock_nb_resa where orl_ref = ls_ORL_REF and ord_ref = arg_ord_ref;
                exception when others then
                    msg := 'erreur sur création de réservation orl_ref=' || ls_ORL_REF || ' ordre ' || arg_ord_ref || '/' || arg_cli_ref || ':' || SQLERRM;
                end;

            exception when others then null;
            end;
        end if;
    exception when others then
        msg := '%%% Erreur à la création de la ligne d~''ordre: ' || SQLERRM;
        return;
    end;

    res := 1;
end;
-- Creation des lignes articles en automatique pour les commandes EDI
-- avec réservation de stock
/

