package fr.microtec.geo2.service.graphql.stock;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoStockConsolide;
import fr.microtec.geo2.persistance.repository.stock.GeoStockConsolideRepository;
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
public class GeoStockConsolideGraphQLService extends GeoAbstractGraphQLService<GeoStockConsolide, String> {

	public GeoStockConsolideGraphQLService(GeoStockConsolideRepository repository) {
		super(repository, GeoStockConsolide.class);
	}

	@GraphQLQuery
	public RelayPage<GeoStockConsolide> allStockConsolide(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoStockConsolide> getStockConsolide(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
    }

    @GraphQLMutation
    public void saveStockConsolide(GeoStockConsolide stockConsolide, @GraphQLEnvironment ResolutionEnvironment env) {
        this.saveEntity(stockConsolide, env);
    }

}
