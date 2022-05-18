package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoCodifDevalexp;
import fr.microtec.geo2.persistance.repository.ordres.GeoCodifDevalexpRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoCodifDevalexpGraphQLService extends GeoAbstractGraphQLService<GeoCodifDevalexp, String> {

	public GeoCodifDevalexpGraphQLService(GeoCodifDevalexpRepository repository) {
		super(repository, GeoCodifDevalexp.class);
	}

	@GraphQLQuery
	public RelayPage<GeoCodifDevalexp> allCodifDevalexp(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoCodifDevalexp> getCodifDevalexp(
			@GraphQLArgument(name = "id") String id) {
		return super.getOne(id);
	}

}
