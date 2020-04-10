package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.persistance.entity.tiers.GeoBasePaiement;
import fr.microtec.geo2.persistance.repository.tiers.GeoBasePaiementRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@GraphQLApi
@Service
public class GeoBasePaiementService extends GeoAbstractGraphQlService<GeoBasePaiement, String> {

	public GeoBasePaiementService(GeoBasePaiementRepository basePaiementRepository) {
		super(basePaiementRepository);
	}

	@GraphQLQuery
	public Page<GeoBasePaiement> getBasePaiements(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pagination") PageRequest pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

}
