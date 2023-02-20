package fr.microtec.geo2.service.graphql.litige;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.litige.GeoFunctionLitigeRepository;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@Secured("ROLE_USER")
@GraphQLApi
public class GeoFunctionsLitigeGraphQLService {

    private final GeoFunctionLitigeRepository repository;

    public GeoFunctionsLitigeGraphQLService(
            GeoFunctionLitigeRepository repository) {
        this.repository = repository;
    }

    @GraphQLQuery
    public FunctionResult ofClotureLitigeClient(
            String litigeRef,
            String societeCode,
            String promptFraisAnnexe,
            String promptAvoirClient,
            String promptCreateAvoirClient) {
        return this.repository.ofClotureLitigeClient(
                litigeRef,
                societeCode,
                promptFraisAnnexe,
                promptAvoirClient,
                promptCreateAvoirClient);
    }

    @GraphQLQuery
    public FunctionResult ofClotureLitigeResponsable(
            String litigeRef,
            String societeCode,
            String promptFraisAnnexe,
            String promptAvoirResponsable,
            String promptCreateAvoirResponsable) {
        return this.repository.ofClotureLitigeResponsable(
                litigeRef,
                societeCode,
                promptFraisAnnexe,
                promptAvoirResponsable,
                promptCreateAvoirResponsable);
    }

    @GraphQLQuery
    public FunctionResult ofClotureLitigeGlobale(
            String litigeRef,
            String societeCode,
            String promptFraisAnnexe,
            String promptAvoirClient,
            String promptAvoirGlobal,
            String promptCreateAvoirGlobal) {
        return this.repository.ofClotureLitigeGlobale(
                litigeRef,
                societeCode,
                promptFraisAnnexe,
                promptAvoirClient,
                promptAvoirGlobal,
                promptCreateAvoirGlobal);
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

}
