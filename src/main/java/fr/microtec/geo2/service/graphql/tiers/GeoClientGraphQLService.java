package fr.microtec.geo2.service.graphql.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.tiers.GeoClientRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoClientGraphQLService extends GeoAbstractGraphQLService<GeoClient, String> {

	public GeoClientGraphQLService(GeoClientRepository clientRepository) {
		super(clientRepository, GeoClient.class);
	}

	@GraphQLQuery
	public RelayPage<GeoClient> allClient(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoClient> getClient(
			@GraphQLArgument(name = "id") String id
	) {
		return this.getOne(id);
	}

	@GraphQLMutation
	public GeoClient saveClient(GeoClient client) {
		return this.save(client);
	}

	@GraphQLMutation
	public void deleteClient(String id) {
		this.delete(id);
	}

	@GraphQLQuery
	public long countClient(
		@GraphQLArgument(name = "search") String search
	) {
		return this.count(search);
	}
}
