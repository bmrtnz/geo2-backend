package fr.microtec.geo2.service.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleNormalisation;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleNormalisationRepository;
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
public class GeoArticleNormalisationGraphQLService extends GeoAbstractGraphQLService<GeoArticleNormalisation, String> {

	public GeoArticleNormalisationGraphQLService(GeoArticleNormalisationRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleNormalisation> allArticleNormalisation(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	protected Optional<GeoArticleNormalisation> getArticleNormalisation(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

	@GraphQLMutation
	public GeoArticleNormalisation saveArticleNormalisation(@Validated GeoArticleNormalisation articleNormalisation) {
		return this.save(articleNormalisation);
	}

	@GraphQLMutation
	public void deleteArticleNormalisation(String id) {
		this.delete(id);
	}

}
