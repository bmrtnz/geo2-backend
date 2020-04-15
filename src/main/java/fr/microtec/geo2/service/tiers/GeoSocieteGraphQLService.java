package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.tiers.GeoSecteurRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoSocieteRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class GeoSocieteGraphQLService extends GeoAbstractGraphQLService<GeoSociete, String> {

	public GeoSocieteGraphQLService(GeoSocieteRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoSociete> allSociete(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

}
