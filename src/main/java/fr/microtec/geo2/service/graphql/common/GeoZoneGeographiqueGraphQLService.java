package fr.microtec.geo2.service.graphql.common;

import java.util.Optional;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoZoneGeographique;
import fr.microtec.geo2.persistance.repository.common.GeoZoneGeographiqueRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoZoneGeographiqueGraphQLService extends GeoAbstractGraphQLService<GeoZoneGeographique, Integer> {

	public GeoZoneGeographiqueGraphQLService(GeoZoneGeographiqueRepository repository) {
		super(repository, GeoZoneGeographique.class);
	}

	@GraphQLQuery
	public RelayPage<GeoZoneGeographique> allZoneGeographique(
		@GraphQLArgument(name = "search") String search,
		@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
		@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoZoneGeographique> getZoneGeographique(
		@GraphQLArgument(name = "id") Integer id
	) {
		return super.getOne(id);
	}
}
