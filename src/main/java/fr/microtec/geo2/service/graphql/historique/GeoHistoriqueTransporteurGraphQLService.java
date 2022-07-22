package fr.microtec.geo2.service.graphql.historique;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueTransporteur;
import fr.microtec.geo2.persistance.repository.historique.GeoHistoriqueTransporteurRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
public class GeoHistoriqueTransporteurGraphQLService extends GeoAbstractGraphQLService<GeoHistoriqueTransporteur, BigDecimal> {

	public GeoHistoriqueTransporteurGraphQLService(GeoHistoriqueTransporteurRepository repository) {
		super(repository, GeoHistoriqueTransporteur.class);
	}

	@GraphQLMutation
	public GeoHistoriqueTransporteur saveHistoriqueTransporteur(GeoHistoriqueTransporteur historiqueTransporteur, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(historiqueTransporteur, env);
	}

}
