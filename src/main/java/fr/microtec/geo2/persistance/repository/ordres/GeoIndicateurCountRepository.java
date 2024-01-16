package fr.microtec.geo2.persistance.repository.ordres;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class GeoIndicateurCountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BigDecimal countClientsDepassementEncours(
            String secteurCode,
            String societeCode,
            Boolean byUser) {
        return (BigDecimal) this.entityManager
                .createNamedQuery("Indicateur.countClientsDepassementEncours")
                .setParameter("arg_sco_code", secteurCode)
                .setParameter("arg_soc_code", societeCode)
                // .setParameter("arg_by_user", byUser)
                .getSingleResult();
    }

    public BigDecimal countOrdresNonConfirmes(
            String secteurCode,
            String societeCode) {
        return (BigDecimal) this.entityManager
                .createNamedQuery("Indicateur.countOrdresNonConfirmes")
                .setParameter("arg_sco_code", secteurCode)
                .setParameter("arg_soc_code", societeCode)
                .setParameter("arg_date_min", LocalDate.now().minusDays(1))
                .setParameter("arg_date_max", LocalDate.now())
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

    public BigDecimal countLitigeOuvert(
            String secteurCode,
            String societeCode) {
        return (BigDecimal) this.entityManager
                .createNamedQuery("Indicateur.countLitigeOuvert")
                .setParameter("arg_sco_code", secteurCode)
                .setParameter("arg_soc_code", societeCode)
                .getSingleResult();
    }

}
