package fr.microtec.geo2.persistance.repository;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.configuration.PersistanceConfiguration;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@ContextConfiguration(classes = { Geo2Application.class, PersistanceConfiguration.class })
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrdreFunctionTest {

    @Autowired
    private GeoFunctionOrdreRepository functionOrdreRepository;

    @Test
    public void testOfValideEntrepotForOrdreClientNotValid() {
        FunctionResult result = this.functionOrdreRepository.ofValideEntrepotForOrdre("002701");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes());
        Assertions.assertEquals("Le client n'est plus valide. Création de l'ordre annulée.", result.getMsg());
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testOfValideEntrepotForOrdreValid() {
        FunctionResult result = this.functionOrdreRepository.ofValideEntrepotForOrdre("011809");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertEquals("OK", result.getMsg());
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFCalculMarge() {
        FunctionResult result = this.functionOrdreRepository.fCalculMarge("1218222");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertEquals("OK", result.getMsg());
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFRecupFraisValid() {
        FunctionResult result = this.functionOrdreRepository.fRecupFrais("ENVY", "%", "%", "F", 0, "F");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(44, result.getRes(), result.getMsg());
        Assertions.assertTrue(StringUtils.isEmpty(result.getMsg()));
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFRecupFraisNotFound() {
        FunctionResult result = this.functionOrdreRepository.fRecupFrais("TRUC", "TRUC", "TRUC", "TRUC", -1, "TRUC");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes(), result.getMsg());
        Assertions.assertTrue(StringUtils.isEmpty(result.getMsg()));
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFCalculPerequationSecteurCommercialIndustrie() {
        FunctionResult result = this.functionOrdreRepository.fCalculPerequation("001061", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertTrue(StringUtils.isEmpty(result.getMsg()));
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFCalculPerequation() {
        FunctionResult result = this.functionOrdreRepository.fCalculPerequation("1218222", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertTrue(StringUtils.isEmpty(result.getMsg()));
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFAfficheOrdreBaf() {
        FunctionResult result = this.functionOrdreRepository.fAfficheOrdreBaf(
                "SA", "F", "", "",
                LocalDate.of(2020, 2, 1),
                LocalDate.of(2020, 3, 1),
                "", ""
        );

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getCursorData().isEmpty());

        List<GeoOrdreBaf> ordreBaf = result.getCursorDataAs(GeoOrdreBaf.class);
        Assertions.assertEquals(3, ordreBaf.size());
    }

    @Test
    public void testFControleOrdreBaf() {

    }

    @Test
    public void testFNouvelOrdre() {
        FunctionResult result = this.functionOrdreRepository.fNouvelOrdre("SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertFalse(result.getData().isEmpty());
        Assertions.assertNotNull(result.getData().get("ll_nordre"));
    }

    @Test
    public void testFNouvelOrdreInvalid() {
        FunctionResult result = this.functionOrdreRepository.fNouvelOrdre("TRUC");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes());
        Assertions.assertEquals("%%% f_nouvel_ordre : société TRUC inconnue", result.getMsg());
    }

}
