package fr.microtec.geo2.persistance.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import fr.microtec.geo2.configuration.PersistanceTestConfiguration;

@DataJpaTest
@Import(PersistanceTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IndicatorQueriesTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCountClientsDepassementEncours() {
        this.entityManager.getEntityManager()
                .createNamedQuery("Indicateur.countClientsDepassementEncours")
                .setParameter("arg_sco_code", "AFA")
                .setParameter("arg_soc_code", "SA")
                .getSingleResult();
    }

    @Test
    public void testCountOrdresNonConfirmes() {
        this.entityManager.getEntityManager()
                .createNamedQuery("Indicateur.countOrdresNonConfirmes")
                .setParameter("arg_sco_code", "AFA")
                .setParameter("arg_soc_code", "SA")
                .getSingleResult();
    }

    @Test
    public void testCountPlanningDepart() {
        this.entityManager.getEntityManager()
                .createNamedQuery("Indicateur.countPlanningDepart")
                .setParameter("arg_sco_code", "AFA")
                .setParameter("arg_soc_code", "SA")
                .getSingleResult();
    }

}
