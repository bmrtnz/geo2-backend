package fr.microtec.geo2.service.graphql.tiers;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepotTransporteurBassin;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotTransporteurBassinRepository;
import fr.microtec.geo2.service.EntrepotTransporteurBassinService;
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
public class GeoEntrepotTransporteurBassinGraphQLService
        extends GeoAbstractGraphQLService<GeoEntrepotTransporteurBassin, BigDecimal> {

    private final EntrepotTransporteurBassinService etbService;

    public GeoEntrepotTransporteurBassinGraphQLService(
            GeoEntrepotTransporteurBassinRepository repository,
            EntrepotTransporteurBassinService etbService) {
        super(repository, GeoEntrepotTransporteurBassin.class);
        this.etbService = etbService;
    }

    @GraphQLQuery
    public RelayPage<GeoEntrepotTransporteurBassin> allEntrepotTransporteurBassin(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoEntrepotTransporteurBassin> getEntrepotTransporteurBassin(
            @GraphQLArgument(name = "id") BigDecimal id) {
        return this.getOne(id);
    }

    @GraphQLMutation
    public GeoEntrepotTransporteurBassin saveEntrepotTransporteurBassin(
            GeoEntrepotTransporteurBassin entrepotTransporteurBassin,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(this.etbService.affecte(entrepotTransporteurBassin), env);
    }

}
