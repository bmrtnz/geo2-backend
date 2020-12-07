package fr.microtec.geo2.service.graphql.stock;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoColis;
import fr.microtec.geo2.persistance.repository.stock.GeoColisRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoColisGraphQLService extends GeoAbstractGraphQLService<GeoColis, String> {

	public GeoColisGraphQLService(GeoColisRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoColis> allColis(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoColis> getColis(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
  }

}