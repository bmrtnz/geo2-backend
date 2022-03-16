package fr.microtec.geo2.service.graphql.produits;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleCahierDesCharge;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleCahierDesChargeRepository;
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
public class GeoArticleCahierDesChargeGraphQLService
		extends GeoAbstractGraphQLService<GeoArticleCahierDesCharge, String> {

	public GeoArticleCahierDesChargeGraphQLService(GeoArticleCahierDesChargeRepository repository) {
		super(repository, GeoArticleCahierDesCharge.class);
	}

	@GraphQLQuery
	public RelayPage<GeoArticleCahierDesCharge> allArticleCahierDesCharge(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoArticleCahierDesCharge> getArticleCahierDesCharge(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoArticleCahierDesCharge saveArticleCahierDesCharge(GeoArticleCahierDesCharge articleCahierDesCharge, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(articleCahierDesCharge, env);
	}

	@GraphQLMutation
	public void deleteArticleCahierDesCharge(String id) {
		this.delete(id);
	}

}
