package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.repository.ordres.GeoMRUOrdreRepository;
import fr.microtec.geo2.service.MRUOrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoMRUOrdreGraphQLService extends GeoAbstractGraphQLService<GeoMRUOrdre, GeoMRUOrdreKey> {

	private final MRUOrdreService mruOrdreService;

  public GeoMRUOrdreGraphQLService(
		GeoMRUOrdreRepository repository,
		MRUOrdreService mruOrdreService
	) {
		super(repository);
		this.mruOrdreService = mruOrdreService;
	}

	@GraphQLQuery
	public RelayPage<GeoMRUOrdre> allMRUOrdre(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public RelayPage<GeoMRUOrdre> allGroupedMRUOrdre(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.mruOrdreService.fetchGroupedMRUOrdre(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoMRUOrdre> getMRUOrdre(
			@GraphQLArgument(name = "id") GeoMRUOrdreKey id
	) {
		return super.getOne(id);
  }

}
