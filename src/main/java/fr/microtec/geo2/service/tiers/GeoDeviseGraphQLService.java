package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoDevise;
import fr.microtec.geo2.persistance.repository.tiers.GeoDeviseRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class GeoDeviseGraphQLService extends GeoAbstractGraphQLService<GeoDevise, String> {

	public GeoDeviseGraphQLService(GeoDeviseRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoDevise> allDevise(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	public Optional<GeoDevise> getDevise(String id, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.getOne(id, env);
	}

}
