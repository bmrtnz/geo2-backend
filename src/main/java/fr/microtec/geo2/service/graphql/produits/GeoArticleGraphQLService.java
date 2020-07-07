package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleRepository;
import fr.microtec.geo2.service.ArticleService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoArticleGraphQLService extends GeoAbstractGraphQLService<GeoArticle, String> {

	private final ArticleService articleService;

	public GeoArticleGraphQLService(GeoArticleRepository repository, ArticleService articleService) {
		super(repository);
		this.articleService = articleService;
	}

	@GraphQLQuery
	public RelayPage<GeoArticle> allArticle(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoArticle> getArticle(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

	//@GraphQLMutation
	//public GeoArticle saveArticle(GeoArticle article) {
		/*String id = (String) this.getId(article);
		Optional<GeoArticle> optionalEntity = this.repository.findById(id);*/

		// Update TODO : No update on article ?
		/*if (optionalEntity.isPresent()) {
			article = this.merge(article, optionalEntity.get(), null);
		}*/

		//return this.articleService.save(article);
	//}

	@GraphQLMutation
	public void deleteArticle(String id) {
		this.delete(id);
	}
}
