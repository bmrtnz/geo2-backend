package fr.microtec.geo2.service.graphql.stock;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoStockMouvement;
import fr.microtec.geo2.persistance.repository.stock.GeoStockMouvementRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoStockMouvementGraphQLService extends GeoAbstractGraphQLService<GeoStockMouvement, String> {

    public GeoStockMouvementGraphQLService(GeoStockMouvementRepository stockMouvementRepository) {
        super(stockMouvementRepository, GeoStockMouvement.class);
    }

    @GraphQLQuery
    public RelayPage<GeoStockMouvement> allStockMouvement(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoStockMouvement> getStockMouvement(
            @GraphQLArgument(name = "id") String id) {
        return this.getOne(id);
    }

    @GraphQLMutation
    public GeoStockMouvement saveStockMouvement(GeoStockMouvement stockMouvement,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(stockMouvement, env);
    }

    @GraphQLMutation
    public void deleteStockMouvement(String id) {
        this.delete(id);
    }

    @GraphQLMutation
    public void deleteAllByOrdreLigneId(String id) {
        ((GeoStockMouvementRepository) this.repository).deleteAllByOrdreLigneId(id);
    }

}
