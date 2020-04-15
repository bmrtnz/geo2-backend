package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import fr.microtec.geo2.persistance.repository.tiers.GeoTransporteurRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoTransporteurGraphQLService extends GeoAbstractGraphQLService<GeoTransporteur, String> {

	public GeoTransporteurGraphQLService(GeoTransporteurRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoTransporteur> allTransporteur(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	public Optional<GeoTransporteur> getTransporteur(String id, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.getOne(id, env);
	}

	@GraphQLMutation
	public GeoTransporteur saveTransporteur(@Validated GeoTransporteur client) {
		return this.save(client);
	}

	@GraphQLMutation
	public void deleteTransporteur(String id) {
		this.delete(id);
	}

}
