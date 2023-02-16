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
            String promptClotureClient,
            String promptCreateAvoirClient) {
        return this.repository.ofClotureLitigeClient(
                litigeRef,
                societeCode,
                promptFraisAnnexe,
                promptClotureClient,
                promptCreateAvoirClient);
    }

    @GraphQLQuery
    public FunctionResult ofClotureLitigeResponsable(
            String litigeRef,
            String societeCode,
            String promptFraisAnnexe,
            String promptClotureResponsable,
            String promptCreateAvoirResponsable) {
        return this.repository.ofClotureLitigeResponsable(
                litigeRef,
                societeCode,
                promptFraisAnnexe,
                promptClotureResponsable,
                promptCreateAvoirResponsable);
    }

}
