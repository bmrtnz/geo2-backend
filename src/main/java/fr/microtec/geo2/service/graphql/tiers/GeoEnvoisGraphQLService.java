package fr.microtec.geo2.service.graphql.tiers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoEnvois;
import fr.microtec.geo2.persistance.repository.tiers.GeoEnvoisRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoEnvoisGraphQLService extends GeoAbstractGraphQLService<GeoEnvois, String> {

	public GeoEnvoisGraphQLService(GeoEnvoisRepository envoisRepository) {
		super(envoisRepository, GeoEnvois.class);
	}

	@GraphQLQuery
	public RelayPage<GeoEnvois> allEnvois(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoEnvois> getEnvois(@GraphQLArgument(name = "id") String id) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoEnvois saveEnvois(GeoEnvois envois, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(envois, env);
	}

	@GraphQLMutation
	public List<GeoEnvois> saveAllEnvois(List<GeoEnvois> allEnvois) {
		return this.saveAll(allEnvois, null);
	}

}
