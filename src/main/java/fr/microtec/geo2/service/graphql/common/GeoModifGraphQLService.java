package fr.microtec.geo2.service.graphql.common;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoModification;
import fr.microtec.geo2.persistance.repository.common.GeoModifRepository;
import fr.microtec.geo2.service.ModificationService;
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
public class GeoModifGraphQLService extends GeoAbstractGraphQLService<GeoModification, BigDecimal> {

    private final ModificationService modificationService;

    public GeoModifGraphQLService(
            GeoModifRepository repository,
            ModificationService modificationService) {
        super(repository, GeoModification.class);
        this.modificationService = modificationService;
    }

    @GraphQLQuery
    public RelayPage<GeoModification> allModification(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public List<GeoModification> listModification(
            @GraphQLArgument(name = "search") String search,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getUnpaged(search, env);
    }

    @GraphQLQuery
    public Optional<GeoModification> getModification(
            @GraphQLArgument(name = "id") BigDecimal id) {
        return super.getOne(id);
    }

    @GraphQLMutation
    public GeoModification saveModification(GeoModification modification,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(this.modificationService.prepare(modification), env);
    }

}
