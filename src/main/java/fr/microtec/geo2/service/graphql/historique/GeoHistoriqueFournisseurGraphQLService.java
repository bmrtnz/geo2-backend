package fr.microtec.geo2.service.graphql.historique;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueFournisseur;
import fr.microtec.geo2.persistance.repository.historique.GeoHistoriqueFournisseurRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
public class GeoHistoriqueFournisseurGraphQLService
		extends GeoAbstractGraphQLService<GeoHistoriqueFournisseur, String> {

	public GeoHistoriqueFournisseurGraphQLService(GeoHistoriqueFournisseurRepository repository) {
		super(repository);
	}

	@GraphQLMutation
	public GeoHistoriqueFournisseur saveHistoriqueFournisseur(GeoHistoriqueFournisseur historiqueFournisseur) {
		return this.save(historiqueFournisseur);
	}

}