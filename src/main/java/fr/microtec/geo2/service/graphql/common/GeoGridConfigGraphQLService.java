package fr.microtec.geo2.service.graphql.common;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoGridConfig;
import fr.microtec.geo2.persistance.entity.common.GeoGridConfigKey;
import fr.microtec.geo2.persistance.repository.common.GeoGridConfigRepository;
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
public class GeoGridConfigGraphQLService extends GeoAbstractGraphQLService<GeoGridConfig, GeoGridConfigKey> {

    public GeoGridConfigGraphQLService(GeoGridConfigRepository repository) {
        super(repository, GeoGridConfig.class);
    }

    @GraphQLQuery
    public RelayPage<GeoGridConfig> allGridConfig(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoGridConfig> getGridConfig(
            @GraphQLArgument(name = "id") GeoGridConfigKey id) {
        return super.getOne(id);
    }

    @GraphQLQuery
    public Optional<GeoGridConfig> getGridConfigBySociete(
            @GraphQLArgument(name = "grid") String grid,
            @GraphQLArgument(name = "utilisateur") String nomUtilisateur,
            @GraphQLArgument(name = "societe") String societe) {
        return ((GeoGridConfigRepository) super.repository)
                .getOneByGridAndUtilisateurNomUtilisateurAndSocieteId(grid, nomUtilisateur, societe);
    }

    @GraphQLMutation
    public GeoGridConfig saveGridConfig(GeoGridConfig gridConfig, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(gridConfig, env);
    }

    @GraphQLMutation
    public void deleteGridConfig(GeoGridConfigKey id) {
        this.delete(id);
    }
}
