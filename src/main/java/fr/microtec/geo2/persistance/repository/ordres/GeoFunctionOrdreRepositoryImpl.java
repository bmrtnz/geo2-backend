package fr.microtec.geo2.persistance.repository.ordres;

import java.math.BigDecimal;
import java.time.LocalDate;

import fr.microtec.geo2.persistance.GeoStringArrayType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.function.AbstractFunctionsRepositoryImpl;
import fr.microtec.geo2.persistance.repository.function.FunctionQuery;

@Repository
public class GeoFunctionOrdreRepositoryImpl extends AbstractFunctionsRepositoryImpl
        implements GeoFunctionOrdreRepository {

    @Override
    public FunctionResult ofValideEntrepotForOrdre(String code_entrepot) {
        return this.runMono("OF_VALIDE_ENTREPOT_FOR_ORDRE", "code_entrepot", String.class, code_entrepot);
    }

    @Override
    public FunctionResult fCalculMarge(String refOrdre) {
        return this.runMono("F_CALCUL_MARGE", "arg_ord_ref", String.class, refOrdre);
    }

    @Override
    public FunctionResult fCalculPerequation(String refOrdre, String codeSociete) {
        FunctionQuery query = this.build("F_CALCUL_PEREQUATION");

        query.attachInput("arg_ord_ref", String.class, refOrdre);
        query.attachInput("arg_soc_code", String.class, codeSociete);

        return query.fetch();
    }

    @Override
    public FunctionResult fCalculMargePrevi(String refOrdre, String codeSociete) {
        FunctionQuery query = this.build("F_CALCUL_MARGE_PREVI");

        query.attachInput("is_ord_ref", String.class, refOrdre);
        query.attachInput("is_soc_code", String.class, codeSociete);
        query.attachOutput("result", Float.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fRecupFrais(String varCode, String catCode, String scoCode, String tvtCode,
            Integer modeCulture, String origine) {
        FunctionQuery query = this.build("F_RECUP_FRAIS");

        query.attachInput("arg_var_code", String.class, varCode);
        query.attachInput("arg_cat_code", String.class, catCode);
        query.attachInput("arg_sco_code", String.class, scoCode);
        query.attachInput("arg_tvt_code", String.class, tvtCode);
        query.attachInput("arg_mode_culture", Integer.class, modeCulture);
        query.attachInput("arg_origine", String.class, origine);

        return query.fetch();
    }

    // @Override
    public FunctionResult fCalculQte(String argOrdRef, String argOrlRef, Float argPdsBrut, Float argPdsNet,
            Integer argAchQte, Integer argVteQte) {
        FunctionQuery query = super.build("F_CALCUL_QTE");

        query
                .attachInput("arg_ord_ref", String.class, argOrdRef)
                .attachInput("arg_orl_ref", String.class, argOrlRef)
                .attachInputOutput("arg_pds_brut", Float.class, argPdsBrut)
                .attachInputOutput("arg_pds_net", Float.class, argPdsNet)
                .attachInputOutput("arg_ach_qte", Integer.class, argAchQte)
                .attachInputOutput("arg_vte_qte", Integer.class, argVteQte);

        return query.fetch();
    }

    @Override
    public FunctionResult fAfficheOrdreBaf(String socCode, String scoCode, String cliRef, String cenRef,
            LocalDate dateMin, LocalDate dateMax, String codeAss, String codeCom) {
        FunctionQuery query = this.build("F_AFFICHE_ORDRE_BAF", GeoOrdreBaf.class);

        query.attachInput("gs_soc_code", String.class, socCode);
        query.attachInput("is_sco_code", String.class, scoCode);
        query.attachInput("is_cli_ref", String.class, cliRef);
        query.attachInput("is_cen_ref", String.class, cenRef);
        query.attachInput("id_date_min", LocalDate.class, dateMin);
        query.attachInput("id_date_max", LocalDate.class, dateMax);
        query.attachInput("is_per_codeass", String.class, codeAss);
        query.attachInput("is_per_codecom", String.class, codeCom);
        query.attachCursor("c_ordre_baf");

        return query.fetch();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Require for skip error (Max cursor open) in oracle
    public FunctionResult fControlOrdreBaf(String refOrdre, String scoCode) {
        FunctionQuery query = this.build("F_CONTROL_ORDRE_BAF");

        query.attachInput("arg_ord_ref", String.class, refOrdre);
        query.attachInput("arg_soc_code", String.class, scoCode);

        query.attachOutput("ind_baf", String.class);
        query.attachOutput("ind_trp", String.class);
        query.attachOutput("ind_prix", String.class);
        query.attachOutput("ind_qte", String.class);
        query.attachOutput("ind_autre", String.class);
        query.attachOutput("ind_station", String.class);
        query.attachOutput("ind_date", String.class);
        query.attachOutput("desc_ctl", String.class);
        query.attachOutput("pc_marge_brute", BigDecimal.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fNouvelOrdre(String socCode) {
        FunctionQuery query = this.build("F_NOUVEL_ORDRE");

        query.attachInput("arg_soc_code", String.class, socCode);
        query.attachOutput("ls_nordre", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fVerifOrdreWarning(String refOrdre, String socCode) {
        FunctionQuery query = this.build("F_VERIF_ORDRE_WARNING");

        query.attachInput("arg_ord_ref", String.class, refOrdre);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult ofInitArticle(String ordRef, String artRef, String socCode) {
        FunctionQuery query = this.build("OF_INIT_ARTICLE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_art_ref", String.class, artRef);
        query.attachInput("arg_soc_code", String.class, socCode);
        query.attachOutput("new_orl_ref", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fGenereDluo(String input, LocalDate dateExp, LocalDate dateLiv) {
        FunctionQuery query = this.build("F_GENERE_DLUO");

        query.attachInput("arg_param", String.class, input);
        query.attachInput("arg_datexp", LocalDate.class, dateExp);
        query.attachInput("arg_datliv", LocalDate.class, dateLiv);
        query.attachOutput("arg_dluo", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult ofInitArtrefGrp(String orlRef) {
        return this.runMono("OF_INIT_ARTREF_GRP", "cur_orl_ref", String.class, orlRef);
    }

    @Override
    public FunctionResult fInitBlocageOrdre(String ordRef, String user) {
        FunctionQuery query = this.build("F_INIT_BLOCAGE_ORDRE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_user", String.class, user);
        query.attachOutput("bloquer", Character.class, v -> ((Character) v).equals('O'));

        return query.fetch();
    }

    @Override
    public FunctionResult ofRepartitionPalette(String ordreLigneRef, String secteurCode, String nomUtilisateur) {
        FunctionQuery query = this.build("OF_REPARTITION_PALETTE");

        query.attachInput("arg_orl_ref", String.class, ordreLigneRef);
        query.attachInput("gs_sco_code", String.class, secteurCode);
        query.attachInput("gs_user", String.class, nomUtilisateur);

        return query.fetch();
    }

    @Override
    public FunctionResult ofVerifLogistiqueDepart(String ordRef) {
        return this.runMono("OF_VERIF_LOGISTIQUE_DEPART", "arg_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult ofCalculRegimeTvaEncours(String ordRef, String regimeTVA) {
        FunctionQuery query = this.build("OF_CALCUL_REGIME_TVA_ENCOURS");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("str_regime_tva_defaut", String.class, regimeTVA);

        return query.fetch();
    }

    @Override
    public FunctionResult fResaUneLigne(
            String arg_fou_code,
            String arg_prop_code,
            String arg_art_ref,
            String arg_username,
            Integer arg_qte_resa,
            String arg_ord_ref,
            String arg_orl_ref,
            String arg_desc,
            String arg_pal_code) {
        FunctionQuery query = this.build("F_RESA_UNE_LIGNE");

        query.attachInput("arg_fou_code", String.class, arg_fou_code);
        query.attachInput("arg_prop_code", String.class, arg_prop_code);
        query.attachInput("arg_art_ref", String.class, arg_art_ref);
        query.attachInput("arg_username", String.class, arg_username);
        query.attachInput("arg_qte_resa", Integer.class, arg_qte_resa);
        query.attachInput("arg_ord_ref", String.class, arg_ord_ref);
        query.attachInput("arg_orl_ref", String.class, arg_orl_ref);
        query.attachInput("arg_desc", String.class, arg_desc);
        query.attachInput("arg_pal_code", String.class, arg_pal_code);
        query.attachOutput("nb_resa", Integer.class);
        query.attachOutput("nb_dispo", Integer.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fGetInfoResa(String orlRef) {
        FunctionQuery query = this.build("F_GET_INFO_RESA");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachOutput("ll_tot_qte_ini", Integer.class);
        query.attachOutput("ll_tot_qte_res", Integer.class);
        query.attachOutput("ll_tot_mvt_qte", Integer.class);
        query.attachOutput("ll_tot_nb_resa", Integer.class);

        return query.fetch();
    }

    @Override
    public FunctionResult ofSauveOrdre(String ordRef) {
        return this.runMono("OF_SAUVE_ORDRE", "arg_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fVerifLogistiqueOrdre(String ordRef) {
        return this.runMono("F_VERIF_LOGISTIQUE_ORDRE", "arg_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fAjoutOrdlog(String orxRef, String typePassage, String choixPassage) {
        FunctionQuery query = this.build("F_AJOUT_ORDLOG");

        query.attachInput("arg_orx_ref", String.class, orxRef);
        query.attachInput("arg_typ_passage", String.class, typePassage);
        query.attachInput("arg_ch_passage", String.class, choixPassage);

        return query.fetch();
    }

    @Override
    public FunctionResult geoPrepareEnvois(
            String ordRef,
            String fluCode,
            Character modeAuto,
            Character annOrdre,
            String user) {
        FunctionQuery query = this.build("GEO_PREPARE_ENVOIS");

        query.attachInput("is_ord_ref", String.class, ordRef);
        query.attachInput("is_flu_code", String.class, fluCode);
        query.attachInput("mode_auto", Character.class, modeAuto);
        query.attachInput("ann_ordre", Character.class, annOrdre);
        query.attachInput("arg_nom_utilisateur", String.class, user);
        query.attachCursor("co");

        return query.fetch();
    }

    @Override
    public FunctionResult ofAREnvois(String ordRef) {
        return this.runMono("OF_AR_ENVOIS", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult onChangeCdeNbPal(String orlRef, String scoCode) {
        FunctionQuery query = this.build("ON_CHANGE_CDE_NB_PAL");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("gs_sco_code", String.class, scoCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeDemipalInd(String orlRef, String username) {
        FunctionQuery query = this.build("ON_CHANGE_DEMIPAL_IND");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("gs_user", String.class, username);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangePalNbCol(String orlRef, String username) {
        FunctionQuery query = this.build("ON_CHANGE_PAL_NB_COL");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("gs_user", String.class, username);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeCdeNbCol(String orlRef, String username) {
        FunctionQuery query = this.build("ON_CHANGE_CDE_NB_COL");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("gs_user", String.class, username);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeProprCode(String orlRef, String username, String socCode) {
        FunctionQuery query = this.build("ON_CHANGE_PROPR_CODE");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_user", String.class, username);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeFouCode(String orlRef, String username, String socCode) {
        FunctionQuery query = this.build("ON_CHANGE_FOU_CODE");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_user", String.class, username);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangePalCode(String orlRef, String username, String scoCode) {
        FunctionQuery query = this.build("ON_CHANGE_PAL_CODE");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_user", String.class, username);
        query.attachInput("arg_sco_code", String.class, scoCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeVtePu(String orlRef) {
        return this.runMono("ON_CHANGE_VTE_PU", "arg_orl_ref", String.class, orlRef);
    }

    @Override
    public FunctionResult onChangePalinterCode(String orlRef) {
        return this.runMono("ON_CHANGE_PALINTER_CODE", "arg_orl_ref", String.class, orlRef);
    }

    @Override
    public FunctionResult onChangeIndGratuit(String orlRef) {
        return this.runMono("ON_CHANGE_IND_GRATUIT", "arg_orl_ref", String.class, orlRef);
    }

    @Override
    public FunctionResult onChangeAchDevPu(String orlRef, String socCode) {
        FunctionQuery query = this.build("ON_CHANGE_ACH_DEV_PU");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangePalNbPalinter(String orlRef, String username) {
        FunctionQuery query = this.build("ON_CHANGE_PAL_NB_PALINTER");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_user", String.class, username);

        return query.fetch();
    }

    @Override
    public FunctionResult fDetailsExpOnClickAuto(String orlRef) {
        FunctionQuery query = this.build("F_DETAILS_EXP_ON_CLICK_AUTO");

        query.attachInput("arg_orl_ref", String.class, orlRef);

        return query.fetch();
    }

    @Override
    public FunctionResult fChgtQteArtRet(String ordRef) {
        FunctionQuery query = this.build("F_CHGT_QTE_ART_RET");

        query.attachInput("arg_ord_ref", String.class, ordRef);

        return query.fetch();
    }

    @Override
    public FunctionResult fDetailsExpClickModifier(String ordRef, String orlRef, String histoOrxRef) {
        FunctionQuery query = this.build("F_DETAILS_EXP_CLICK_MODIFIER");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_histo_orx_ref", String.class, histoOrxRef);

        return query.fetch();
    }

    @Override
    public FunctionResult fConfirmationCommande(String ordRef, String socCode, String username) {
        FunctionQuery query = this.build("F_CONFIRMATION_COMMANDE");

        query.attachInput("is_ord_ref", String.class, ordRef);
        query.attachInput("is_soc_code", String.class, socCode);
        query.attachInput("is_utilisateur", String.class, username);

        return query.fetch();
    }

    @Override
    public FunctionResult fDocumentEnvoiDetailsExp(String ordRef, String socCode) {
        FunctionQuery query = this.build("F_DOCUMENT_ENVOI_DETAILS_EXP");

        query.attachInput("is_ord_ref", String.class, ordRef);
        query.attachInput("is_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fDocumentEnvoiConfirmationPrixAchat(String ordRef) {
        return this.runMono("F_DOC_ENVOI_CONF_PRIX_ACHAT", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fDocumentEnvoiFichesPalette(String ordRef) {
        return this.runMono("F_DOCUMENT_ENVOI_FICHES_PAL", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fDocumentEnvoiGenereTraca(String ordRef) {
        return this.runMono("F_DOCUMENT_ENVOI_FICHES_PAL", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fDocumentEnvoiAfficheCMR(String ordRef) {
        return this.runMono("F_DOCUMENT_ENVOI_AFFICHE_CMR", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fDocumentEnvoiBonLivraison(String ordRef) {
        return this.runMono("F_DOCUMENT_ENVOI_BON_LIVRAISON", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fDocumentEnvoiProforma(String ordRef) {
        return this.runMono("F_DOCUMENT_ENVOI_PROFORMA", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fDocumentEnvoiCominv(String ordRef) {
        return this.runMono("F_DOCUMENT_ENVOI_COMINV", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fDocumentEnvoiShipmentBuyco(String ordRef) {
        return this.runMono("F_DOCUMENT_ENVOI_SHIP_BUYCO", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fDocumentEnvoiDeclarationBollore(String ordRef) {
        return this.runMono("F_DOC_ENVOI_DEC_BOLLORE", "is_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fClotureLogGrp(String ordRef, String fouCode, Character expedie) {
        FunctionQuery query = this.build("F_CLOTURE_LOG_GRP");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_fou_code", String.class, fouCode);
        query.attachInput("arg_exped_fournni", Character.class, expedie);

        return query.fetch();
    }

    @Override
    public FunctionResult fSetDetailKitArticle(String ordRef, String fouCode) {
        FunctionQuery query = this.build("F_SET_DETAIL_KIT_ARTICLE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_fou_code", String.class, fouCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fActualiseNbPalettesSol(String ordRef, String fouCode) {
        FunctionQuery query = this.build("F_ACTUALISE_NB_PALETTES_SOL");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_fou_code", String.class, fouCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fGetQttPerBta(String artRef, String btaCode, Double nbPal, Double nbCol, Double pdsNet) {
        FunctionQuery query = this.build("F_GET_QTT_PER_BTA");

        query.attachInput("arg_art_ref", String.class, artRef);
        query.attachInput("arg_bta_code", String.class, btaCode);
        query.attachInput("arg_nb_pal", Double.class, nbPal);
        query.attachInput("arg_nb_col", Double.class, nbCol);
        query.attachInput("arg_pds_net", Double.class, pdsNet);
        query.attachOutput("ld_qte", Double.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fSubmitEnvoiDetailSeccom(String ordRef, String fouCode, String logName) {
        FunctionQuery query = this.build("F_SUBMIT_ENVOI_DETAIL_SECCOM");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_fou_code", String.class, fouCode);
        query.attachInput("log_name", String.class, logName);

        return query.fetch();
    }

    @Override
    public FunctionResult fDetailEnteteSauve(String ordRef, String mode, Character checkPalette) {
        FunctionQuery query = this.build("F_DETAIL_ENTETE_SAUVE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_mode", String.class, mode);
        query.attachInput("arg_check_palette", Character.class, checkPalette);

        return query.fetch();
    }

    @Override
    public FunctionResult fTracabiliteCloturer(String ordRef, Character cloturer) {
        FunctionQuery query = this.build("F_TRACABILITE_CLOTURER");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_cloturer", Character.class, cloturer);

        return query.fetch();
    }

    @Override
    public FunctionResult fDetailsExpOnCheckCloturer(String orxRef, String devalexpRef, String username,
            String socCode) {
        FunctionQuery query = this.build("F_EXP_ON_CHECK_CLOTURER");

        query.attachInput("arg_orx_ref", String.class, orxRef);
        query.attachInput("arg_devalexp_ref", String.class, devalexpRef);
        query.attachInput("arg_username", String.class, username);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fBonAFacturerPrepare(String ordRef, String socCode) {
        FunctionQuery query = this.build("F_BON_A_FACTURER_PREPARE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fBonAFacturer(String ordRef, String socCode) {
        FunctionQuery query = this.build("F_BON_A_FACTURER");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fSuppressionOrdre(String ordRef, String username, String commentaire) {
        FunctionQuery query = this.build("F_SUPPRESSION_ORDRE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_username", String.class, username);
        query.attachInput("arg_commentaire", String.class, commentaire);

        return query.fetch();
    }

    @Override
    public FunctionResult fTestAnnuleOrdre(String ordRef) {
        return this.runMono("F_TEST_ANNULE_ORDRE", "arg_ord_ref", String.class, ordRef);
    }

    @Override
    public FunctionResult fAnnulationOrdre(String ordRef, String motif) {
        FunctionQuery query = this.build("F_ANNULATION_ORDRE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_motif", String.class, motif);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreeOrdreComplementaire(String ordRef, String socCode, String username) {
        FunctionQuery query = this.build("F_CREE_ORDRE_COMPLEMENTAIRE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_soc_code", String.class, socCode);
        query.attachInput("arg_username", String.class, username);
        query.attachOutput("ls_ord_ref_compl", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreeOrdreRegularisation(String ordRef, String socCode, String lcaCode, String typReg, Boolean indDetail, String username, String[] listOrlRef) {
        FunctionQuery query = this.build("F_CREE_ORDRE_REGULARISATION");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_soc_code", String.class, socCode);
        query.attachInput("arg_lca_code", String.class, lcaCode);
        query.attachInput("arg_typ_reg", String.class, typReg);
        query.attachInput("arg_ind_detail", String.class, indDetail ? "O" : "N");
        query.attachInput("arg_username", String.class, username);
        query.attachInput("arg_list_orl_ref", GeoStringArrayType.class, listOrlRef);
        query.attachOutput("ls_ord_ref_regul", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreateOrdresEdi(String ediOrdre, String camCode, String socCode, String cliRef, String cenRef, String refCmd, String dateLiv, String username) {
        FunctionQuery query = this.build("F_CREATE_ORDRES_EDI");

        query.attachInput("arg_edi_ordre", String.class, ediOrdre);
        query.attachInput("arg_cam_code", String.class, camCode);
        query.attachInput("arg_soc_code", String.class, socCode);
        query.attachInput("arg_cli_ref", String.class, cliRef);
        query.attachInput("arg_cen_ref", String.class, cenRef);
        query.attachInput("arg_ref_cmd", String.class, refCmd);
        query.attachInput("arg_date_liv", String.class, dateLiv);
        query.attachInput("arg_username", String.class, username);
        query.attachOutput("ls_nordre_tot", String.class);
        query.attachOutput("tab_ordre_cree", GeoStringArrayType.class);

        return query.fetch();
    }

        return query.fetch();
    }
}
