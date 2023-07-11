package fr.microtec.geo2.service.graphql.produits;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoEdiArticleClient;
import fr.microtec.geo2.persistance.repository.produits.GeoEdiArticleClientRepository;
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
public class GeoEdiArticleClientGraphQLService extends GeoAbstractGraphQLService<GeoEdiArticleClient, Integer> {

    public GeoEdiArticleClientGraphQLService(GeoEdiArticleClientRepository repository) {
        super(repository, GeoEdiArticleClient.class);
    }

    @GraphQLQuery
    public RelayPage<GeoEdiArticleClient> allEdiArticleClient(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoEdiArticleClient> getEdiArticleClient(
            @GraphQLArgument(name = "id") Integer id) {
        return super.getOne(id);
    }

}
