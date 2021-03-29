package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUEntrepot;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUEntrepotKey;
import fr.microtec.geo2.persistance.repository.ordres.GeoMRUEntrepotRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoMRUEntrepotGraphQLService extends GeoAbstractGraphQLService<GeoMRUEntrepot, GeoMRUEntrepotKey> {

  public GeoMRUEntrepotGraphQLService(
		GeoMRUEntrepotRepository repository
	) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoMRUEntrepot> allMRUEntrepot(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoMRUEntrepot> getMRUEntrepot(
			@GraphQLArgument(name = "id") GeoMRUEntrepotKey id
	) {
		return super.getOne(id);
  }

}
