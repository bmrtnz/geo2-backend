package fr.microtec.geo2.service.graphql.ordres;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLogistiqueRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.service.OrdreLigneService;
import fr.microtec.geo2.service.OrdreService;
import fr.microtec.geo2.service.security.SecurityService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@Secured("ROLE_USER")
@GraphQLApi
public class GeoFunctionsOrdreGraphQLService {

    private final GeoFunctionOrdreRepository repository;
    private final OrdreService ordreService;
    private final GeoOrdreRepository ordreRepo;
    private final OrdreLigneService ordreLigneService;
    private final GeoOrdreLogistiqueRepository logistiqueRepo;

    public GeoFunctionsOrdreGraphQLService(
            GeoFunctionOrdreRepository repository,
            OrdreLigneService ordreLigneService,
            GeoOrdreRepository ordreRepo,
            OrdreService ordreService,
            GeoOrdreLogistiqueRepository logistiqueRepo,
            SecurityService securityService) {
        this.repository = repository;
        this.ordreService = ordreService;
        this.ordreRepo = ordreRepo;
        this.ordreLigneService = ordreLigneService;
        this.logistiqueRepo = logistiqueRepo;
    }

    @GraphQLQuery
    public FunctionResult ofValideEntrepotForOrdre(
            @GraphQLArgument(name = "entrepotID") String entrepotID) {
        return this.repository.ofValideEntrepotForOrdre(entrepotID);
    }

    @GraphQLQuery
    public FunctionResult fNouvelOrdre(
            @GraphQLArgument(name = "societe") String socCode) {
        return this.repository.fNouvelOrdre(socCode);
    }

    @GraphQLQuery
    public List<GeoOrdreBaf> fAfficheBaf(
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "secteurCode") String secteurCode,
            @GraphQLArgument(name = "clientCode") String clientCode,
            @GraphQLArgument(name = "entrepotCode") String entrepotCode,
            @GraphQLArgument(name = "dateMin") LocalDate dateMin,
            @GraphQLArgument(name = "dateMax") LocalDate dateMax,
            @GraphQLArgument(name = "codeAssistante") String codeAssistante,
            @GraphQLArgument(name = "codeCommercial") String codeCommercial) {
        return this.ordreService.allDepartBaf(societeCode, secteurCode, clientCode, entrepotCode, dateMin, dateMax,
                codeAssistante, codeCommercial);
    }

    @GraphQLQuery
    public FunctionResult fControlBaf(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "societeCode") String societeCode) {
        return this.repository.fControlOrdreBaf(ordreRef, societeCode);
    }

    @GraphQLQuery
    public FunctionResult ofInitArticle(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "articleRef") String articleRef,
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "orlRefUpdate") String orlRefUpdate) {
        FunctionResult res = this.repository.ofInitArticle(ordreRef, articleRef, societeCode, orlRefUpdate);
        if (res.getRes() == FunctionResult.RESULT_OK) {
            String newligneRef = res.getData().get("new_orl_ref").toString();
            this.repository.onChangeAchDevPu(newligneRef, societeCode);
        }
        return res;
    }

    @GraphQLQuery
    public FunctionResult ofInitArticleHistory(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "articleRef") String articleRef,
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "fromLigneRef") String historyLigneRef) {
        FunctionResult res = this.repository.ofInitArticle(ordreRef, articleRef, societeCode, null);

        String newligneRef = res.getData().get("new_orl_ref").toString();

        // Update generated row with history values
        if (res.getRes() == FunctionResult.RESULT_OK) {
            this.ordreLigneService.updateFromHistory(newligneRef, historyLigneRef);

            // set bassin
            this.repository.setTransporteurBassin(newligneRef);

            // Manually generate logistique
            this.repository.fVerifLogistiqueOrdre(ordreRef);

            // Update `ach_dev_pu`
            this.repository.onChangeAchDevPu(newligneRef, societeCode);
        }

        return res;

    }

    @GraphQLQuery
    public FunctionResult fInitBlocageOrdre(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "userName") String userName) {
        return this.repository.fInitBlocageOrdre(ordreRef, userName);
    }

    @GraphQLQuery
    public FunctionResult fCalculMargePrevi(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "socCode") String socCode) {
        return this.repository.fCalculMargePrevi(ordreRef, socCode);
    }

    @GraphQLQuery
    public FunctionResult fCalculMarge(
            @GraphQLArgument(name = "ordreRef") String ordreRef) {
        return this.repository.fCalculMarge(ordreRef);
    }

    @GraphQLQuery
    public FunctionResult fVerifLogistiqueOrdre(
            @GraphQLArgument(name = "ordreRef") String ordreRef) {
        return this.repository.fVerifLogistiqueOrdre(ordreRef);
    }

    @GraphQLQuery
    public FunctionResult fAjoutOrdlog(
            @GraphQLArgument(name = "ordreLogRef") String ordreLogRef,
            @GraphQLArgument(name = "typePassage") String typePassage,
            @GraphQLArgument(name = "choixPassage") String choixPassage) {
        return this.repository.fAjoutOrdlog(ordreLogRef, typePassage, choixPassage);
    }

    @GraphQLQuery
    public FunctionResult fResaUneLigne(
            @GraphQLArgument(name = "fouCode") String arg_fou_code,
            @GraphQLArgument(name = "propCode") String arg_prop_code,
            @GraphQLArgument(name = "artRef") String arg_art_ref,
            @GraphQLArgument(name = "username") String arg_username,
            @GraphQLArgument(name = "qteResa") Integer arg_qte_resa,
            @GraphQLArgument(name = "ordRef") String arg_ord_ref,
            @GraphQLArgument(name = "orlRef") String arg_orl_ref,
            @GraphQLArgument(name = "desc") String arg_desc,
            @GraphQLArgument(name = "palCode") String arg_pal_code) {
        return this.repository.fResaUneLigne(arg_fou_code, arg_prop_code, arg_art_ref, arg_username, arg_qte_resa,
                arg_ord_ref, arg_orl_ref, arg_desc, arg_pal_code);
    }

    @GraphQLQuery
    public FunctionResult fResaAutoOrdre(
            @GraphQLArgument(name = "ordRef") String arg_ord_ref,
            @GraphQLArgument(name = "username") String arg_username) {
        return this.repository.fResaAutoOrdre(arg_ord_ref, arg_username);
    }

    @GraphQLQuery
    public FunctionResult geoPrepareEnvois(
            @GraphQLArgument(name = "ordRef") String ordRef,
            @GraphQLArgument(name = "fluCode") String fluCode,
            @GraphQLArgument(name = "modeAuto") Boolean modeAuto,
            @GraphQLArgument(name = "annOrdre") Boolean annOrdre,
            @GraphQLArgument(name = "user") String user) {
        FunctionResult res = this.repository.geoPrepareEnvois(ordRef, fluCode, modeAuto ? 'O' : 'N',
                annOrdre ? 'O' : 'N', user);

        if (res.getCursorData() != null) {
            List<Object> contacts = res.getCursorData();
            res.setData(Map.of("contacts", contacts));
        }

        return res;
    }

    @GraphQLQuery
    public FunctionResult fGetInfoResa(@GraphQLArgument(name = "orlRef") String orlRef) {
        return this.repository.fGetInfoResa(orlRef);
    }

    @GraphQLQuery
    public FunctionResult ofAREnvois(@GraphQLArgument(name = "ordRef") String ordRef) {
        return this.repository.ofAREnvois(ordRef);
    }

    @GraphQLQuery
    public FunctionResult onChangeAchDevPu(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "societeCode") String socCode) {
        return this.repository.onChangeAchDevPu(orlRef, socCode);
    }

    @GraphQLQuery
    public FunctionResult fDetailsExpOnClickAuto(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef) {
        return this.repository.fDetailsExpOnClickAuto(orlRef);
    }

    @GraphQLQuery
    public FunctionResult fChgtQteArtRet(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fChgtQteArtRet(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDetailsExpClickModifier(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "historiqueRef") String histoOrxRef) {
        return this.repository.fDetailsExpClickModifier(ordRef, orlRef, histoOrxRef);
    }

    @GraphQLQuery
    public FunctionResult fConfirmationCommande(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "societeCode") String socCode,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.fConfirmationCommande(ordRef, socCode, username);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiDetailsExp(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "societeCode") String socCode) {
        return this.repository.fDocumentEnvoiDetailsExp(ordRef, socCode);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiConfirmationPrixAchat(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiConfirmationPrixAchat(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiFichesPalette(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiFichesPalette(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiGenereTraca(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiGenereTraca(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiAfficheCMR(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiAfficheCMR(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiBonLivraison(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiBonLivraison(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiCominv(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiCominv(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiShipmentBuyco(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiShipmentBuyco(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDocumentEnvoiFactureDouaniere(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiFactureDouaniere(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fDetailsExpOnCheckCloturer(
            @GraphQLArgument(name = "logistiqueRef") String orxRef,
            @GraphQLArgument(name = "devalexpRef") String devalexpRef,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "societeCode") String socCode) {
        return this.repository.fDetailsExpOnCheckCloturer(orxRef, devalexpRef, username, socCode);
    }

    @GraphQLQuery
    public FunctionResult fBonAFacturerPrepare(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "socCode") String socCode) {
        return this.repository.fBonAFacturerPrepare(ordRef, socCode);
    }

    @GraphQLQuery
    public FunctionResult fBonAFacturer(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "socCode") String socCode) {
        return this.repository.fBonAFacturer(ordRef, socCode);
    }

    @GraphQLQuery
    public FunctionResult fSuppressionOrdre(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "commentaire") String commentaire) {
        return this.repository.fSuppressionOrdre(ordRef, username, commentaire);
    }

    @GraphQLQuery
    public FunctionResult fTestAnnuleOrdre(@GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fTestAnnuleOrdre(ordRef);
    }

    @GraphQLQuery
    public FunctionResult fAnnulationOrdre(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "motif") String motif) {
        return this.repository.fAnnulationOrdre(ordRef, motif);
    }

    @GraphQLQuery
    public FunctionResult fCreeOrdreComplementaire(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "socCode") String socCode,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.fCreeOrdreComplementaire(ordRef, socCode, username);
    }

    @GraphQLQuery
    public FunctionResult fCreeOrdreRegularisation(
            @GraphQLArgument(name = "ordreRef") String ordRef,
            @GraphQLArgument(name = "socCode") String socCode,
            @GraphQLArgument(name = "lcaCode") String lcaCode,
            @GraphQLArgument(name = "typeReg") String typeReg,
            @GraphQLArgument(name = "indDetail") Boolean indDetail,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "listOrlRef") String[] listOrlRef) {
        return this.repository.fCreeOrdreRegularisation(ordRef, socCode, lcaCode, typeReg, indDetail, username,
                listOrlRef);
    }

    @GraphQLQuery
    public FunctionResult fCreeOrdresEdi(
            @GraphQLArgument(name = "ediOrdreId") String ediOrdreId,
            @GraphQLArgument(name = "campagneId") String campagneId,
            @GraphQLArgument(name = "societeId") String societeId,
            @GraphQLArgument(name = "referenceClient") String referenceClient,
            @GraphQLArgument(name = "entrepotId") String entrepotId,
            @GraphQLArgument(name = "referenceCommande") String referenceCommande,
            @GraphQLArgument(name = "dateLivraison") String dateLivraison,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.fCreateOrdresEdi(ediOrdreId, campagneId, societeId, referenceClient, entrepotId,
                referenceCommande, dateLivraison, username);
    }

    @GraphQLQuery
    public FunctionResult ofSauveOrdre(
            @GraphQLArgument(name = "ordRef") String ordRef) {
        return this.repository.ofSauveOrdre(ordRef);
    }

    @GraphQLQuery
    public FunctionResult wDupliqueOrdreOnDuplique(
            @GraphQLArgument(name = "ordRef") String ordRef,
            @GraphQLArgument(name = "user") String user,
            @GraphQLArgument(name = "socCode") String socCode,
            @GraphQLArgument(name = "cenRef") String cenRef,
            @GraphQLArgument(name = "depDate") LocalDateTime depDate,
            @GraphQLArgument(name = "livDate") LocalDate livDate,
            @GraphQLArgument(name = "withCodeChargement") Boolean withCodeChargement,
            @GraphQLArgument(name = "withEtdLocation") Boolean withEtdLocation,
            @GraphQLArgument(name = "withEtaLocation") Boolean withEtaLocation,
            @GraphQLArgument(name = "withEtdDate") Boolean withEtdDate,
            @GraphQLArgument(name = "withEtaDate") Boolean withEtaDate,
            @GraphQLArgument(name = "withIncCode") Boolean withIncCode,
            @GraphQLArgument(name = "withFourni") Boolean withFourni,
            @GraphQLArgument(name = "withVtePu") Boolean withVtePu,
            @GraphQLArgument(name = "withAchPu") Boolean withAchPu,
            @GraphQLArgument(name = "withLibDlv") Boolean withLibDlv) {
        return this.repository.wDupliqueOrdreOnDuplique(
                ordRef,
                user,
                socCode,
                cenRef,
                depDate,
                livDate,
                withCodeChargement,
                withEtdLocation,
                withEtaLocation,
                withEtdDate,
                withEtaDate,
                withIncCode,
                withFourni,
                withVtePu,
                withAchPu,
                withLibDlv);
    }

    @GraphQLQuery
    public FunctionResult ofInitRegimeTva(
            @GraphQLArgument(name = "ordRef") String ordRef,
            @GraphQLArgument(name = "tvrCode") String tvrCode) {
        return this.repository.ofInitRegimeTva(ordRef, tvrCode);
    }

    @GraphQLQuery
    public FunctionResult fDecomptePalox(
            Long nbPallox,
            String fouCode,
            String colCode,
            String cenRef,
            String espCode,
            LocalDate dateApplication,
            String socCode) {
        return this.repository.fDecomptePalox(nbPallox, fouCode, colCode, cenRef, espCode, dateApplication, socCode);
    }

    @GraphQLQuery
    public FunctionResult fAjustPalox(
            String socCode,
            String cenCode,
            String fouCode,
            String colCode,
            String espCode,
            Integer nbPallox,
            LocalDate dateApplication,
            String commentaire,
            String cliCode) {
        return this.repository.fAjustPallox(
                socCode,
                cenCode,
                fouCode,
                colCode,
                espCode,
                nbPallox,
                dateApplication,
                commentaire,
                cliCode);
    }

    @GraphQLQuery
    public FunctionResult fEnvoiBLAuto(
            String socCode,
            String scoCode,
            LocalDate dateMin,
            LocalDate dateMax,
            String nomUtilisateur) {
        return this.repository.fEnvoiBLAuto(
                socCode,
                scoCode,
                dateMin,
                dateMax,
                nomUtilisateur);
    }

    @GraphQLQuery
    public FunctionResult fnMajOrdreRegroupementV2(
            String ordreRef,
            String socCode,
            Boolean entrepotGeneric,
            String nomUtilisateur) {
        return this.repository
                .fnMajOrdreRegroupementV2(ordreRef, socCode, entrepotGeneric, nomUtilisateur);
    }

    @GraphQLQuery
    public FunctionResult fDuplicationBukSa(
            String ordreRef,
            String socCode,
            String nomUtilisateur,
            String codeRegimeTva) {
        return this.repository.fDuplicationBukSa(
                ordreRef,
                socCode,
                nomUtilisateur,
                codeRegimeTva);
    }

    @GraphQLQuery
    public FunctionResult fDelRegroupement(String ordreRef) {
        return this.repository.fDelRegroupement(ordreRef);
    }

    @GraphQLQuery
    public FunctionResult fCreeOrdreReplacement(
            String ordreOriginID,
            String entrepotID,
            String nomUtilisateur,
            String societeID) {
        return this.repository.fCreeOrdreReplacement(
                ordreOriginID,
                entrepotID,
                nomUtilisateur,
                societeID);
    }

    @GraphQLQuery
    public FunctionResult fCreeOrdreReplacementLigne(
            String litigeLigneID,
            String ordreID,
            String ordreOriginID,
            String ordreLigneOriginID,
            String societeID) {
        return this.repository.fCreeOrdreReplacementLigne(
                litigeLigneID,
                ordreID,
                ordreOriginID,
                ordreLigneOriginID,
                societeID);
    }

    @GraphQLQuery
    public FunctionResult fCreeOrdreReedFact(
            String ordreOriginID,
            String societeID,
            String nomUtilisateur) {
        return this.repository.fCreeOrdreReedFact(
                ordreOriginID,
                societeID,
                nomUtilisateur);
    }

    @GraphQLQuery
    public FunctionResult fCreeOrdreReedFactLigne(
            String ordreID,
            String ordreOriginID,
            String societeID) {
        return this.repository.fCreeOrdreReedFactLigne(
                ordreID,
                ordreOriginID,
                societeID);
    }

    @GraphQLQuery
    public FunctionResult fReturnForfaitsTrp(String cenRef,
            String incCode,
            String typeOrd) {
        return this.repository.fReturnForfaitsTrp(cenRef, incCode, typeOrd);
    }

    @GraphQLQuery
    public FunctionResult prcGenFraisDedimp(String ordRef) {
        return this.repository.prcGenFraisDedimp(ordRef);
    }

    @GraphQLQuery
    public FunctionResult onChangeTrpDevCode(
            String ordreID,
            String transporteurDevCode,
            String societeID,
            Float transporteurPU) {
        return this.repository.onChangeTrpDevCode(
                ordreID,
                transporteurDevCode,
                societeID,
                transporteurPU);
    }

    @GraphQLQuery
    public FunctionResult wAjoutArtRecapEdiColibri(
            String articleID,
            String fournisseurCode,
            String proprietaireCode,
            Integer quantiteValide,
            BigDecimal fromID) {
        return this.repository.wAjoutArtRecapEdiColibri(
                articleID,
                fournisseurCode,
                proprietaireCode,
                quantiteValide,
                fromID);
    }

    @GraphQLQuery
    public FunctionResult ofReadOrdEdiColibri(
            BigDecimal numeroCommandeEDI,
            String codeCampagne,
            Character typeStock) {
        return this.repository.ofReadOrdEdiColibri(
                numeroCommandeEDI,
                codeCampagne,
                typeStock);
    }

    @GraphQLQuery
    public FunctionResult ofControleSelArt(
            BigDecimal numeroCommandeEDI,
            String codeCampagne) {
        return this.repository.ofControleSelArt(
                numeroCommandeEDI,
                codeCampagne);
    }

    @GraphQLQuery
    public FunctionResult ofControleQteArt(
            BigDecimal numeroCommandeEDI,
            String codeCampagne) {
        return this.repository.ofControleQteArt(
                numeroCommandeEDI,
                codeCampagne);
    }

    @GraphQLQuery
    public FunctionResult clearTraca(String ordreLigneRef) {
        return this.repository.clearTraca(ordreLigneRef);
    }

    @GraphQLQuery
    public FunctionResult fRecupFrais(
            String varCode,
            String catCode,
            String scoCode,
            String tvtCode,
            Integer modeCulture,
            String origine) {
        return this.repository.fRecupFrais(varCode, catCode, scoCode, tvtCode, modeCulture, origine);
    }

    @GraphQLQuery
    public FunctionResult setTransporteurBassin(String orlRef) {
        return this.repository.setTransporteurBassin(orlRef);
    }

    @GraphQLQuery
    public FunctionResult fCreateEdiEsp(
            BigDecimal ediOrdreRef,
            String socCode,
            String cliRef,
            String cenRef,
            String username) {
        return this.repository.fCreateEdiEsp(ediOrdreRef, socCode, cliRef, cenRef, username);
    }

    @GraphQLQuery
    public FunctionResult clotureSP(@GraphQLArgument(name = "ordresRef") List<String> ordresRef) {
        AtomicReference<FunctionResult> res = new AtomicReference<FunctionResult>(
                new FunctionResult(FunctionResult.RESULT_OK, "Rien à traiter", null, null));

        ordresRef.forEach(ordreRef -> {
            this.ordreRepo.findById(ordreRef).ifPresent(ordre -> {
                ordre.getLogistiques().forEach(logistique -> {
                    logistique.getLignes().forEach(ligne -> {
                        res.set(this.repository.fDetailsExpOnClickAuto(ligne.getId()));
                        if (!res.get().getRes().equals(FunctionResult.RESULT_OK))
                            return;
                    });
                    logistique.setExpedieStation(true);
                    this.logistiqueRepo.save(logistique);
                });
            });
        });

        return res.get();

    }

    @GraphQLQuery
    public FunctionResult supprLignesNonExped(@GraphQLArgument(name = "ordreRef") String ordreRef) {
        return this.repository.supprLignesNonExped(ordreRef);
    }

}
