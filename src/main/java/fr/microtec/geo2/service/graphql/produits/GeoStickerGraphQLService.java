package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import fr.microtec.geo2.persistance.entity.produits.GeoStickeur;
import fr.microtec.geo2.persistance.repository.produits.GeoStickerRepository;
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
public class GeoStickerGraphQLService extends GeoAbstractGraphQLService<GeoStickeur, GeoProduitWithEspeceId> {

	public GeoStickerGraphQLService(GeoStickerRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoStickeur> allStickeur(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoStickeur> getStickeur(
			@GraphQLArgument(name = "id") GeoProduitWithEspeceId id
	) {
		return super.getOne(id);
	}

}
