package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoBasePaiement;
import fr.microtec.geo2.persistance.repository.tiers.GeoBasePaiementRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class GeoBasePaiementGraphQLService extends GeoAbstractGraphQLService<GeoBasePaiement, String> {

	public GeoBasePaiementGraphQLService(GeoBasePaiementRepository basePaiementRepository) {
		super(basePaiementRepository);
	}

	@GraphQLQuery
	public RelayPage<GeoBasePaiement> allBasePaiement(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	public Optional<GeoBasePaiement> getBasePaiement(String id, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.getOne(id, env);
	}

}
