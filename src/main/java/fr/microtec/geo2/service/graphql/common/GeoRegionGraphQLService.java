package fr.microtec.geo2.service.graphql.common;

import java.util.Optional;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoRegion;
import fr.microtec.geo2.persistance.repository.common.GeoRegionRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoRegionGraphQLService extends GeoAbstractGraphQLService<GeoRegion, Integer> {

	public GeoRegionGraphQLService(GeoRegionRepository repository) {
		super(repository, GeoRegion.class);
	}

	@GraphQLQuery
	public RelayPage<GeoRegion> allRegion(
		@GraphQLArgument(name = "search") String search,
		@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
		@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoRegion> getRegion(
		@GraphQLArgument(name = "id") Integer id
	) {
		return super.getOne(id);
	}
}