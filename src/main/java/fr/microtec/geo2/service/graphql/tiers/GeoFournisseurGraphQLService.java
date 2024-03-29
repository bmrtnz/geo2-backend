package fr.microtec.geo2.service.graphql.tiers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.repository.tiers.GeoFournisseurRepository;
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
public class GeoFournisseurGraphQLService extends GeoAbstractGraphQLService<GeoFournisseur, String> {

    public GeoFournisseurGraphQLService(GeoFournisseurRepository repository) {
        super(repository, GeoFournisseur.class);
    }

    @GraphQLQuery
    public RelayPage<GeoFournisseur> allFournisseur(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public List<GeoFournisseur> allFournisseurList(
            @GraphQLArgument(name = "search") String search,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getUnpaged(search, env);
    }

    @GraphQLQuery
    public Optional<GeoFournisseur> getFournisseur(
            @GraphQLArgument(name = "id") String id) {
        return this.getOne(id);
    }

    @GraphQLMutation
    public GeoFournisseur saveFournisseur(GeoFournisseur fournisseur, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(fournisseur, env);
    }

    @GraphQLQuery
    public long countFournisseur(
            @GraphQLArgument(name = "search") String search) {
        return this.count(search);
    }

    @GraphQLQuery
    public Optional<GeoFournisseur> getFournisseurByCode(
            @GraphQLArgument(name = "code") String code) {
        return ((GeoFournisseurRepository) this.repository).getOneByCode(code);
    }

}
