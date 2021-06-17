package fr.microtec.geo2.service.graphql.tiers;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoIdentifiantFournisseur;
import fr.microtec.geo2.persistance.repository.tiers.GeoIdentifiantFournisseurRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoIdentifiantFournisseurGraphQLService extends GeoAbstractGraphQLService<GeoIdentifiantFournisseur, Integer> {

	public GeoIdentifiantFournisseurGraphQLService(GeoIdentifiantFournisseurRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoIdentifiantFournisseur> allIdentifiantFournisseur(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoIdentifiantFournisseur> getIdentifiantFournisseur(
			@GraphQLArgument(name = "id") Integer id
	) {
		return super.getOne(id);
	}

}
