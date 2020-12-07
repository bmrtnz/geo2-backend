package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleEmballage;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleEmballageRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoArticleEmballageGraphQLService extends GeoAbstractGraphQLService<GeoArticleEmballage, String> {

	public GeoArticleEmballageGraphQLService(GeoArticleEmballageRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleEmballage> allArticleEmballage(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoArticleEmballage> getArticleEmballage(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoArticleEmballage saveArticleEmballage(GeoArticleEmballage articleEmballage) {
		return this.save(articleEmballage);
	}

	@GraphQLMutation
	public void deleteArticleEmballage(String id) {
		this.delete(id);
	}

}
