package fr.microtec.geo2.service.graphql.ordres;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoAttribFrais;
import fr.microtec.geo2.persistance.repository.ordres.GeoAttribFraisRepository;
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
public class GeoAttribFraisGraphQLService extends GeoAbstractGraphQLService<GeoAttribFrais, BigDecimal> {

    public GeoAttribFraisGraphQLService(GeoAttribFraisRepository repository) {
        super(repository, GeoAttribFrais.class);
    }

    @GraphQLQuery
    public RelayPage<GeoAttribFrais> allAttribFrais(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoAttribFrais> getAttribFrais(
            @GraphQLArgument(name = "id") BigDecimal id) {
        return super.getOne(id);
    }

}
