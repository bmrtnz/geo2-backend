package fr.microtec.geo2.persistance.repository.ordres;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.FunctionResult;

@Repository
public interface GeoFunctionOrdreRepository {

    /**
     * Vérifie si la création de l'ordre pour l'entrepot est autorisé
     */
    FunctionResult ofValideEntrepotForOrdre(String code_entrepot);

    /**
     * Retourne les ordres BAF.
     */
    FunctionResult fAfficheOrdreBaf(String socCode, String scoCode, String cliRef, String cenRef, LocalDate dateMin,
            LocalDate dateMax, String codeAss, String codeCom);

    /**
     * Control les ordres BAF.
     */
    FunctionResult fControlOrdreBaf(String refOrdre, String socCode);

    /**
     * Génère une nouvelle ref ordre.
     */
    FunctionResult fNouvelOrdre(String socCode);

    /**
     * Génère une nouvelle ligne d'ordre avec l'article sélectionné.
     */
    FunctionResult ofInitArticle(String ordRef, String artRef, String socCode);

    /**
     * Indicateur du blocage de l'ordre dont le départ est aujourd'hui
     */
    FunctionResult fInitBlocageOrdre(String ordRef, String user);

    /**
     * Verification de la bonne conformité de la logistique
     * avant envoi de documents et validation bon à facturer
     */
    FunctionResult fVerifLogistiqueOrdre(String ordRef);

    /**
     * Ajoute un lieu de passage dans la logistique
     */
    FunctionResult fAjoutOrdlog(String orxRef, String typePassage, String choixPassage);

    /**
     * Point d'entrée pour la gestion des flux de documents
     */
    FunctionResult geoPrepareEnvois(
            String ordRef,
            String fluCode,
            Character modeAuto,
            Character annOrdre,
            String user);

    /**
     * Gestion spécifique des annule-et-remplace
     * Pour Geo2, créé des lignes envois avec `traite = R`
     */
    FunctionResult ofAREnvois(String ordRef);

    // Sub procedures, declare only for testing
    FunctionResult fCalculMarge(String refOrdre);

    FunctionResult fRecupFrais(String varCode, String catCode, String scoCode, String tvtCode, Integer modeCulture,
            String origine);

    FunctionResult fCalculPerequation(String refOrdre, String codeSociete);

    FunctionResult fCalculMargePrevi(String refOrdre, String codeSociete);

    FunctionResult fVerifOrdreWarning(String refOrdre, String socCode);

    FunctionResult fGenereDluo(String input, LocalDate dateExp, LocalDate dateLiv);

    FunctionResult ofInitArtrefGrp(String orlRef);

    FunctionResult ofRepartitionPalette(String ordreLigneRef, String secteurCode, String nomUtilisateur);

    FunctionResult ofVerifLogistiqueDepart(String ordRef);

    FunctionResult ofCalculRegimeTvaEncours(String ordRef, String regimeTVA);

    FunctionResult fResaUneLigne(
            String arg_fou_code,
            String arg_prop_code,
            String arg_art_ref,
            String arg_username,
            Integer arg_qte_resa,
            String arg_ord_ref,
            String arg_orl_ref,
            String arg_desc,
            String arg_pal_code);

    FunctionResult fGetInfoResa(String orlRef);

    FunctionResult ofSauveOrdre(String ordRef);

    // FunctionResult fCalculQte(String argOrdRef, String argOrlRef, Float
    // argPdsBrut, Float argPdsNet, Integer argAchQte, Integer argVteQte);

    // Evenements de cellules sur les lignes d'ordres
    FunctionResult onChangeCdeNbPal(String orlRef, String scoCode);

    FunctionResult onChangeDemipalInd(String orlRef, String username);

    FunctionResult onChangePalNbCol(String orlRef, String username);

    FunctionResult onChangeCdeNbCol(String orlRef, String username);

    FunctionResult onChangeProprCode(String orlRef, String username, String socCode);

    FunctionResult onChangeFouCode(String orlRef, String username, String socCode);

    FunctionResult onChangeVtePu(String orlRef);

    FunctionResult onChangePalCode(String orlRef, String username, String scoCode);

    FunctionResult onChangePalinterCode(String orlRef);

    FunctionResult onChangeIndGratuit(String orlRef);

    FunctionResult onChangeAchDevPu(String orlRef, String socCode);

    FunctionResult onChangePalNbPalinter(String orlRef, String username);

    FunctionResult fDetailsExpOnClickAuto(String orlRef);

    FunctionResult fChgtQteArtRet(String ordRef);

    FunctionResult fDetailsExpClickModifier(String ordRef, String orlRef, String histoOrxRef);

    FunctionResult fConfirmationCommande(String ordRef, String socCode, String username);

    FunctionResult fDocumentEnvoiDetailsExp(String ordRef, String socCode);

    FunctionResult fDocumentEnvoiConfirmationPrixAchat(String ordRef);

    FunctionResult fDocumentEnvoiFichesPalette(String ordRef);

    FunctionResult fDocumentEnvoiGenereTraca(String ordRef);

    FunctionResult fDocumentEnvoiAfficheCMR(String ordRef);

    FunctionResult fDocumentEnvoiBonLivraison(String ordRef);

    FunctionResult fDocumentEnvoiProforma(String ordRef);

    FunctionResult fDocumentEnvoiCominv(String ordRef);

    FunctionResult fDocumentEnvoiShipmentBuyco(String ordRef);

    FunctionResult fDocumentEnvoiDeclarationBollore(String ordRef);

    FunctionResult fClotureLogGrp(String ordRef, String fouCode, Character expedie);

    FunctionResult fSetDetailKitArticle(String ordRef, String fouCode);

    FunctionResult fActualiseNbPalettesSol(String ordRef, String fouCode);

    FunctionResult fGetQttPerBta(String artRef, String btaCode, Double nbPal, Double nbCol, Double pdsNet);

    FunctionResult fSubmitEnvoiDetailSeccom(String ordRef, String fouCode, String logName);

    FunctionResult fDetailEnteteSauve(String ordRef, String mode, Character checkPalette);

    FunctionResult fTracabiliteCloturer(String ordRef, Character cloturer);

    FunctionResult fDetailsExpOnCheckCloturer(String orxRef, String devalexpRef, String username, String socCode);

    FunctionResult fBonAFacturerPrepare(String ordRef, String socCode);

    FunctionResult fBonAFacturer(String ordRef, String socCode);

    FunctionResult fSuppressionOrdre(String ordRef, String username, String commentaire);

    FunctionResult fTestAnnuleOrdre(String ordRef);

    FunctionResult fAnnulationOrdre(String ordRef, String motif);

    FunctionResult fCreeOrdreComplementaire(String ordRef, String socCode, String username);

    FunctionResult fCreeOrdreRegularisation(String ordRef, String socCode, String lcaCode, String typReg,
            Boolean indDetail, String username, String[] listOrlRef);

    FunctionResult fCreateOrdresEdi(String ediOrdre, String camCode, String socCode, String cliRef, String cenRef,
            String refCmd, String dateLiv, String username);

    FunctionResult wDupliqueOrdreOnDuplique(
            String arg_ord_ref,
            String arg_username,
            String arg_soc_code,
            String arg_cen_ref,
            LocalDateTime arg_depdatp,
            LocalDate arg_livdatp,
            Boolean arg_code_chargement,
            Boolean arg_etd_location,
            Boolean arg_eta_location,
            Boolean arg_etd_date,
            Boolean arg_eta_date,
            Boolean arg_inc_code,
            Boolean arg_fourni,
            Boolean arg_vte_pu,
            Boolean arg_ach_pu,
            Boolean arg_lib_dlv);

    FunctionResult ofInitRegimeTva(String ordreRef, String entrepotCode);
}
