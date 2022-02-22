package fr.microtec.geo2.service.graphql.produits;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleMatierePremiere;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleMatierePremiereRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoArticleMatierePremiereGraphQLService
		extends GeoAbstractGraphQLService<GeoArticleMatierePremiere, String> {

	public GeoArticleMatierePremiereGraphQLService(GeoArticleMatierePremiereRepository repository) {
		super(repository, GeoArticleMatierePremiere.class);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleMatierePremiere> allArticleMatierePremiere(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
			,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoArticleMatierePremiere> getArticleMatierePremiere(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoArticleMatierePremiere saveArticleMatierePremiere(GeoArticleMatierePremiere articleMatierePremiere, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(articleMatierePremiere, env);
	}

	@GraphQLMutation
	public void deleteArticleMatierePremiere(String id) {
		this.delete(id);
	}

}
