package fr.microtec.geo2.persistance.repository.ordres;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class GeoIndicateurCountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public long countClientsDepassementEncours(
            String secteurCode,
            String societeCode) {
        return (long) this.entityManager
                .createNamedQuery("Indicateur.countClientsDepassementEncours")
                .setParameter("arg_sco_code", secteurCode)
                .setParameter("arg_soc_code", societeCode)
                .getSingleResult();
    }
}
