package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleStatistiqueClient;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleStatistiqueFournisseur;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleRepository;
import fr.microtec.geo2.service.ArticleService;
import fr.microtec.geo2.service.DocumentService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoArticleGraphQLService extends GeoAbstractGraphQLService<GeoArticle, String> {

	private final ArticleService articleService;
    private final DocumentService documentService;

	public GeoArticleGraphQLService(GeoArticleRepository repository, ArticleService articleService, DocumentService documentService) {
		super(repository, GeoArticle.class);
		this.articleService = articleService;
        this.documentService = documentService;
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
		return this.documentService.loadDocuments(super.getOne(id), env);
	}

	@GraphQLMutation
	public GeoArticle saveArticle(
		GeoArticle article,
		@GraphQLArgument(name = "clone") Boolean clone
	) {
		GeoArticle saved = this.articleService.save(article, clone);
		((GeoArticleRepository)this.repository).syncArticle(saved.getId());
		return saved;
	}

	@GraphQLMutation
	public void deleteArticle(String id) {
		this.delete(id);
	}

	@GraphQLQuery
	public long countArticle(
		@GraphQLArgument(name = "search") String search
	) {
		return this.count(search);
	}

    @GraphQLQuery
    public List<GeoArticleStatistiqueClient> allArticleStatistiqueClients(
        @GraphQLArgument(name = "article") String article,
        @GraphQLArgument(name = "societe") String societe,
        @GraphQLArgument(name = "dateMin") LocalDate dateMin,
        @GraphQLArgument(name = "dateMax") LocalDate dateMax
    ) {
        return ((GeoArticleRepository) this.repository).allArticleStatistiqueClients(article, societe, dateMin, dateMax);
    }

    @GraphQLQuery
    public List<GeoArticleStatistiqueFournisseur> allArticleStatistiqueFournisseurs(
        @GraphQLArgument(name = "article") String article,
        @GraphQLArgument(name = "societe") String societe,
        @GraphQLArgument(name = "dateMin") LocalDate dateMin,
        @GraphQLArgument(name = "dateMax") LocalDate dateMax
    ) {
        return ((GeoArticleRepository) this.repository).allArticleStatistiqueFournisseurs(article, societe, dateMin, dateMax);
    }
}
