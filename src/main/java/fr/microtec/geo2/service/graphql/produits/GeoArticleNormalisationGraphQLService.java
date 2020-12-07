package fr.microtec.geo2.service.graphql.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleNormalisation;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleNormalisationRepository;
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
public class GeoArticleNormalisationGraphQLService extends GeoAbstractGraphQLService<GeoArticleNormalisation, String> {

	public GeoArticleNormalisationGraphQLService(GeoArticleNormalisationRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleNormalisation> allArticleNormalisation(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoArticleNormalisation> getArticleNormalisation(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoArticleNormalisation saveArticleNormalisation(GeoArticleNormalisation articleNormalisation) {
		return this.save(articleNormalisation);
	}

	@GraphQLMutation
	public void deleteArticleNormalisation(String id) {
		this.delete(id);
	}

}
