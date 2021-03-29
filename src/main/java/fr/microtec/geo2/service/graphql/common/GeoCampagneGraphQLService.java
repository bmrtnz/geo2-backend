package fr.microtec.geo2.service.graphql.common;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.repository.common.GeoCampagneRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoCampagneGraphQLService extends GeoAbstractGraphQLService<GeoCampagne, String> {

	public GeoCampagneGraphQLService(GeoCampagneRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoCampagne> allCampagne(
		@GraphQLArgument(name = "search") String search,
		@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoCampagne> getCampagne(
		@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}
}
