package fr.microtec.geo2.service.produits;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleCahierDesCharge;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleCahierDesChargeRepository;
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
public class GeoArticleCahierDesChargeGraphQLService extends GeoAbstractGraphQLService<GeoArticleCahierDesCharge, String> {

	public GeoArticleCahierDesChargeGraphQLService(GeoArticleCahierDesChargeRepository repository) {
		super(repository);
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
	protected Optional<GeoArticleCahierDesCharge> getArticleCahierDesCharge(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
	}

	@GraphQLMutation
	public GeoArticleCahierDesCharge saveArticleCahierDesCharge(@Validated GeoArticleCahierDesCharge client) {
		return this.save(client);
	}

	@GraphQLMutation
	public void deleteArticleCahierDesCharge(String id) {
		this.delete(id);
	}

}
