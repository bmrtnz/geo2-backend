package fr.microtec.geo2.persistance.repository.ordres;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.common.TemporalUtils;
import fr.microtec.geo2.persistance.GeoStringArrayType;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.function.AbstractFunctionsRepositoryImpl;
import fr.microtec.geo2.persistance.repository.function.FunctionQuery;
import fr.microtec.geo2.service.FunctionOrdreService;

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
    public FunctionResult ofInitArticle(String ordRef, String artRef, String socCode, String orlRefUpdate) {
        FunctionQuery query = this.build("OF_INIT_ARTICLE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_art_ref", String.class, artRef);
        query.attachInput("arg_soc_code", String.class, socCode);
        query.attachInput("orl_ref_update", String.class, orlRefUpdate);
        query.attachOutput("new_orl_ref", String.class);
        query.attachOutput("art_ass", String.class);

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
    public FunctionResult fResaAutoOrdre(String arg_ord_ref, String arg_username) {
        FunctionQuery query = this.build("F_RESA_AUTO_ORDRE");

        query.attachInput("arg_ord_ref", String.class, arg_ord_ref);
        query.attachInput("arg_username", String.class, arg_username);
        query.attachOutput("result", GeoStringArrayType.class);

        return FunctionOrdreService.parseResaAutoResult(query.fetch());
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
    public FunctionResult fDocumentEnvoiFactureDouaniere(String ordRef) {
        return this.runMono("F_DOC_ENVOI_FACTURE_DOUANIERE", "is_ord_ref", String.class, ordRef);
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
    public FunctionResult fCreeOrdreRegularisation(String ordRef, String socCode, String lcaCode, String typReg,
            Boolean indDetail, String username, String[] listOrlRef) {
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
    public FunctionResult fCreateOrdresEdi(String ediOrdre, String camCode, String socCode, String cliRef,
            String cenRef, String refCmd, String dateLiv, String username) {
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

    @Override
    public FunctionResult wDupliqueOrdreOnDuplique(
            String ordRef,
            String user,
            String socCode,
            String cenRef,
            LocalDateTime depDate,
            LocalDate livDate,
            Boolean withCodeChargement,
            Boolean withEtdLocation,
            Boolean withEtaLocation,
            Boolean withEtdDate,
            Boolean withEtaDate,
            Boolean withIncCode,
            Boolean withFourni,
            Boolean withVtePu,
            Boolean withAchPu,
            Boolean withLibDlv) {
        FunctionQuery query = this.build("W_DUPLIQUE_ORDRE_ON_DUPLIQUE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_username", String.class, user);
        query.attachInput("arg_soc_code", String.class, socCode);
        query.attachInput("arg_cen_ref", String.class, cenRef);
        query.attachInput("arg_depdatp", LocalDateTime.class, depDate);
        query.attachInput("arg_livdatp", LocalDate.class, livDate);
        query.attachInput("arg_code_chargement", Character.class, withCodeChargement ? 'O' : 'N');
        query.attachInput("arg_etd_location", Character.class, withEtdLocation ? 'O' : 'N');
        query.attachInput("arg_eta_location", Character.class, withEtaLocation ? 'O' : 'N');
        query.attachInput("arg_etd_date", Character.class, withEtdDate ? 'O' : 'N');
        query.attachInput("arg_eta_date", Character.class, withEtaDate ? 'O' : 'N');
        query.attachInput("arg_inc_code", Character.class, withIncCode ? 'O' : 'N');
        query.attachInput("arg_fourni", Character.class, withFourni ? 'O' : 'N');
        query.attachInput("arg_vte_pu", Character.class, withVtePu ? 'O' : 'N');
        query.attachInput("arg_ach_pu", Character.class, withAchPu ? 'O' : 'N');
        query.attachInput("arg_lib_dlv", Character.class, withLibDlv ? 'O' : 'N');
        query.attachOutput("nordre", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult ofInitRegimeTva(String ordreRef, String tvrCode) {
        FunctionQuery query = this.build("OF_INIT_REGIME_TVA");

        query.attachInput("is_cur_ord_ref", String.class, ordreRef);
        query.attachInput("is_tvr_code_entrepot", String.class, tvrCode);

        return query.fetch();
    }

    @Override
    public FunctionResult setTransporteurBassin(String orlRef, String socCode) {
        FunctionQuery query = this.build("SET_TRANSPORTEUR_BASSIN");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fDecomptePalox(
            Long nbPallox,
            String fouCode,
            String colCode,
            String cenRef,
            String espCode,
            LocalDate dateApplication,
            String socCode) {
        FunctionQuery query = this.build("F_DECOMPTE_PALOX");

        query.attachInput("arg_nb_pallox", Long.class, nbPallox);
        query.attachInput("arg_fou_code", String.class, fouCode);
        query.attachInput("arg_col_code", String.class, colCode);
        query.attachInput("arg_cen_ref", String.class, cenRef);
        query.attachInput("arg_esp_code", String.class, espCode);
        query.attachInput("arg_date_application", LocalDate.class, dateApplication);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fAjustPallox(
            String socCode,
            String cenCode,
            String fouCode,
            String colCode,
            String espCode,
            Integer nbPallox,
            LocalDate dateApplication,
            String commentaire,
            String cliCode) {
        FunctionQuery query = this.build("F_AJUST_PALLOX");

        query.attachInput("gs_soc_code", String.class, socCode);
        query.attachInput("arg_cen_code", String.class, cenCode);
        query.attachInput("arg_fou_code", String.class, fouCode);
        query.attachInput("arg_col_code", String.class, colCode);
        query.attachInput("arg_esp_code", String.class, espCode);
        query.attachInput("arg_nb_pallox", Integer.class, nbPallox);
        query.attachInput("arg_date_application", LocalDate.class, dateApplication);
        query.attachInput("arg_commentaire", String.class, commentaire);
        query.attachInput("arg_cli_code", String.class, cliCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreateOrdreV4(
            String arg_societe,
            String arg_client,
            String arg_entrepot,
            String arg_transporteur,
            String arg_ref_cmd_cli,
            Boolean arg_is_baf,
            Boolean arg_is_regulation,
            LocalDateTime arg_datedep,
            String arg_type_ordre,
            LocalDateTime arg_date_liv,
            String arg_load_ref) {
        FunctionQuery query = this.build("F_CREATE_ORDRE_V4");

        query.attachInput("arg_societe", String.class, arg_societe);
        query.attachInput("arg_client", String.class, arg_client);
        query.attachInput("arg_entrepot", String.class, arg_entrepot);
        query.attachInput("arg_transporteur", String.class, arg_transporteur);
        query.attachInput("arg_ref_cmd_cli", String.class, arg_ref_cmd_cli);
        query.attachInput("arg_is_baf", Character.class, arg_is_baf ? 'O' : 'N');
        query.attachInput("arg_is_regulation", Character.class, arg_is_regulation ? 'O' : 'N');
        query.attachInput("arg_datedep", LocalDateTime.class, arg_datedep);
        query.attachInput("arg_type_ordre", String.class, arg_type_ordre);
        query.attachInput("arg_date_liv", LocalDateTime.class, arg_date_liv);
        query.attachInput("arg_load_ref", String.class, arg_load_ref);
        query.attachOutput("ls_ord_ref", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreateLigneOrdre(
            String arg_ord_ref,
            String arg_art_ref,
            String arg_fou_code,
            String arg_cen_ref,
            Double arg_pal_nb_col,
            Double arg_nb_pal,
            Double arg_nb_colis,
            Double arg_prix_vente,
            Double arg_prix_mini,
            String arg_prog,
            String arg_dluo) {
        FunctionQuery query = this.build("F_CREATE_LIGNE_ORDRE");

        query.attachInput("arg_ord_ref", String.class, arg_ord_ref);
        query.attachInput("arg_art_ref", String.class, arg_art_ref);
        query.attachInput("arg_fou_code", String.class, arg_fou_code);
        query.attachInput("arg_cen_ref", String.class, arg_cen_ref);
        query.attachInput("arg_pal_nb_col", Double.class, arg_pal_nb_col);
        query.attachInput("arg_nb_pal", Double.class, arg_nb_pal);
        query.attachInput("arg_nb_colis", Double.class, arg_nb_colis);
        query.attachInput("arg_prix_vente", Double.class, arg_prix_vente);
        query.attachInput("arg_prix_mini", Double.class, arg_prix_mini);
        query.attachInput("arg_prog", String.class, arg_prog);
        query.attachInput("arg_dluo", String.class, arg_dluo);
        query.attachOutput("ls_orl_ref", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fnMajOrdreRegroupementV2(
            String arg_ord_ref_origine,
            String arg_soc_code,
            Boolean arg_entrepot_generic,
            String arg_username) {
        FunctionQuery query = this.build("FN_MAJ_ORDRE_REGROUPEMENT_V2");

        query.attachInput("arg_ord_ref_origine", String.class, arg_ord_ref_origine);
        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("arg_entrepot_generic", Character.class, arg_entrepot_generic ? 'O' : 'N');
        query.attachInput("arg_username", String.class, arg_username);

        return query.fetch();
    }

    @Override
    public FunctionResult fEnvoiBLAuto(
            String gs_soc_code,
            String ls_sco_code,
            LocalDate arg_date_min,
            LocalDate arg_date_max,
            String arg_utilisateur) {
        FunctionQuery query = this.build("F_ENVOI_BL_AUTO");

        query.attachInput("gs_soc_code", String.class, gs_soc_code);
        query.attachInput("ls_sco_code", String.class, ls_sco_code);
        query.attachInput("arg_date_min", LocalDate.class, arg_date_min);
        query.attachInput("arg_date_max", LocalDate.class, arg_date_max);
        query.attachInput("arg_utilisateur", String.class, arg_utilisateur);
        query.attachOutput("array_ord_ref", GeoStringArrayType.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fTakeOptionStock(
            Integer quantite,
            String stockRef,
            String propCode,
            String palCode,
            String stockDescription) {
        FunctionQuery query = this.build("F_TAKE_OPTION_STOCK");

        query.attachInput("em_qte_res", Integer.class, quantite);
        query.attachInput("is_sto_ref", String.class, stockRef);
        query.attachInput("is_prop_code", String.class, propCode);
        query.attachInput("is_pal_code", String.class, palCode);
        query.attachInput("is_sto_desc", String.class, stockDescription);

        return query.fetch();
    }

    @Override
    public FunctionResult fDuplicationBukSa(
            String is_ord_ref,
            String is_soc_code,
            String is_utilisateur,
            String is_tvr_code_entrepot) {
        FunctionQuery query = this.build("F_DUPLICATION_BUK_SA");

        query.attachInput("is_ord_ref", String.class, is_ord_ref);
        query.attachInput("is_soc_code", String.class, is_soc_code);
        query.attachInput("is_utilisateur", String.class, is_utilisateur);
        query.attachInput("is_tvr_code_entrepot", String.class, is_tvr_code_entrepot);

        return query.fetch();
    }

    @Override
    public FunctionResult fDelRegroupement(String is_cur_ord_ref) {
        FunctionQuery query = this.build("F_DEL_REGROUPEMENT");

        query.attachInput("is_cur_ord_ref", String.class, is_cur_ord_ref);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreeOrdreReplacement(
            String arg_ord_ref_origine,
            String arg_cen_ref,
            String gs_username,
            String gs_soc_code) {
        FunctionQuery query = this.build("F_CREE_ORDRE_REPLACEMENT");

        query.attachInput("arg_ord_ref_origine", String.class, arg_ord_ref_origine);
        query.attachInput("arg_cen_ref", String.class, arg_cen_ref);
        query.attachInput("gs_username", String.class, gs_username);
        query.attachInput("gs_soc_code", String.class, gs_soc_code);
        query.attachOutput("ls_ord_ref_replace", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreeOrdreReplacementLigne(
            String litigeLigneID,
            String ordreID,
            String ordreOriginID,
            String ordreLigneOriginID,
            String societeID) {
        FunctionQuery query = this.build("F_CREE_ORDRE_REPLACEMENT_LIGNE");

        query.attachInput("arg_lil_ref", String.class, litigeLigneID);
        query.attachInput("arg_ord_ref", String.class, ordreID);
        query.attachInput("arg_ord_ref_ori", String.class, ordreOriginID);
        query.attachInput("arg_orl_ref_ori", String.class, ordreLigneOriginID);
        query.attachInput("arg_soc_code", String.class, societeID);
        query.attachOutput("ls_orl_ref", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreeOrdreReedFact(
            String ordreOriginID,
            String societeID,
            String nomUtilisateur) {
        FunctionQuery query = this.build("F_CREE_ORDRE_REED_FACT");

        query.attachInput("arg_ord_ref_origine", String.class, ordreOriginID);
        query.attachInput("gs_soc_code", String.class, societeID);
        query.attachInput("gs_username", String.class, nomUtilisateur);
        query.attachOutput("ls_ord_ref_reed_fact", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreeOrdreReedFactLigne(
            String ordreID,
            String ordreOriginID,
            String societeID) {
        FunctionQuery query = this.build("F_CREE_ORDRE_REED_FACT_LIGNE");

        query.attachInput("arg_ord_ref", String.class, ordreID);
        query.attachInput("arg_ord_ref_ori", String.class, ordreOriginID);
        query.attachInput("gs_soc_code", String.class, societeID);
        query.attachOutput("ls_orl_ref", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fReturnForfaitsTrp(
            String cenRef,
            String incCode,
            String typeOrd) {
        FunctionQuery query = this.build("F_RETURN_FORFAITS_TRP");

        query.attachInput("arg_cen_ref", String.class, cenRef);
        query.attachInput("arg_inc_code", String.class, incCode);
        query.attachOutput("arg_trp_dev_pu", Double.class);
        query.attachOutput("arg_bta_code", String.class);
        query.attachOutput("arg_dev_code", String.class);
        query.attachInput("arg_typ_ordre", String.class, typeOrd);
        query.attachOutput("li_ret", Integer.class);

        return query.fetch();
    }

    @Override
    public FunctionResult prcGenFraisDedimp(String arg_ord_ref) {
        FunctionQuery query = this.build("PRC_GEN_FRAIS_DEDIMP");

        query.attachInput("arg_ord_ref", String.class, arg_ord_ref);

        return query.fetch();
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
    public FunctionResult onChangeTrpDevCode(
            String arg_ord_ref,
            String arg_trp_dev_code,
            String arg_soc_code,
            Float arg_trp_pu) {
        FunctionQuery query = this.build("ON_CHANGE_TRP_DEV_CODE");

        query.attachInput("arg_ord_ref", String.class, arg_ord_ref);
        query.attachInput("arg_trp_dev_code", String.class, arg_trp_dev_code);
        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("arg_trp_pu", Float.class, arg_trp_pu);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreatePreordre(
            String arg_soc_code,
            String arg_cli_ref,
            String arg_cen_ref,
            String arg_trp_code,
            String arg_ref_cmd_cli,
            Boolean arg_is_regulation,
            Boolean arg_is_baf,
            LocalDateTime arg_dat_dep,
            LocalDateTime arg_dat_liv,
            String arg_instruction_log,
            String arg_assistante,
            String arg_commercial) {
        FunctionQuery query = this.build("F_CREATE_PREORDRE");

        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("arg_cli_ref", String.class, arg_cli_ref);
        query.attachInput("arg_cen_ref", String.class, arg_cen_ref);
        query.attachInput("arg_trp_code", String.class, arg_trp_code);
        query.attachInput("arg_ref_cmd_cli", String.class, arg_ref_cmd_cli);
        query.attachInput("arg_is_regulation", Character.class, arg_is_regulation ? 'O' : 'N');
        query.attachInput("arg_is_baf", Character.class, arg_is_baf ? 'O' : 'N');
        query.attachInput("arg_dat_dep", String.class,
                arg_dat_dep.format(DateTimeFormatter.ofPattern(TemporalUtils.GEO_DATETIME_PATTERN)));
        query.attachInput("arg_dat_liv", String.class,
                arg_dat_liv.format(DateTimeFormatter.ofPattern(TemporalUtils.GEO_DATETIME_PATTERN)));
        query.attachInput("arg_instruction_log", String.class, arg_instruction_log);
        query.attachInput("arg_assistante", String.class, arg_assistante);
        query.attachInput("arg_commercial", String.class, arg_commercial);
        query.attachOutput("ls_ord_ref", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fCreateLignePreordre(
            String arg_ord_ref,
            String arg_cen_ref,
            Double arg_nb_colis,
            Double arg_nb_pal,
            Double arg_pal_nb_col,
            String arg_pal_code,
            Double arg_palinter_nb,
            String arg_art_ref,
            String arg_prop_code,
            String arg_fou_code,
            Double arg_prix_vente,
            Double arg_prix_achat,
            String arg_unite_achat,
            String arg_unite_vente) {
        FunctionQuery query = this.build("F_CREATE_LIGNE_PREORDRE");

        query.attachInput("arg_ord_ref", String.class, arg_ord_ref);
        query.attachInput("arg_cen_ref", String.class, arg_cen_ref);
        query.attachInput("arg_nb_colis", Double.class, arg_nb_colis);
        query.attachInput("arg_nb_pal", Double.class, arg_nb_pal);
        query.attachInput("arg_pal_nb_col", Double.class, arg_pal_nb_col);
        query.attachInput("arg_pal_code", String.class, arg_pal_code);
        query.attachInput("arg_palinter_nb", Double.class, arg_palinter_nb);
        query.attachInput("arg_art_ref", String.class, arg_art_ref);
        query.attachInput("arg_prop_code", String.class, arg_prop_code);
        query.attachInput("arg_fou_code", String.class, arg_fou_code);
        query.attachInput("arg_prix_vente", Double.class, arg_prix_vente);
        query.attachInput("arg_prix_achat", Double.class, arg_prix_achat);
        query.attachInput("arg_unite_achat", String.class, arg_unite_achat);
        query.attachInput("arg_unite_vente", String.class, arg_unite_vente);
        query.attachOutput("ls_orl_ref", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult wAjoutArtRecapEdiColibri(
            String arg_art_ref,
            String arg_fou_code,
            String arg_prop_code,
            Integer arg_qte_valide,
            BigDecimal arg_k_stock_art_edi_bassin) {
        FunctionQuery query = this.build("W_AJOUT_ART_RECAP_EDI_COLIBRI");

        query.attachInput("arg_art_ref", String.class, arg_art_ref);
        query.attachInput("arg_fou_code", String.class, arg_fou_code);
        query.attachInput("arg_prop_code", String.class, arg_prop_code);
        query.attachInput("arg_qte_valide", Integer.class, arg_qte_valide);
        query.attachInput("arg_k_stock_art_edi_bassin", BigDecimal.class, arg_k_stock_art_edi_bassin);

        return query.fetch();
    }

    @Override
    public FunctionResult ofReadOrdEdiColibri(
            BigDecimal arg_num_cde_edi,
            String arg_cam_code,
            Character arg_stock_type) {
        FunctionQuery query = this.build("OF_READ_ORD_EDI_COLIBRI");

        query.attachInput("arg_num_cde_edi", BigDecimal.class, arg_num_cde_edi);
        query.attachInput("arg_cam_code", String.class, arg_cam_code);
        query.attachInput("arg_stock_type", Character.class, arg_stock_type);

        return query.fetch();
    }

    @Override
    public FunctionResult ofControleSelArt(
            BigDecimal arg_edi_ordre,
            String arg_cam_code) {
        FunctionQuery query = this.build("OF_CONTROLE_SEL_ART");

        query.attachInput("arg_edi_ordre", BigDecimal.class, arg_edi_ordre);
        query.attachInput("arg_cam_code", String.class, arg_cam_code);

        return query.fetch();
    }

    @Override
    public FunctionResult ofControleQteArt(
            BigDecimal arg_edi_ordre,
            String arg_cam_code) {
        FunctionQuery query = this.build("OF_CONTROLE_QTE_ART");

        query.attachInput("arg_edi_ordre", BigDecimal.class, arg_edi_ordre);
        query.attachInput("arg_cam_code", String.class, arg_cam_code);

        return query.fetch();
    }

}
