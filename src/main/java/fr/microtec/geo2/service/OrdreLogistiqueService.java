package fr.microtec.geo2.service;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLogistique;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLogistiqueRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service()
public class OrdreLogistiqueService extends GeoAbstractGraphQLService<GeoOrdreLogistique, String> {

    public OrdreLogistiqueService(
            GeoOrdreLogistiqueRepository ordreLogistiqueRepository,
            EntityManager entityManager) {
        super(ordreLogistiqueRepository, GeoOrdreLogistique.class);
    }

}
