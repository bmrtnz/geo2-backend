package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.service.OrdreService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Secured("ROLE_USER")
@GraphQLApi
public class GeoFunctionsOrdreGraphQLService {

    private final GeoFunctionOrdreRepository repository;
    private final OrdreService ordreService;

    public GeoFunctionsOrdreGraphQLService(
            GeoFunctionOrdreRepository repository,
            OrdreService ordreService) {
        this.repository = repository;
        this.ordreService = ordreService;
    }

    @GraphQLQuery
    public FunctionResult ofValideEntrepotForOrdre(
            @GraphQLArgument(name = "entrepotID") String entrepotID
    ) {
        return this.repository.ofValideEntrepotForOrdre(entrepotID);
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
            @GraphQLArgument(name = "codeCommercial") String codeCommercial
    ) {
        return this.ordreService.allDepartBaf(societeCode, secteurCode, clientCode, entrepotCode, dateMin, dateMax, codeAssistante, codeCommercial);
    }

}