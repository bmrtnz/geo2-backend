package fr.microtec.geo2.service.graphql.ordres;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
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
    private final OrdreLigneService ordreLigneService;
    private final SecurityService securityService;

    public GeoFunctionsOrdreGraphQLService(
            GeoFunctionOrdreRepository repository,
            OrdreLigneService ordreLigneService,
            OrdreService ordreService,
            SecurityService securityService) {
        this.repository = repository;
        this.ordreService = ordreService;
        this.ordreLigneService = ordreLigneService;
        this.securityService = securityService;
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
    public FunctionResult ofInitArticle(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "articleRef") String articleRef,
            @GraphQLArgument(name = "societeCode") String societeCode) {
        return this.repository.ofInitArticle(ordreRef, articleRef, societeCode);
    }

    @GraphQLQuery
    public FunctionResult ofInitArticleHistory(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "articleRef") String articleRef,
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "fromLigneRef") String historyLigneRef) {
        FunctionResult res = this.repository.ofInitArticle(ordreRef, articleRef, societeCode);

        String newligneRef = res.getData().get("new_orl_ref").toString();

        // Update generated row with history values
        if (res.getRes() == 1) {
            this.ordreLigneService.updateFromHistory(newligneRef, historyLigneRef);

            // Manually generate logistique
            this.repository.fVerifLogistiqueOrdre(ordreRef);
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
    public FunctionResult geoPrepareEnvois(
            @GraphQLArgument(name = "ordRef") String ordRef,
            @GraphQLArgument(name = "fluCode") String fluCode,
            @GraphQLArgument(name = "modeAuto") Boolean modeAuto,
            @GraphQLArgument(name = "annOrdre") Boolean annOrdre,
            @GraphQLArgument(name = "user") String user) {
        FunctionResult res = this.repository.geoPrepareEnvois(ordRef, fluCode, modeAuto ? 'O' : 'N',
                annOrdre ? 'O' : 'N', user);
        List<Object> contacts = res.getCursorData();
        res.setData(Map.of("contacts", contacts));
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
    public FunctionResult onChangeCdeNbPal(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "secteurCommercialCode") String scoCode) {
        return this.repository.onChangeCdeNbPal(orlRef, scoCode);
    }

    @GraphQLQuery
    public FunctionResult onChangeDemipalInd(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.onChangeDemipalInd(orlRef, username);
    }

    @GraphQLQuery
    public FunctionResult onChangePalNbCol(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.onChangePalNbCol(orlRef, username);
    }

    @GraphQLQuery
    public FunctionResult onChangeCdeNbCol(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.onChangeCdeNbCol(orlRef, username);
    }

    @GraphQLQuery
    public FunctionResult onChangeProprCode(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "societeCode") String socCode) {
        return this.repository.onChangeProprCode(orlRef, username, socCode);
    }

    @GraphQLQuery
    public FunctionResult onChangeFouCode(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "societeCode") String socCode) {
        return this.repository.onChangeFouCode(orlRef, username, socCode);
    }

    @GraphQLQuery
    public FunctionResult onChangeVtePu(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef) {
        return this.repository.onChangeVtePu(orlRef);
    }

    @GraphQLQuery
    public FunctionResult onChangePalCode(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "secteurCode") String scoCode) {
        return this.repository.onChangePalCode(orlRef, username, scoCode);
    }

    @GraphQLQuery
    public FunctionResult onChangePalinterCode(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef) {
        return this.repository.onChangePalinterCode(orlRef);
    }

    @GraphQLQuery
    public FunctionResult onChangeIndGratuit(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef) {
        return this.repository.onChangeIndGratuit(orlRef);
    }

    @GraphQLQuery
    public FunctionResult onChangeAchDevPu(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "societeCode") String socCode) {
        return this.repository.onChangeAchDevPu(orlRef, socCode);
    }

    @GraphQLQuery
    public FunctionResult onChangePalNbPalinter(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.onChangePalNbPalinter(orlRef, username);
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
    public FunctionResult fDocumentEnvoiDeclarationBollore(
            @GraphQLArgument(name = "ordreRef") String ordRef) {
        return this.repository.fDocumentEnvoiDeclarationBollore(ordRef);
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

}
