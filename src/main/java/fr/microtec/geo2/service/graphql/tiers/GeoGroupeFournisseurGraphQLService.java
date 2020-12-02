package fr.microtec.geo2.service.graphql.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoGroupeFournisseur;
import fr.microtec.geo2.persistance.repository.tiers.GeoGroupeFournisseurRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoGroupeFournisseurGraphQLService extends GeoAbstractGraphQLService<GeoGroupeFournisseur, String> {

	public GeoGroupeFournisseurGraphQLService(GeoGroupeFournisseurRepository groupeFournisseurRepository) {
		super(groupeFournisseurRepository);
	}

	@GraphQLQuery
	public RelayPage<GeoGroupeFournisseur> allGroupeFournisseur(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoGroupeFournisseur> getGroupeFournisseur(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

}
