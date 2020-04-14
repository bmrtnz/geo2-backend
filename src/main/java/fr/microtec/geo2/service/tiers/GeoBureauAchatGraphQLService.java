package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoBureauAchat;
import fr.microtec.geo2.persistance.repository.tiers.GeoBureauAchatRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class GeoBureauAchatGraphQLService extends GeoAbstractGraphQLService<GeoBureauAchat, String> {

	public GeoBureauAchatGraphQLService(GeoBureauAchatRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoBureauAchat> getBureauAchats(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

}
