package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigneTotauxDetail;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.service.OrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoOrdreLigneGraphQLService extends GeoAbstractGraphQLService<GeoOrdreLigne, String> {

	private final OrdreService ordreService;

	public GeoOrdreLigneGraphQLService(
		GeoOrdreLigneRepository repository,
		OrdreService ordreService
	) {
		super(repository);
		this.ordreService = ordreService;
	}

	@GraphQLQuery
	public RelayPage<GeoOrdreLigne> allOrdreLigne(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public RelayPage<GeoOrdreLigneTotauxDetail> allOrdreLigneTotauxDetail(
			@GraphQLArgument(name = "ordre") @GraphQLNonNull String ordre,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.ordreService.fetchOrdreLignesTotauxDetail(ordre,pageable);
	}

	@GraphQLQuery
	public Optional<GeoOrdreLigne> getOrdreLigne(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
  }

}