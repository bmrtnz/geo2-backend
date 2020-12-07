package fr.microtec.geo2.service.graphql.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransitaire;
import fr.microtec.geo2.persistance.repository.tiers.GeoTransitaireRepository;
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
public class GeoTransitaireGraphQLService extends GeoAbstractGraphQLService<GeoTransitaire, String> {

	public GeoTransitaireGraphQLService(GeoTransitaireRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoTransitaire> allTransitaire(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoTransitaire> getTransitaire(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

}
