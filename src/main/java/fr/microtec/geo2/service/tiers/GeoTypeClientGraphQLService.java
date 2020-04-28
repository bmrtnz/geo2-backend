package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypeClient;
import fr.microtec.geo2.persistance.repository.tiers.GeoTypeClientRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoTypeClientGraphQLService extends GeoAbstractGraphQLService<GeoTypeClient, String> {

	public GeoTypeClientGraphQLService(GeoTypeClientRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoTypeClient> allTypeClient(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	protected Optional<GeoTypeClient> getTypeClient(
			@GraphQLArgument(name = "search") String search,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(search, env);
	}

}
