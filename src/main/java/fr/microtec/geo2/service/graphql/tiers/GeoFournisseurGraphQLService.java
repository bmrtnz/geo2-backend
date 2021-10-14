package fr.microtec.geo2.service.graphql.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.repository.tiers.GeoFournisseurRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoFournisseur> getFournisseur(
			@GraphQLArgument(name = "id") String id
	) {
		return this.getOne(id);
	}

	@GraphQLMutation
	public GeoFournisseur saveFournisseur(GeoFournisseur fournisseur) {
		return this.save(fournisseur);
	}

}
