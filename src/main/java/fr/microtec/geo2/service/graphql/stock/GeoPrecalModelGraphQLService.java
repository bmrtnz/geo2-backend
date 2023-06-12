package fr.microtec.geo2.service.graphql.stock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoPrecalModel;
import fr.microtec.geo2.persistance.entity.stock.GeoPrecalModelVariete;
import fr.microtec.geo2.persistance.repository.stock.GeoPrecalModelRepository;
import fr.microtec.geo2.service.StockService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoPrecalModelGraphQLService extends GeoAbstractGraphQLService<GeoPrecalModel, String> {

    public GeoPrecalModelGraphQLService(
            GeoPrecalModelRepository stockRepository,
            StockService stockService) {
        super(stockRepository, GeoPrecalModel.class);
    }

    @GraphQLQuery
    public RelayPage<GeoPrecalModel> allPrecalModel(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoPrecalModel> getPrecalModel(
            @GraphQLArgument(name = "id") String id) {
        return this.getOne(id);
    }

    @GraphQLQuery
    public List<String> allPrecaEspece() {
        return ((GeoPrecalModelRepository) this.repository).allPrecaEspece();
    }

    @GraphQLQuery
    public List<GeoPrecalModelVariete> allPrecaVariete(String espece) {
        return ((GeoPrecalModelRepository) this.repository).allPrecaVariete(espece);
    }

}
