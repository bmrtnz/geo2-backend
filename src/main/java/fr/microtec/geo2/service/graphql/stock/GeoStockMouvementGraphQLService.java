package fr.microtec.geo2.service.graphql.stock;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoStockMouvement;
import fr.microtec.geo2.persistance.repository.stock.GeoStockMouvementRepository;
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
public class GeoStockMouvementGraphQLService extends GeoAbstractGraphQLService<GeoStockMouvement, String> {

	public GeoStockMouvementGraphQLService(GeoStockMouvementRepository stockMouvementRepository) {
		super(stockMouvementRepository);
	}

	@GraphQLQuery
	public RelayPage<GeoStockMouvement> allStockMouvement(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoStockMouvement> getStockMouvement(
			@GraphQLArgument(name = "id") String id
	) {
		return this.getOne(id);
  }

}
