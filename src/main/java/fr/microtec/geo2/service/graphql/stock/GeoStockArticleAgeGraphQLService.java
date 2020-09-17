package fr.microtec.geo2.service.graphql.stock;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeKey;
import fr.microtec.geo2.persistance.repository.stock.GeoStockArticleAgeRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoStockArticleAgeGraphQLService extends GeoAbstractGraphQLService<GeoStockArticleAge, GeoStockArticleAgeKey> {

	public GeoStockArticleAgeGraphQLService(GeoStockArticleAgeRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoStockArticleAge> allStockArticleAge(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoStockArticleAge> getStockArticleAge(
			@GraphQLArgument(name = "id") GeoStockArticleAgeKey id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(id, env);
  }

}