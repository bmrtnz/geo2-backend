package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoOrigine;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import fr.microtec.geo2.persistance.repository.produits.GeoOrigineRepository;
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
public class GeoOrigineGraphQLService extends GeoAbstractGraphQLService<GeoOrigine, GeoProduitWithEspeceId> {

	private final ArticleService articleService;

	public GeoOrigineGraphQLService(GeoOrigineRepository repository, ArticleService articleService) {
		super(repository, GeoOrigine.class);
		this.articleService = articleService;
	}

	@GraphQLQuery
	public RelayPage<GeoOrigine> allOrigine(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoOrigine> getOrigine(
			@GraphQLArgument(name = "id") GeoProduitWithEspeceId id) {
		return super.getOne(id);
	}

	@GraphQLQuery
	public RelayPage<GeoOrigine> allDistinctOrigine(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.articleService.fetchDistinct(GeoOrigine.class, pageable, search, env);
	}

}
