package fr.microtec.geo2.persistance.repository.ordres;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class GeoIndicateurCountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BigDecimal countClientsDepassementEncours() {
        return (BigDecimal) this.entityManager
                .createNamedQuery("Indicateur.countClientsDepassementEncours")
                .getSingleResult();
    }
}
