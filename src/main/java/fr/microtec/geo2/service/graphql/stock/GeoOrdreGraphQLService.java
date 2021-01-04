package fr.microtec.geo2.service.graphql.stock;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoOrdre;
import fr.microtec.geo2.persistance.repository.stock.GeoOrdreRepository;
import fr.microtec.geo2.service.OrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoOrdreGraphQLService extends GeoAbstractGraphQLService<GeoOrdre, String> {

	private final OrdreService ordreService;

	public GeoOrdreGraphQLService(GeoOrdreRepository repository, OrdreService ordreService) {
		super(repository);
		this.ordreService = ordreService;
	}

	@GraphQLQuery
	public RelayPage<GeoOrdre> allOrdre(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoOrdre> getOrdre(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoOrdre saveOrdre(GeoOrdre ordre) {
		return this.ordreService.save(ordre);
	}

	@GraphQLMutation
	public GeoOrdre cloneOrdre(GeoOrdre ordre) {
		return this.ordreService.clone(ordre);
	}

	@GraphQLMutation
	public void deleteOrdre(GeoOrdre ordre) {
		this.delete(ordre);
	}

}
