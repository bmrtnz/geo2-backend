package fr.microtec.geo2.service.graphql.common;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoAlerte;
import fr.microtec.geo2.persistance.repository.common.GeoAlerteRepository;
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
public class GeoAlerteGraphQLService extends GeoAbstractGraphQLService<GeoAlerte, BigDecimal> {

    public GeoAlerteGraphQLService(GeoAlerteRepository repository) {
        super(repository, GeoAlerte.class);
    }

    @GraphQLQuery
    public RelayPage<GeoAlerte> allAlerte(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoAlerte> getAlerte(
            @GraphQLArgument(name = "id") BigDecimal id) {
        return this.getOne(id);
    }

    @GraphQLMutation
    public GeoAlerte saveAlerte(GeoAlerte alerte,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(alerte, env);
    }

    @GraphQLMutation
    public void deleteAlerte(BigDecimal id) {
        this.delete(id);
    }

    @GraphQLQuery
    public List<GeoAlerte> allAlerteList(
            @GraphQLArgument(name = "search") String search,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getUnpaged(search, env);
    }

}
