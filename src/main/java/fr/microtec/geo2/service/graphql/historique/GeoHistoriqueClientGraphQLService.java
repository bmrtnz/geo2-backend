package fr.microtec.geo2.service.graphql.historique;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueClient;
import fr.microtec.geo2.persistance.repository.historique.GeoHistoriqueClientRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
public class GeoHistoriqueClientGraphQLService extends GeoAbstractGraphQLService<GeoHistoriqueClient, String> {

	public GeoHistoriqueClientGraphQLService(GeoHistoriqueClientRepository repository) {
		super(repository, GeoHistoriqueClient.class);
	}

	@GraphQLMutation
	public GeoHistoriqueClient saveHistoriqueClient(GeoHistoriqueClient historiqueClient, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(historiqueClient, env);
	}

}
