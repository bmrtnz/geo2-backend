package fr.microtec.geo2.service.graphql.stock;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleEdiBassin;
import fr.microtec.geo2.persistance.repository.stock.GeoStockArticleEdiBassinRepository;
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
public class GeoStockArticleEdiBassinGraphQLService
        extends GeoAbstractGraphQLService<GeoStockArticleEdiBassin, BigDecimal> {

    public GeoStockArticleEdiBassinGraphQLService(GeoStockArticleEdiBassinRepository repository) {
        super(repository, GeoStockArticleEdiBassin.class);
    }

    @GraphQLQuery
    public RelayPage<GeoStockArticleEdiBassin> allStockArticleEdiBassin(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoStockArticleEdiBassin> getStockArticleEdiBassin(
            @GraphQLArgument(name = "id") BigDecimal id) {
        return super.getOne(id);
    }

    @GraphQLMutation
    public GeoStockArticleEdiBassin saveStockArticleEdiBassin(GeoStockArticleEdiBassin stockArticleEdiBassin,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(stockArticleEdiBassin, env);
    }

    @GraphQLMutation
    public void deleteStockArticleEdiBassin(BigDecimal id) {
        this.delete(id);
    }

}
