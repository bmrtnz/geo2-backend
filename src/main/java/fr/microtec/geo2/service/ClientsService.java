package fr.microtec.geo2.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.tiers.GeoClientRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service
public class ClientsService extends GeoAbstractGraphQLService<GeoClient, String> {

    @PersistenceContext
    private EntityManager entityManager;

    public ClientsService(
            GeoClientRepository clientsRepository) {
        super(clientsRepository, GeoClient.class);
    }

}
