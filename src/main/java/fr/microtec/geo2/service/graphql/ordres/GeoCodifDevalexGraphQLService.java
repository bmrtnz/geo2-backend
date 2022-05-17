package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoCodifDevalex;
import fr.microtec.geo2.persistance.repository.ordres.GeoCodifDevalexRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoCodifDevalexGraphQLService extends GeoAbstractGraphQLService<GeoCodifDevalex, String> {

	public GeoCodifDevalexGraphQLService(GeoCodifDevalexRepository repository) {
		super(repository, GeoCodifDevalex.class);
	}

	@GraphQLQuery
	public RelayPage<GeoCodifDevalex> allCodifDevalex(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoCodifDevalex> getCodifDevalex(
			@GraphQLArgument(name = "id") String id) {
		return super.getOne(id);
	}

}
