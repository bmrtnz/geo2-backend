package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

@Service
@Secured("ROLE_USER")
@GraphQLApi
public class GeoFunctionsOrdreGraphQLService {

    private final GeoFunctionOrdreRepository repository;

    public GeoFunctionsOrdreGraphQLService(
            GeoFunctionOrdreRepository repository
    ) {
        this.repository = repository;
    }

    @GraphQLQuery
    public FunctionResult ofValideEntrepotForOrdre(
            @GraphQLArgument(name = "entrepotID") String entrepotID
    ) {
        return this.repository.ofValideEntrepotForOrdre(entrepotID);
    }

}
