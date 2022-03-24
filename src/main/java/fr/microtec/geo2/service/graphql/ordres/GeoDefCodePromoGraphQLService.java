package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoDefCodePromo;
import fr.microtec.geo2.persistance.entity.ordres.GeoDefCodePromoId;
import fr.microtec.geo2.persistance.repository.ordres.GeoDefCodePromoRepository;
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
public class GeoDefCodePromoGraphQLService extends GeoAbstractGraphQLService<GeoDefCodePromo, GeoDefCodePromoId> {

	public GeoDefCodePromoGraphQLService(GeoDefCodePromoRepository repository) {
		super(repository, GeoDefCodePromo.class);
	}

	@GraphQLQuery
	public RelayPage<GeoDefCodePromo> allDefCodePromo(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoDefCodePromo> getDefCodePromo(
			@GraphQLArgument(name = "id") GeoDefCodePromoId id) {
		return super.getOne(id);
	}

}
