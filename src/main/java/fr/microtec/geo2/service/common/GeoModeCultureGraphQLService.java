package fr.microtec.geo2.service.common;

import fr.microtec.geo2.persistance.entity.common.GeoModeCulture;
import fr.microtec.geo2.persistance.repository.common.GeoModeCultureRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.execution.relay.Page;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoModeCultureGraphQLService extends GeoAbstractGraphQLService<GeoModeCulture, String> {

	public GeoModeCultureGraphQLService(GeoModeCultureRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public Page<GeoModeCulture> allModeCulture(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoModeCulture> getModeCulture(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

}
