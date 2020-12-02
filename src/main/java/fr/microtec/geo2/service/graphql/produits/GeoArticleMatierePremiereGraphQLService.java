package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleMatierePremiere;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleMatierePremiereRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoArticleMatierePremiereGraphQLService
		extends GeoAbstractGraphQLService<GeoArticleMatierePremiere, String> {

	public GeoArticleMatierePremiereGraphQLService(GeoArticleMatierePremiereRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleMatierePremiere> allArticleMatierePremiere(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoArticleMatierePremiere> getArticleMatierePremiere(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoArticleMatierePremiere saveArticleMatierePremiere(GeoArticleMatierePremiere articleMatierePremiere) {
		return this.save(articleMatierePremiere);
	}

	@GraphQLMutation
	public void deleteArticleMatierePremiere(String id) {
		this.delete(id);
	}

}
