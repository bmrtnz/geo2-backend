package fr.microtec.geo2.service.graphql.stock;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.stock.GeoFunctionStockRepository;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@Secured("ROLE_USER")
@GraphQLApi
public class GeoFunctionsStockGraphQLService {

    private final GeoFunctionStockRepository repository;

    public GeoFunctionsStockGraphQLService(
            GeoFunctionStockRepository repository) {
        this.repository = repository;
    }

    @GraphQLQuery
    public FunctionResult refreshStockHebdo() {
        return this.repository.refreshStockHebdo();
    }

}
