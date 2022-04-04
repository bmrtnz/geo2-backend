package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoEmballage;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import fr.microtec.geo2.persistance.repository.produits.GeoEmballageRepository;
import fr.microtec.geo2.service.ArticleService;
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
public class GeoEmballageGraphQLService extends GeoAbstractGraphQLService<GeoEmballage, GeoProduitWithEspeceId> {

	private final ArticleService articleService;

	public GeoEmballageGraphQLService(GeoEmballageRepository repository, ArticleService articleService) {
		super(repository, GeoEmballage.class);
		this.articleService = articleService;
	}

	@GraphQLQuery
	public RelayPage<GeoEmballage> allEmballage(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoEmballage> getEmballage(
			@GraphQLArgument(name = "id") GeoProduitWithEspeceId id) {
		return super.getOne(id);
	}

	@GraphQLQuery
	public RelayPage<GeoEmballage> allDistinctEmballage(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.articleService.fetchDistinct(GeoEmballage.class, pageable, search, env);
	}

}
