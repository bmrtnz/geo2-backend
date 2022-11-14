package fr.microtec.geo2.persistance.repository.ordres;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class GeoIndicateurCountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BigDecimal countClientsDepassementEncours(
            String secteurCode,
            String societeCode) {
        return (BigDecimal) this.entityManager
                .createNamedQuery("Indicateur.countClientsDepassementEncours")
                .setParameter("arg_sco_code", secteurCode)
                .setParameter("arg_soc_code", societeCode)
                .getSingleResult();
    }

    public BigDecimal countOrdresNonConfirmes(
            String secteurCode,
            String societeCode) {
        return (BigDecimal) this.entityManager
                .createNamedQuery("Indicateur.countOrdresNonConfirmes")
                .setParameter("arg_sco_code", secteurCode)
                .setParameter("arg_soc_code", societeCode)
                .getSingleResult();
    }

    public BigDecimal countPlanningDepart(
            String secteurCode,
            String societeCode) {
        return (BigDecimal) this.entityManager
                .createNamedQuery("Indicateur.countPlanningDepart")
                .setParameter("arg_sco_code", secteurCode)
                .setParameter("arg_soc_code", societeCode)
                .getSingleResult();
    }

}
