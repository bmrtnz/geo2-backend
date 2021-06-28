package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitige;
import fr.microtec.geo2.persistance.repository.ordres.GeoLitigeRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoLitigeGraphQLService extends GeoAbstractGraphQLService<GeoLitige, String> {

	public GeoLitigeGraphQLService(GeoLitigeRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoLitige> allLitige(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoLitige> getLitige(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
  }

}