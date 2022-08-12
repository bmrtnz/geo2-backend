CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CREATE_LIGNE_EDI" (
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

    ls_histo_ord GEO_EDI_ART_CLI.LAST_ORD%TYPE;

    ls_sql clob;
    ls_ART_REF GEO_ORDLIG.art_ref%TYPE;
    ll_ACH_PU GEO_ORDLIG.ach_pu%TYPE;
    ls_ACH_BTA_CODE GEO_ORDLIG.ach_bta_code%TYPE;
    ll_VTE_PU GEO_ORDLIG.vte_pu%TYPE;
    ls_VTE_BTA_CODE GEO_ORDLIG.vte_bta_code%TYPE;
    ls_FOU_CODE GEO_ORDLIG.fou_code%TYPE;
    ls_PAL_CODE GEO_ORDLIG.pal_code%TYPE;
    ls_PAN_CODE GEO_ORDLIG.pan_code%TYPE;
    ll_PAL_NB_COL GEO_ORDLIG.pal_nb_col%TYPE;
    ls_ACH_DEV_TAUX GEO_ORDLIG.ACH_DEV_TAUX%TYPE;
    ls_ACH_DEV_PU GEO_ORDLIG.ACH_DEV_PU%TYPE;
    ls_ACH_DEV_CODE GEO_ORDLIG.ACH_DEV_CODE%TYPE;
    ls_BAC_CODE GEO_ORDLIG.BAC_CODE%TYPE;
    ls_ESP_CODE GEO_ORDLIG.ESP_CODE%TYPE;
    ls_art_ref_kit GEO_ORDLIG.ART_REF_KIT%TYPE;
    ls_PROP_CODE GEO_ORDLIG.PROPR_CODE%TYPE;

    ls_ORL_REF GEO_ORDLIG.ORL_REF%TYPE;
    ls_orl_lig varchar(10);

    ld_pmb_per_com GEO_ARTICLE_COLIS.U_PAR_COLIS%TYPE;
    ld_pdnet_client GEO_ARTICLE_COLIS.pdnet_client%TYPE;
    ld_col_tare GEO_COLIS.COL_TARE%TYPE;

    ll_cde_nb_pal number;
    ld_pds_net number;
    ld_pds_brut number;
    ld_ACH_QTE number;
    ls_TOTVTE varchar2(100);
    ls_TOTACH varchar2(100);
    ls_TOTMOB varchar2(100);

    ls_tvt_code GEO_ORDRE.TVT_CODE%TYPE;

    ll_article_mode_culture geo_article_colis.MODE_CULTURE%TYPE;
    ls_cat_code geo_article_colis.CAT_CODE%TYPE;
    ls_ori_code geo_article_colis.ORI_CODE%TYPE;
    ls_var_code geo_article_colis.VAR_CODE%TYPE;
    ls_ccw_code geo_article_colis.CCW_CODE%TYPE;

    ld_frais_pu_mark geo_attrib_frais.FRAIS_PU%TYPE;
    ls_frais_unite_mark geo_attrib_frais.FRAIS_UNITE%TYPE;
    ld_accompte geo_attrib_frais.ACCOMPTE%TYPE;
    ls_perequation geo_attrib_frais.PEREQUATION%TYPE;

    ll_k_frais number;
    ld_frais_pu number;
    ls_frais_unite varchar2(200);
    ls_indbloq_ach_dev_pu char(1);
    lb_affect boolean;

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

    C_HISTO SYS_REFCURSOR;
BEGIN
    -- corresponds à f_create_ligne_edi.pbl
    res := 0;
    msg := '';

    select sco_code into ls_sco_code from GEO_ORDRE where GEO_ORDRE.ORD_REF = arg_ord_ref;
    select dev_code into ls_soc_dev_code from geo_societe where soc_code = arg_soc_code;

    ls_ORD_REF := arg_ord_ref;
    ls_arg_bassin := arg_bassin;

    select ean_prod_client, quantite_colis, prix_vente
    into ls_ean_cli, ll_qte_art_cde, ld_prix_vente
    from geo_edi_ligne where ref_edi_ligne = arg_ref_edi_ligne;

    SELECT LAST_ORD into ls_histo_ord FROM GEO_EDI_ART_CLI
    WHERE  CLI_REF = arg_cli_ref AND GTIN_COLIS_CLIENT = ls_ean_cli AND ROWNUM = 1;

    if ls_histo_ord is not null AND ls_histo_ord <> '' then
        -- Recupération des informations de la dernière commande trouvée comportant le GTIN pour ce client en prenant en compte l'ouverture de calibre potentiel
        ls_sql := 'SELECT  GEO_ORDLIG.art_ref, GEO_ORDLIG.ach_pu, GEO_ORDLIG.ach_bta_code, GEO_ORDLIG.vte_pu, GEO_ORDLIG.vte_bta_code, GEO_ORDLIG.fou_code, GEO_ORDLIG.pal_code, GEO_ORDLIG.pan_code, GEO_ORDLIG.pal_nb_col,   ';
        ls_sql := ls_sql || ' GEO_ORDLIG.ACH_DEV_TAUX, GEO_ORDLIG.ACH_DEV_PU, GEO_ORDLIG.ACH_DEV_CODE, GEO_ORDLIG.BAC_CODE, GEO_ORDLIG.ESP_CODE, GEO_ORDLIG.ART_REF_KIT, GEO_ORDLIG.PROPR_CODE';
        ls_sql := ls_sql || ' FROM GEO_ORDLIG, GEO_ORDRE, GEO_ARTICLE  ';
        ls_sql := ls_sql || ' WHERE (GEO_ORDRE.ORD_REF = ''' || ls_histo_ord || ''') AND ';
        ls_sql := ls_sql || '			 ( GEO_ORDRE.ORD_REF = GEO_ORDLIG.ORD_REF ) AND ';
        ls_sql := ls_sql || '		 	 ( GEO_ORDLIG.GTIN_COLIS_KIT = ''' || ls_ean_cli || ''') AND ';
        ls_sql := ls_sql || '		 	 ( GEO_ARTICLE.ART_REF = GEO_ORDLIG.ART_REF )  AND ';
        ls_sql := ls_sql || '			 ( GEO_ARTICLE.VALIDE=''O'' ) AND ';
        ls_sql := ls_sql || '			 ( GEO_ORDLIG.CDE_NB_COL > 0 ) ';

        OPEN C_HISTO FOR to_char(ls_sql);
        LOOP
            fetch C_HISTO into ls_ART_REF, ll_ACH_PU, ls_ACH_BTA_CODE, ll_VTE_PU, ls_VTE_BTA_CODE, ls_FOU_CODE, ls_PAL_CODE, ls_PAN_CODE, ll_PAL_NB_COL, ls_ACH_DEV_TAUX, ls_ACH_DEV_PU, ls_ACH_DEV_CODE, ls_BAC_CODE, ls_ESP_CODE, ls_art_ref_kit, ls_PROP_CODE;
            EXIT WHEN C_HISTO%NOTFOUND;

            -- Recup de ORL suivante
            select F_SEQ_ORL_SEQ() into ls_ORL_REF FROM DUAL;

            -- recherche du prochain numero de ligne dans nouvel ordre
            select TRIM(to_char(count(orl_ref)+1,'00')) into ls_orl_lig from GEO_ORDLIG where GEO_ORDLIG.ORD_REF = arg_ord_ref;

            -- détermination du nbre de pal au sol
            select
                case P.dim_code
                    when '1' then case when CS.COL_XB IS NOT NULL AND CS.COL_XH IS NOT NULL THEN CS.COL_XB * CS.COL_XH ELSE C.COL_XB * C.COL_XH END
                    when '8' then case when CS.COL_YB IS NOT NULL AND CS.COL_YH IS NOT NULL THEN CS.COL_YB * CS.COL_YH ELSE C.COL_YB * C.COL_YH END
                    when '6' then case when CS.COL_ZB IS NOT NULL AND CS.COL_ZH IS NOT NULL THEN CS.COL_ZB * CS.COL_ZH ELSE C.COL_ZB * C.COL_ZH END
                END, AC.U_PAR_COLIS, AC.pdnet_client, C.col_tare
            into ll_pal_nb_col, ld_pmb_per_com, ld_pdnet_client, ld_col_tare
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

            if ll_pal_nb_col is not null and ll_pal_nb_col > 0 then
                ll_cde_nb_pal := ROUND( ll_qte_art_cde / ll_pal_nb_col, 1);
                if ll_cde_nb_pal = 0 then
                    ll_cde_nb_pal := 1;
                end if;
            end if;

            -- Déterminataion du ACH_QTE
            ld_pds_net			:= round(ld_pdnet_client * ll_qte_art_cde, 0);
            ld_pds_brut			:= round(ld_pds_net + (ld_col_tare * ll_qte_art_cde), 0);

            case ls_ach_bta_code
                when 'COLIS' then
                    ld_ACH_QTE 	:= ll_qte_art_cde;
                when 'KILO' then
                    ld_ACH_QTE 	:= ld_pds_net;
                when 'PAL' then
                    ld_ACH_QTE 	:= ll_cde_nb_pal;
                when 'TONNE' then
                    ld_ACH_QTE 	:= round(ld_pds_net / 1000, 0);
                when 'CAMION' then
                    ld_ACH_QTE 	:= 0;
                else
                    ld_ACH_QTE := round(ll_qte_art_cde *ld_pmb_per_com, 0);
            end case;

            -- Si le prix reçu dans la commande EDI est à zéro alors on récupére  le prix de l'historique
            if ld_prix_vente = 0 then
                ld_prix_vente := ll_VTE_PU;
            end if;

            ls_TOTVTE := to_char(ld_prix_vente * ll_qte_art_cde);
            ls_TOTACH := to_char(ll_ACH_PU * ld_ACH_QTE);
			ls_TOTMOB := '0';

            -- Récupération ds frais_pu et frais_unite de la variété
            -- SELECT FRAIS_PU, FRAIS_UNITE into :ld_frais_pu, :ls_frais_unite FROM GEO_ARTICLE A,GEO_VARIET V WHERE A.ART_REF = :ls_ART_REF AND A.ESP_CODE = V.ESP_CODE AND A.VAR_CODE=V.VAR_CODE;

            -- New gestion des frais marketing
            select O.tvt_code, O.sco_code into ls_tvt_code, ls_sco_code from geo_ordre O where O.ord_ref = arg_ord_ref;
            select A.mode_culture, A.cat_code, A.ori_code, A.var_code, A.ccw_code
            into ll_article_mode_culture, ls_cat_code, ls_ori_code, ls_var_code, ls_ccw_code
            from geo_article_colis A
            where A.art_ref  = ls_ART_REF;

            f_recup_frais(ls_var_code, ls_ccw_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code, ll_k_frais, msg);

            begin
                select frais_pu, frais_unite, accompte, perequation
                into ld_frais_pu_mark, ls_frais_unite_mark, ld_accompte, ls_perequation
                from geo_attrib_frais
                where k_frais = ll_k_frais;

                ld_frais_pu := ld_frais_pu_mark;
                ls_frais_unite := ls_frais_unite_mark;
                if ls_perequation = 'O' then
                    If ld_accompte is not null and ld_accompte > 0 then
                        ll_ach_pu 			:= ld_accompte;
                        ls_ach_dev_pu		:= to_char(ld_accompte);
                        ls_ach_dev_code	    := 'EUR';
                        ls_ach_dev_taux	    := '1';
                        ls_indbloq_ach_dev_pu := 'O';
                        ls_ach_bta_code	    := 'KILO';
                    end if;
                end if;
            exception when others then
                ld_frais_pu := 0;
                ls_frais_unite := '';
                ls_indbloq_ach_dev_pu := 'N';
            end;

            -- fin marketing

            -- Transport par défaut
            if ls_arg_bassin is not null and ls_arg_bassin <> '' then
                if ls_bac_code <> ls_arg_bassin then
                    if (ls_bac_code = 'UDC' and ls_arg_bassin = 'SW') or (ls_bac_code = 'SW' and ls_arg_bassin = 'UDC') then
                        lb_affect := TRUE;
                    else
                        lb_affect := FALSE;
                    end if;
                end if;
            else
                lb_affect := TRUE;
            end if;
            if lb_affect = FALSE then
                -- messagebox('Erreur Transporteur/Bassin','Le bassin de la station ' + ls_fou_code + ' est différent de celui du transporteur par défaut')
                ls_fou_code := '';
                ls_prop_code := '';
            end if;
            -- fin Transport par défaut

            -- insertion de la ligne article trouvée dans l'historique du client
            begin
                INSERT INTO GEO_ORDLIG (
                    ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL, EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX, REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, ART_REF_KIT, GTIN_COLIS_KIT, FRAIS_PU, FRAIS_UNITE, PROPR_CODE, REF_EDI_LIGNE, INDBLOQ_ACH_DEV_PU
                ) VALUES (
                     ls_ORL_REF, ls_ORD_REF, ls_orl_lig , ls_PAL_CODE, ll_PAL_NB_COL , ll_cde_nb_pal, ll_qte_art_cde, 0, 0,0, 0, ll_ACH_PU, ls_ACH_DEV_CODE, ls_ACH_BTA_CODE, ld_ACH_QTE, ld_prix_vente, ls_VTE_BTA_CODE, ll_qte_art_cde, ls_FOU_CODE, ld_pds_brut, ld_pds_net, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0, 'N', 'N', 'N', 'N', 'N', 'O', 'N', 'N', ls_BAC_CODE, 0, 0, ls_ART_REF, ls_ESP_CODE, 0, ls_ACH_DEV_TAUX, ls_ACH_DEV_PU, ls_art_ref_kit, ls_ean_cli, ld_frais_pu, ls_frais_unite, ls_PROP_CODE, arg_ref_edi_ligne, ls_indbloq_ach_dev_pu
                 );

                -- DEB TRANSPORT PAR DEFAUT
                -- Vérification que le bassin de la station est en phase avec le bassin du transport par défaut souhaité par l'entrepôt
                -- Table GEO_ENT_TRP_BASSIN
                if ls_arg_bassin = '' OR ls_arg_bassin is null  then
                    begin
                        select trp_code, trp_bta_code, trp_pu, trp_dev_code, dev_tx
                        into ls_trp_code, ls_trp_bta_code, ld_trp_pu, ls_trp_dev_code, ld_dev_tx
                        from geo_ent_trp_bassin , geo_devise_ref
                        where cen_ref = arg_cen_ref and
                                bac_code = ls_bac_code and
                                dev_code = trp_dev_code and
                                dev_code_ref = ls_soc_dev_code;

                        update geo_ordre set trp_code = ls_trp_code, trp_dev_pu = ld_trp_pu, trp_dev_code = ls_trp_dev_code, trp_bta_code = ls_trp_bta_code, dev_tx = ld_dev_tx
                        where ord_ref = arg_ord_ref;

                        ls_arg_bassin := ls_bac_code;
                    exception when others then null;
                    end;
                end if;
                -- FIN TRANSPORT PAR DEFAUT
                -- résa stock

                begin
                    select STO_REF, QTE_INI, QTE_RES into ls_sto_ref, ll_qte_ini, ll_qte_res from GEO_STOCK where FOU_CODE = ls_FOU_CODE AND ART_REF = ls_ART_REF AND VALIDE='O';

                    select seq_stm_num.nextval into ll_stm_ref from dual;
                    select nordre, cli_code into ls_nordre, ls_cli_code from GEO_ORDRE WHERE ORD_REF = arg_ord_ref;

                    ls_stm_ref := to_char(ll_stm_ref, '000000');
                    ls_desc := 'ordre ' || ls_nordre || '/' || ls_cli_code;

                    -- voir trigger GEO_STOMVT_BEF_INS qui actualise aussi geo_stock ainsi que les champs manquant de stomvt
                    begin
                        insert into geo_stomvt (stm_ref, sto_ref, nom_utilisateur, mvt_type, mvt_qte, ord_ref, art_ref, orl_ref, stm_desc, mod_user, mod_date)
                        values(ls_stm_ref, ls_sto_ref, arg_username, 'R', ll_qte_art_cde, arg_ord_ref, ls_ART_REF, ls_ORL_REF, ls_desc, arg_username, SYSDATE);

                        ll_qte_restante := ll_qte_ini - ll_qte_res - ll_qte_art_cde;
                        if ll_qte_restante > 0 then
                            ll_stock_nb_resa := 1;
                        else
                            ll_stock_nb_resa := -1;
                        end if;

                        update GEO_ORDLIG SET stock_nb_resa = ll_stock_nb_resa where orl_ref = ls_ORL_REF and ord_ref = ls_ORD_REF;
                    exception when others then
                        msg := 'erreur sur création de réservation orl_ref=' || ls_ORL_REF || ' ordre ' || arg_ord_ref || '/' || arg_cli_ref || ' : ' || SQLERRM;
                    end;
                exception when others then null;
                end;
            exception when others then
                msg := '%%% Erreur à la création de la ligne d ordre: ' || SQLERRM;
                return;
            end;
        end loop;
        CLOSE C_HISTO;
    else
        -- On insert la ligne article générique art_ref = '000000'
        -- Recup de ORL suivante
        select F_SEQ_ORL_SEQ() into ls_ORL_REF FROM DUAL;

        -- recherche du prochain numero de ligne dans nouvel ordre
        select TRIM(to_char(count(orl_ref)+1,'00')) into ls_orl_lig from GEO_ORDLIG where GEO_ORDLIG.ORD_REF = arg_ord_ref;

        -- Recup du pal_code au niveau de l'entrepôt
        select PAL_CODE into ls_pal_code from GEO_ENTREP where cen_ref = arg_cen_ref;

        -- Insert d'une ligne avec la ref article générique
        begin
            INSERT INTO GEO_ORDLIG (
                ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL, EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX, REMHF_TX, ART_REF, ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, ART_REF_KIT, GTIN_COLIS_KIT, REF_EDI_LIGNE, mod_user, mod_date
            ) VALUES (
                 ls_ORL_REF, ls_ORD_REF, ls_orl_lig , ls_PAL_CODE, 0 , 0, ll_qte_art_cde, 0, 0,0, 0, 0, ls_soc_dev_code, 'COLIS', ll_qte_art_cde, 0, 'COLIS', ll_qte_art_cde, '', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 'N', 'N', 'N', 'N', 'N', 'O', 0, 'N', 'N', '', 0, 0, '000000', '', 0, '1', 0, '000000', ls_ean_cli, arg_ref_edi_ligne, arg_username, SYSDATE
            );
        exception when others then
            msg := '%%% Erreur à la création de la ligne d ordre: ' || SQLERRM;
            return;
        end;
    end if;
end;
-- Creation des lignes articles en automatique pour les commandes EDI
-- avec réservation de stock
