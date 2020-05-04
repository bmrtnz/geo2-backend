package fr.microtec.geo2.service.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleEmballage;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleEmballageRepository;
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
public class GeoArticleEmballageGraphQLService extends GeoAbstractGraphQLService<GeoArticleEmballage, String> {

	public GeoArticleEmballageGraphQLService(GeoArticleEmballageRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleEmballage> allArticleEmballage(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	protected Optional<GeoArticleEmballage> getArticleEmballage(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

	@GraphQLMutation
	public GeoArticleEmballage saveArticleEmballage(@Validated GeoArticleEmballage client) {
		return this.save(client);
	}

	@GraphQLMutation
	public void deleteArticleEmballage(String id) {
		this.delete(id);
	}

}
