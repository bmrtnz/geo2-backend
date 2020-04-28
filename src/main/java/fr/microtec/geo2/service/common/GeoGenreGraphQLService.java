package fr.microtec.geo2.service.common;

import fr.microtec.geo2.persistance.entity.common.GeoGenre;
import fr.microtec.geo2.persistance.repository.common.GeoGenreRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.execution.relay.Page;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoGenreGraphQLService extends GeoAbstractGraphQLService<GeoGenre, String> {

	public GeoGenreGraphQLService(GeoGenreRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public Page<GeoGenre> allGenre(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	protected Optional<GeoGenre> getGenre(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}
}
