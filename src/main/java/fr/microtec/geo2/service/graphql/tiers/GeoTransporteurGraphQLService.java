package fr.microtec.geo2.service.graphql.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import fr.microtec.geo2.persistance.repository.tiers.GeoTransporteurRepository;
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
public class GeoTransporteurGraphQLService extends GeoAbstractGraphQLService<GeoTransporteur, String> {

	public GeoTransporteurGraphQLService(GeoTransporteurRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoTransporteur> allTransporteur(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoTransporteur> getTransporteur(
			@GraphQLArgument(name = "id") String id
	) {
		return this.getOne(id);
	}

	@GraphQLMutation
	public GeoTransporteur saveTransporteur(GeoTransporteur transporteur) {
		return this.save(transporteur);
	}

	@GraphQLMutation
	public void deleteTransporteur(String id) {
		this.delete(id);
	}

}
