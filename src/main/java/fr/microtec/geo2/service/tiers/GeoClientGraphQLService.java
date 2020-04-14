package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.tiers.GeoClientRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@GraphQLApi
public class GeoClientGraphQLService extends GeoAbstractGraphQLService<GeoClient, String> {

	public GeoClientGraphQLService(GeoClientRepository clientRepository) {
		super(clientRepository);
	}

	@GraphQLQuery
	public RelayPage<GeoClient> getClients(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pagination") Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoClient> getClient(String id, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.getOne(id, env);
	}

	@GraphQLMutation
	public GeoClient saveClient(@Validated GeoClient client) {
		return this.save(client);
	}

	@GraphQLMutation
	public void deleteClient(String id) {
		this.delete(id);
	}
}
