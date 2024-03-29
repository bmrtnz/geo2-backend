package fr.microtec.geo2.service.graphql.common;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoTypeVente;
import fr.microtec.geo2.persistance.repository.common.GeoTypeVenteRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoTypeVenteGraphQLService extends GeoAbstractGraphQLService<GeoTypeVente, String> {

	public GeoTypeVenteGraphQLService(GeoTypeVenteRepository repository) {
		super(repository, GeoTypeVente.class);
	}

	@GraphQLQuery
	public RelayPage<GeoTypeVente> allTypeVente(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoTypeVente> getTypeVente(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

}
