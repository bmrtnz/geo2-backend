package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.service.OrdreLigneService;
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
public class GeoOrdreLigneGraphQLService extends GeoAbstractGraphQLService<GeoOrdreLigne, String> {

	private final OrdreLigneService ordreLigneService;

	public GeoOrdreLigneGraphQLService(
		GeoOrdreLigneRepository repository,
		OrdreLigneService ordreLigneService
	) {
		super(repository, GeoOrdreLigne.class);
		this.ordreLigneService = ordreLigneService;
	}

	@GraphQLQuery
	public RelayPage<GeoOrdreLigne> allOrdreLigne(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public RelayPage<GeoOrdreLigne> allOrdreLigneMarge(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.ordreLigneService.fetchAllMarge(search, pageable, env);
	}

	@GraphQLQuery
	public RelayPage<GeoOrdreLigne> allOrdreLigneTotauxDetail(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.ordreLigneService.fetchOrdreLignesTotauxDetail(search,pageable,env);
	}

	@GraphQLQuery
	public Optional<GeoOrdreLigne> getOrdreLigne(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
  }

}
