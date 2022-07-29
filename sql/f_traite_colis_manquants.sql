CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_TRAITE_COLIS_MANQUANTS (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_err_msg clob := '';
    nRes number := 0;

    ll_EXP_NB_COL GEO_ORDLIG.exp_nb_col%TYPE;
    ld_EXP_PDS_BRUT GEO_ORDLIG.EXP_PDS_BRUT%TYPE;
    ld_EXP_PDS_NET GEO_ORDLIG.EXP_PDS_NET%TYPE;
    ld_ACH_PU GEO_ORDLIG.ACH_PU%TYPE;
    ld_ACH_QTE GEO_ORDLIG.ACH_QTE%TYPE;
    ls_ACH_BTA_CODE GEO_ORDLIG.ACH_BTA_CODE%TYPE;
    ld_VTE_PU GEO_ORDLIG.VTE_PU%TYPE;
    ld_VTE_QTE GEO_ORDLIG.VTE_QTE%TYPE;
    ls_VTE_BTA_CODE GEO_ORDLIG.VTE_BTA_CODE%TYPE;
    ls_U_PAR_COLIS GEO_ARTICLE.U_PAR_COLIS%TYPE;
    ls_orl_ref_manquant GEO_ORDLIG.ORL_REF%TYPE;

    ll_new_nb_colis number;
    ld_new_pds_brut number;
    ld_new_pds_net number;

    CURSOR C1 (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select orl_ref, nb_colis_manquant
        from geo_ordlig
        where ord_ref = ref_ordre and nb_colis_manquant > 0;
BEGIN
    -- correspond à f_traite_colis_manquants.pbl
    res := 0;
    msg := '';

    for r in C1(arg_ord_ref)
    loop
        -- On récupère les info
        begin
            select OL.exp_nb_col, OL.EXP_PDS_BRUT, OL.EXP_PDS_NET, OL.ACH_PU, OL.ACH_QTE, OL.ACH_BTA_CODE, OL.VTE_PU, OL.VTE_QTE, OL.VTE_BTA_CODE, A.U_PAR_COLIS
            into ll_EXP_NB_COL, ld_EXP_PDS_BRUT, ld_EXP_PDS_NET, ld_ACH_PU, ld_ACH_QTE, ls_ACH_BTA_CODE, ld_VTE_PU, ld_VTE_QTE, ls_VTE_BTA_CODE, ls_U_PAR_COLIS
            from geo_ordlig OL, geo_article A
            where OL.orl_ref = r.ORL_REF AND  ord_ref = arg_ord_ref  AND OL.ART_REF = A.ART_REF;
        exception when others then
            ls_err_msg := ls_err_msg || SQLERRM;
            nRes := nRes + 1;
        end;

        -- On crée la PK de la ligne
        begin
            select F_SEQ_ORL_SEQ INTO ls_orl_ref_manquant from dual;
        exception when others then
            ls_err_msg := ls_err_msg || SQLERRM;
            nRes := nRes + 1;
        end;

        -- On duplique
        begin
            Insert into GEO_ORDLIG ( orl_ref, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL, EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU, VTE_BTA_CODE, VTE_QTE, FOU_CODE, VALIDE, LIB_DLV, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FAC_NB_PAL, FAC_NB_COL, FAC_PDS_BRUT, FAC_PDS_NET, HT_NET, TVA_1, TVA_2, TVA_3, COMPTE_A_CREDIT, LIG_HT_BRUT_0, LIG_HT_BRUT_1, LIG_HT_BRUT_2, LIG_HT_RIST_0, LIG_HT_RIST_1, LIG_HT_RIST_2, LIG_HT_CTIFL_0, LIG_HT_CTIFL_1, LIG_HT_CTIFL_2, TVC_CODE, LIG_HT_CTIFLRIST_0, LIG_HT_CTIFLRIST_1, LIG_HT_CTIFLRIST_2, HT_BRUT, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FRAIS_UNITE, FLVERFOU, FLVERTRP, LIG_REMSF, LIG_REMHF, FOU_FACREF, BAC_CODE, REMSF_TX, REMHF_TX, VTE_PU_NET, ART_REF, ESP_CODE, LIG_HT_INTERFEL_0, LIG_HT_INTERFEL_1, LIG_HT_INTERFEL_2, LIG_HT_INTERFELRIST_0, LIG_HT_INTERFELRIST_1, LIG_HT_INTERFELRIST_2, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, USER_VERFOU, DATE_VERFOU, EXP_PAL_NB_COL, PAL_NB_PALINTER, DEMIPAL_IND, PROPR_CODE, IND_GRATUIT, NB_CQPHOTOS, INDBLOQ_ACH_DEV_PU, DATE_ENVOI_SYLEG_FOU, TOTFRAIS_PLATEFORME, LIG_HT_INTERFEL_IMP_0, LIG_HT_INTERFEL_IMP_1, LIG_HT_INTERFEL_IMP_2, LIG_HT_INTERFELRIST_IMP_0, LIG_HT_INTERFELRIST_IMP_1, LIG_HT_INTERFELRIST_IMP_2, ART_REF_KIT, GTIN_COLIS_KIT, NB_COLIS_MANQUANT)
            select ls_orl_ref_manquant, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, '0', '0', EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, '0', VTE_BTA_CODE, VTE_QTE, FOU_CODE, VALIDE, LIB_DLV, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FAC_NB_PAL, FAC_NB_COL, FAC_PDS_BRUT, FAC_PDS_NET, HT_NET, TVA_1, TVA_2, TVA_3, COMPTE_A_CREDIT, LIG_HT_BRUT_0, LIG_HT_BRUT_1, LIG_HT_BRUT_2, LIG_HT_RIST_0, LIG_HT_RIST_1, LIG_HT_RIST_2, LIG_HT_CTIFL_0, LIG_HT_CTIFL_1, LIG_HT_CTIFL_2, TVC_CODE, LIG_HT_CTIFLRIST_0, LIG_HT_CTIFLRIST_1, LIG_HT_CTIFLRIST_2, HT_BRUT, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU, FRAIS_UNITE, FLVERFOU, FLVERTRP, LIG_REMSF, LIG_REMHF, FOU_FACREF, BAC_CODE, REMSF_TX, REMHF_TX, VTE_PU_NET, ART_REF, ESP_CODE, LIG_HT_INTERFEL_0, LIG_HT_INTERFEL_1, LIG_HT_INTERFEL_2, LIG_HT_INTERFELRIST_0, LIG_HT_INTERFELRIST_1, LIG_HT_INTERFELRIST_2, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, USER_VERFOU, DATE_VERFOU, EXP_PAL_NB_COL, PAL_NB_PALINTER, DEMIPAL_IND, PROPR_CODE, IND_GRATUIT, NB_CQPHOTOS, INDBLOQ_ACH_DEV_PU, DATE_ENVOI_SYLEG_FOU, TOTFRAIS_PLATEFORME, LIG_HT_INTERFEL_IMP_0, LIG_HT_INTERFEL_IMP_1, LIG_HT_INTERFEL_IMP_2, LIG_HT_INTERFELRIST_IMP_0, LIG_HT_INTERFELRIST_IMP_1, LIG_HT_INTERFELRIST_IMP_2, ART_REF_KIT, GTIN_COLIS_KIT, r.NB_COLIS_MANQUANT * (-1)
            from geo_ordlig where orl_ref = r.ORL_REF;
        exception when others then
            ls_err_msg := ls_err_msg || SQLERRM;
            nRes := nRes + 1;
        end;

        -- Mise à jour des données de la ligne d'origine
        ll_new_nb_colis := ll_EXP_NB_COL - r.NB_COLIS_MANQUANT;

        If ll_EXP_NB_COL <> 0 then
            ld_new_pds_brut := round(ld_EXP_PDS_BRUT * (ll_EXP_NB_COL - r.NB_COLIS_MANQUANT) / ll_EXP_NB_COL, 2);
            ld_new_pds_net  := round(ld_EXP_PDS_NET   * (ll_EXP_NB_COL - r.NB_COLIS_MANQUANT) / ll_EXP_NB_COL, 2);
        Else
            ld_new_pds_brut := 0;
            ld_new_pds_net  := 0;
        End If;

        case ls_ACH_BTA_CODE
            when 'PIECE' then
                ld_ach_qte	:= ll_new_nb_colis;
            when 'SACHET' then
                ld_ach_qte	:= ll_new_nb_colis * ls_U_PAR_COLIS;
            when 'UNITE' then
                ld_ach_qte	:= ll_new_nb_colis;
            when 'BARQUE' then
                ld_ach_qte	:= ll_new_nb_colis * ls_U_PAR_COLIS;
            when 'COLIS' then
                ld_ach_qte	:= ll_new_nb_colis;
            when 'KILO' then
                ld_ach_qte	:= ld_new_pds_net;
            else
                 msg := 'Erreur : Erreur traitement des colis manquants.~r~nCas ACH_BTA_CODE non prévu.';
                 rollback;
                 return;
        end case;

        case ls_VTE_BTA_CODE
            when 'PIECE' then
                ld_vte_qte	:= ll_new_nb_colis;
            when 'SACHET' then
                ld_vte_qte	:= ll_new_nb_colis * ls_U_PAR_COLIS;
            when 'UNITE' then
                ld_vte_qte	:= ll_new_nb_colis;
            when 'BARQUE' then
                ld_vte_qte	:= ll_new_nb_colis * ls_U_PAR_COLIS;
            when 'COLIS' then
                ld_vte_qte	:= ll_new_nb_colis;
            when 'KILO' then
                ld_vte_qte	:= ld_new_pds_net;
            else
                 msg := 'Erreur : Erreur traitement des colis manquants.~r~nCas VTE_BTA_CODE non prévu.';
                 rollback;
                 return;
        end case;

        begin
            UPDATE GEO_ORDLIG OL SET OL.EXP_NB_COL = ll_new_nb_colis, OL.EXP_PDS_BRUT = ld_new_pds_brut, OL.EXP_PDS_NET = ld_new_pds_net,
                 OL.ACH_QTE = ld_ach_qte, OL.VTE_QTE = ld_vte_qte WHERE OL.ORL_REF = r.ORL_REF;
        exception when others then
            msg := 'Erreur : Erreur traitement des colis manquants.~r~n' || SQLERRM;
            nRes := nRes + 1;
        end;

        -- Mise à jour des données de la ligne des manquants en faisant les compléments pour que le total reste exacte
        ll_new_nb_colis := r.NB_COLIS_MANQUANT;
        ld_new_pds_brut := round(ld_EXP_PDS_BRUT - ld_new_pds_brut, 2);
	    ld_new_pds_net  := round(ld_EXP_PDS_NET - ld_new_pds_net, 2);

        case ls_ACH_BTA_CODE
            when 'PIECE' then
                ld_ach_qte	:= ll_new_nb_colis;
            when 'SACHET' then
                ld_ach_qte	:= ll_new_nb_colis * ls_U_PAR_COLIS;
            when 'UNITE' then
                ld_ach_qte	:= ll_new_nb_colis;
            when 'BARQUE' then
                ld_ach_qte	:= ll_new_nb_colis * ls_U_PAR_COLIS;
            when 'COLIS' then
                ld_ach_qte	:= ll_new_nb_colis;
            when 'KILO' then
                ld_ach_qte	:= ld_new_pds_net;
            else
                 msg := 'Erreur : Erreur traitement des colis manquants.~r~nCas ACH_BTA_CODE non prévu.';
                 rollback;
                 return;
        end case;

        -- OL.VTE_PU forcement à 0 puisque c'est devenu gratuit mais pas VTE_QTE qui apparait sur la facture
        case ls_VTE_BTA_CODE
            when 'PIECE' then
                ld_vte_qte	:= ll_new_nb_colis;
            when 'SACHET' then
                ld_vte_qte	:= ll_new_nb_colis * ls_U_PAR_COLIS;
            when 'UNITE' then
                ld_vte_qte	:= ll_new_nb_colis;
            when 'BARQUE' then
                ld_vte_qte	:= ll_new_nb_colis * ls_U_PAR_COLIS;
            when 'COLIS' then
                ld_vte_qte	:= ll_new_nb_colis;
            when 'KILO' then
                ld_vte_qte	:= ld_new_pds_net;
            else
                 msg := 'Erreur : Erreur traitement des colis manquants.~r~nCas VTE_BTA_CODE non prévu.';
                 rollback;
                 return;
        end case;

        begin
            UPDATE GEO_ORDLIG OL SET OL.EXP_NB_COL = ll_new_nb_colis, OL.EXP_PDS_BRUT = ld_new_pds_brut, OL.EXP_PDS_NET = ld_new_pds_net,
                 OL.ACH_QTE = ld_ach_qte, OL.VTE_QTE = ld_vte_qte, OL.EXP_NB_PAL = '0', OL.VTE_PU = '0', OL.NB_COLIS_MANQUANT = r.NB_COLIS_MANQUANT * (-1)  WHERE OL.ORL_REF = ls_orl_ref_manquant;
        exception when others then
            ls_err_msg := ls_err_msg || SQLERRM;
            nRes := nRes + 1;
        end;
    end loop;

    if nRes > 0 then
        msg := 'Erreur : Erreur traitement des colis manquants.~r~n ' || ls_err_msg;
        rollback;
        return;
    else
        -- Tout va bien
        commit;
        res := 1;
        return;
    end if;

END F_TRAITE_COLIS_MANQUANTS;
/

