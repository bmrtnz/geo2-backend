package fr.microtec.geo2.service.graphql.produits;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.repository.produits.GeoEspeceRepository;
import fr.microtec.geo2.service.ArticleService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoEspeceGraphQLService extends GeoAbstractGraphQLService<GeoEspece, String> {

	private final ArticleService articleService;

	public GeoEspeceGraphQLService(GeoEspeceRepository repository, ArticleService articleService) {
		super(repository, GeoEspece.class);
		this.articleService = articleService;
	}

	@GraphQLQuery
	public RelayPage<GeoEspece> allEspece(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoEspece> getEspece(
			@GraphQLArgument(name = "id") String id) {
		return super.getOne(id);
	}

	@GraphQLQuery
	public RelayPage<GeoEspece> allDistinctEspece(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.articleService.fetchDistinct(GeoEspece.class, pageable, search, env);
	}

}
