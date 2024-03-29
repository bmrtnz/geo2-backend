package fr.microtec.geo2.service.graphql.produits;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoCalibreFournisseur;
import fr.microtec.geo2.persistance.repository.produits.GeoCalibreFournisseurRepository;
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
public class GeoCalibreFournisseurGraphQLService extends GeoAbstractGraphQLService<GeoCalibreFournisseur, String> {

    public GeoCalibreFournisseurGraphQLService(GeoCalibreFournisseurRepository repository) {
        super(repository, GeoCalibreFournisseur.class);
    }

    @GraphQLQuery
    public RelayPage<GeoCalibreFournisseur> allCalibreFournisseur(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoCalibreFournisseur> getCalibreFournisseur(
            @GraphQLArgument(name = "id") String id,
            @GraphQLArgument(name = "espece") String espece) {
        return ((GeoCalibreFournisseurRepository) this.repository).findOneByIdAndEspeceId(id, espece);
    }

}
