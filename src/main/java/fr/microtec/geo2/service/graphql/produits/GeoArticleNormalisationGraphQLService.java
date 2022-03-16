package fr.microtec.geo2.service.graphql.produits;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleNormalisation;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleNormalisationRepository;
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
public class GeoArticleNormalisationGraphQLService extends GeoAbstractGraphQLService<GeoArticleNormalisation, String> {

	public GeoArticleNormalisationGraphQLService(GeoArticleNormalisationRepository repository) {
		super(repository, GeoArticleNormalisation.class);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleNormalisation> allArticleNormalisation(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
			,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoArticleNormalisation> getArticleNormalisation(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoArticleNormalisation saveArticleNormalisation(GeoArticleNormalisation articleNormalisation, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(articleNormalisation, env);
	}

	@GraphQLMutation
	public void deleteArticleNormalisation(String id) {
		this.delete(id);
	}

}
