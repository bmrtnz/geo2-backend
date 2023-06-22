package fr.microtec.geo2.service.graphql.tiers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import fr.microtec.geo2.persistance.repository.tiers.GeoTransporteurRepository;
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
public class GeoTransporteurGraphQLService extends GeoAbstractGraphQLService<GeoTransporteur, String> {

    public GeoTransporteurGraphQLService(GeoTransporteurRepository repository) {
        super(repository, GeoTransporteur.class);
    }

    @GraphQLQuery
    public RelayPage<GeoTransporteur> allTransporteur(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoTransporteur> getTransporteur(
            @GraphQLArgument(name = "id") String id) {
        return this.getOne(id);
    }

    @GraphQLMutation
    public GeoTransporteur saveTransporteur(GeoTransporteur transporteur,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(transporteur, env);
    }

    @GraphQLMutation
    public void deleteTransporteur(String id) {
        this.delete(id);
    }

    @GraphQLQuery
    public long countTransporteur(
            @GraphQLArgument(name = "search") String search) {
        return this.count(search);
    }

    @GraphQLQuery
    public List<GeoTransporteur> allTransporteurList(
            @GraphQLArgument(name = "search") String search,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getUnpaged(search, env);
    }

}
