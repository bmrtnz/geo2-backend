package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoEtiquetteUc;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import fr.microtec.geo2.persistance.repository.produits.GeoEtiquetteUcRepository;
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
public class GeoEtiquetteUcGraphQLService extends GeoAbstractGraphQLService<GeoEtiquetteUc, GeoProduitWithEspeceId> {

	public GeoEtiquetteUcGraphQLService(GeoEtiquetteUcRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoEtiquetteUc> allEtiquetteUc(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoEtiquetteUc> getEtiquetteUc(
			@GraphQLArgument(name = "id") GeoProduitWithEspeceId id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

}
