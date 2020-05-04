package fr.microtec.geo2.service.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoEtiquetteUc;
import fr.microtec.geo2.persistance.repository.produits.GeoEtiquetteUcRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoEtiquetteUcGraphQLService extends GeoAbstractGraphQLService<GeoEtiquetteUc, String> {

	public GeoEtiquetteUcGraphQLService(GeoEtiquetteUcRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoEtiquetteUc> allEtiquetteUc(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	protected Optional<GeoEtiquetteUc> getEtiquetteUc(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

}
