package fr.microtec.geo2.service.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleNormalisation;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleNormalisationRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
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
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	protected Optional<GeoArticleNormalisation> getArticleNormalisation(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

	@GraphQLMutation
	public GeoArticleNormalisation saveArticleNormalisation(@Validated GeoArticleNormalisation client) {
		return this.save(client);
	}

	@GraphQLMutation
	public void deleteArticleNormalisation(String id) {
		this.delete(id);
	}

}
