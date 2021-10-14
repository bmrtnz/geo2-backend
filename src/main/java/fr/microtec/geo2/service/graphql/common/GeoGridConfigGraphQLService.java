package fr.microtec.geo2.service.graphql.common;

import java.util.Optional;

import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoGridConfig;
import fr.microtec.geo2.persistance.entity.common.GeoGridConfigKey;
import fr.microtec.geo2.persistance.repository.common.GeoGridConfigRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoGridConfigGraphQLService extends GeoAbstractGraphQLService<GeoGridConfig, GeoGridConfigKey> {

	public GeoGridConfigGraphQLService(GeoGridConfigRepository repository) {
		super(repository, GeoGridConfig.class);
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
			@GraphQLArgument(name = "id") GeoGridConfigKey id
	) {
		return super.getOne(id);
  }

  	@GraphQLMutation
	public GeoGridConfig saveGridConfig(GeoGridConfig gridConfig) {
		return this.save(gridConfig);
	}

	@GraphQLMutation
	public void deleteGridConfig(GeoGridConfigKey id) {
		this.delete(id);
	}
}
