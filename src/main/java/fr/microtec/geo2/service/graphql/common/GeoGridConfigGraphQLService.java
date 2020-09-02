package fr.microtec.geo2.service.graphql.common;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoGridConfig;
import fr.microtec.geo2.persistance.repository.common.GeoGridConfigRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoGridConfigGraphQLService extends GeoAbstractGraphQLService<GeoGridConfig, String> {

	public GeoGridConfigGraphQLService(GeoGridConfigRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoGridConfig> allGridConfig(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoGridConfig> getGridConfig(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
  }
  
  @GraphQLMutation
	public GeoGridConfig saveGridConfig(GeoGridConfig gridConfig) {
		return this.save(gridConfig);
	}

	@GraphQLMutation
	public void deleteGridConfig(String id) {
		this.delete(id);
	}
}