package fr.microtec.geo2.service.graphql.common;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoModeCulture;
import fr.microtec.geo2.persistance.repository.common.GeoModeCultureRepository;
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
public class GeoModeCultureGraphQLService extends GeoAbstractGraphQLService<GeoModeCulture, Integer> {

	public GeoModeCultureGraphQLService(GeoModeCultureRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoModeCulture> allModeCulture(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoModeCulture> getModeCulture(
			@GraphQLArgument(name = "id") Integer id
	) {
		return super.getOne(id);
	}

}
