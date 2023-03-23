package fr.microtec.geo2.service.graphql.litige;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.litige.GeoFunctionLitigeRepository;
import fr.microtec.geo2.service.security.SecurityService;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@Secured("ROLE_USER")
@GraphQLApi
public class GeoFunctionsLitigeGraphQLService {

    private final GeoFunctionLitigeRepository repository;
    private final SecurityService securityService;

    public GeoFunctionsLitigeGraphQLService(
            GeoFunctionLitigeRepository repository,
            SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    @GraphQLQuery
    public FunctionResult ofClotureLitigeClient(
            String litigeRef,
            String societeCode,
            Boolean promptFraisAnnexe,
            Boolean promptAvoirClient,
            Boolean promptCreateAvoirClient) {
        return this.repository.ofClotureLitigeClient(
                litigeRef,
                societeCode,
                this.securityService.getUser().getNomUtilisateur(),
                promptFraisAnnexe != null ? (promptFraisAnnexe ? "O" : "N") : "",
                promptAvoirClient != null ? (promptAvoirClient ? "O" : "N") : "",
                promptCreateAvoirClient != null ? (promptCreateAvoirClient ? "O" : "N") : "");
    }

    @GraphQLQuery
    public FunctionResult ofClotureLitigeResponsable(
            String litigeRef,
            String societeCode,
            Boolean promptFraisAnnexe,
            Boolean promptAvoirResponsable,
            Boolean promptCreateAvoirResponsable) {
        return this.repository.ofClotureLitigeResponsable(
                litigeRef,
                societeCode,
                this.securityService.getUser().getNomUtilisateur(),
                promptFraisAnnexe != null ? (promptFraisAnnexe ? "O" : "N") : "",
                promptAvoirResponsable != null ? (promptAvoirResponsable ? "O" : "N") : "",
                promptCreateAvoirResponsable != null ? (promptCreateAvoirResponsable ? "O" : "N") : "");
    }

    @GraphQLQuery
    public FunctionResult ofClotureLitigeGlobale(
            String litigeRef,
            String societeCode,
            Boolean promptFraisAnnexe,
            Boolean promptAvoirClient,
            Boolean promptAvoirGlobal,
            Boolean promptCreateAvoirGlobal) {
        return this.repository.ofClotureLitigeGlobale(
                litigeRef,
                societeCode,
                this.securityService.getUser().getNomUtilisateur(),
                promptFraisAnnexe != null ? (promptFraisAnnexe ? "O" : "N") : "",
                promptAvoirClient != null ? (promptAvoirClient ? "O" : "N") : "",
                promptAvoirGlobal != null ? (promptAvoirGlobal ? "O" : "N") : "",
                promptCreateAvoirGlobal != null ? (promptCreateAvoirGlobal ? "O" : "N") : "");
    }

    @GraphQLQuery
    public FunctionResult ofSauveLitige(String litigeRef) {
        return this.repository.ofSauveLitige(litigeRef);
    }

    @GraphQLQuery
    public FunctionResult ofChronoLitige(String ordreOrigineRef) {
        return this.repository.ofChronoLitige(ordreOrigineRef);
    }

    @GraphQLQuery
    public FunctionResult ofLitigeCtlClientInsert(
            String societeCode,
            String ordreRef,
            String litigeRef) {
        return this.repository.ofLitigeCtlClientInsert(
                societeCode,
                ordreRef,
                litigeRef);
    }

    @GraphQLQuery
    public FunctionResult ofInitLigneLitige(
            String ordreLigneList,
            String litigeID,
            String numeroLot) {
        return this.repository.ofInitLigneLitige(
                ordreLigneList,
                litigeID,
                numeroLot);
    }

    @GraphQLQuery
    public FunctionResult fCreeOrdreRefacturationTransporteur(
            String ordRefOrigine,
            Float montIndemn,
            String socCode,
            String username) {
        return this.repository.fCreeOrdreRefacturationTransporteur(
                ordRefOrigine,
                montIndemn,
                socCode,
                username);
    }

}
