package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoLieuPassageAQuai;
import fr.microtec.geo2.persistance.entity.tiers.GeoMoyenPaiement;
import fr.microtec.geo2.persistance.repository.tiers.GeoLieuPassageAQuaiRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoMoyenPaiementRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class GeoMoyenPaiementGraphQLService extends GeoAbstractGraphQLService<GeoMoyenPaiement, String> {

	public GeoMoyenPaiementGraphQLService(GeoMoyenPaiementRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoMoyenPaiement> getMoyenPaiements(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

}
