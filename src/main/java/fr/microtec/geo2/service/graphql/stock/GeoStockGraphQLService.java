package fr.microtec.geo2.service.graphql.stock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoStock;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticle;
import fr.microtec.geo2.persistance.repository.stock.GeoStockRepository;
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
public class GeoStockGraphQLService extends GeoAbstractGraphQLService<GeoStock, String> {

	public GeoStockGraphQLService(GeoStockRepository stockRepository) {
		super(stockRepository, GeoStock.class);
	}

	@GraphQLQuery
	public RelayPage<GeoStock> allStock(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoStock> getStock(
			@GraphQLArgument(name = "id") String id) {
		return this.getOne(id);
	}

	@GraphQLQuery
	public List<GeoStockArticle> allStockArticle(
			@GraphQLArgument(name = "espece") String espece,
			@GraphQLArgument(name = "variete", defaultValue = "%") String variete,
			@GraphQLArgument(name = "origine", defaultValue = "%") String origine,
			@GraphQLArgument(name = "modeCulture", defaultValue = "%") String modeCulture,
			@GraphQLArgument(name = "emballage", defaultValue = "%") String emballage,
			@GraphQLArgument(name = "bureauAchat", defaultValue = "%") String bureauAchat) {
		return this.allStockArticle(espece, variete, origine, modeCulture, emballage, bureauAchat);
	}

}
