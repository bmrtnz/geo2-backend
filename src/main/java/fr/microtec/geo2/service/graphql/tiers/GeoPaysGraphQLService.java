package fr.microtec.geo2.service.graphql.tiers;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import fr.microtec.geo2.persistance.repository.tiers.GeoPaysRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.graphql.PaysService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoPaysGraphQLService extends GeoAbstractGraphQLService<GeoPays, String> {

	private final PaysService paysService;

	public GeoPaysGraphQLService(
		GeoPaysRepository repository,
		PaysService paysService
	) {
		super(repository);
		this.paysService = paysService;
	}

	@GraphQLQuery
	public RelayPage<GeoPays> allPays(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoPays> getPays(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLQuery
	public Float sommeAgrement(@GraphQLContext GeoPays pays) {
		return this.paysService.fetchSommeAgrement(pays);
	}

}
