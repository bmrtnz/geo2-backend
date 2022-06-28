package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import fr.microtec.geo2.persistance.entity.produits.GeoStickeur;
import fr.microtec.geo2.persistance.repository.produits.GeoStickerRepository;
import fr.microtec.geo2.service.DocumentService;
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

    private final DocumentService documentService;

	public GeoStickerGraphQLService(GeoStickerRepository repository, DocumentService documentService) {
		super(repository, GeoStickeur.class);
        this.documentService = documentService;
    }

	@GraphQLQuery
	public RelayPage<GeoStickeur> allStickeur(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoStickeur> getStickeur(
			@GraphQLArgument(name = "id") GeoProduitWithEspeceId id
	) {
		return this.documentService.loadDocument(super.getOne(id));
	}

}
