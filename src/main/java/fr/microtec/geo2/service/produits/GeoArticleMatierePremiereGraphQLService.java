package fr.microtec.geo2.service.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleMatierePremiere;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleMatierePremiereRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@GraphQLApi
public class GeoArticleMatierePremiereGraphQLService extends GeoAbstractGraphQLService<GeoArticleMatierePremiere, String> {

	public GeoArticleMatierePremiereGraphQLService(GeoArticleMatierePremiereRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleMatierePremiere> allArticleMatierePremiere(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	protected Optional<GeoArticleMatierePremiere> getArticleMatierePremiere(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

	@GraphQLMutation
	public GeoArticleMatierePremiere saveArticleMatierePremiere(@Validated GeoArticleMatierePremiere client) {
		return this.save(client);
	}

	@GraphQLMutation
	public void deleteArticleMatierePremiere(String id) {
		this.delete(id);
	}

}
