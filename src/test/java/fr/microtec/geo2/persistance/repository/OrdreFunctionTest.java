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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = { Geo2Application.class, PersistanceConfiguration.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
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
                "", "");

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getCursorData().isEmpty());

        List<GeoOrdreBaf> ordreBaf = result.getCursorDataAs(GeoOrdreBaf.class);
        Assertions.assertEquals(3, ordreBaf.size());
    }

    @Test
    public void testFVerifOrdreWarning() {
        FunctionResult result = this.functionOrdreRepository.fVerifOrdreWarning("1370744", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        System.out.println(result.getMsg());
    }

    @Test
    public void testFControleOrdreBaf() {
        FunctionResult result = this.functionOrdreRepository.fControlOrdreBaf("1370744", "SA");

        Assertions.assertNotNull(result);
        System.out.println(result.getMsg());
    }

    @Test
    public void testFControleOrdreBaf2() {
        String expectedMsg = "(A) Aucune confirmation n'a été effectuée\r\n" +
                "(S) %%%  un détail n'est pas clôturé\r\n" +
                "(P) Ligne=01 PU vente à zéro\r\n";

        FunctionResult result = this.functionOrdreRepository.fControlOrdreBaf("1429565", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertEquals(expectedMsg, result.getMsg());
        System.out.println(result.getMsg());
    }

    @Test
    public void testFAfficheBafControl() {
        FunctionResult result = this.functionOrdreRepository.fAfficheOrdreBaf(
                "SA", "F", "", "",
                LocalDate.of(2020, 2, 1),
                LocalDate.of(2020, 3, 1),
                "", "");

        List<GeoOrdreBaf> ordresBaf = result.getCursorDataAs(GeoOrdreBaf.class);
        for (GeoOrdreBaf baf : ordresBaf) {
            FunctionResult controlResult = this.functionOrdreRepository.fControlOrdreBaf(baf.getOrdreRef(), "SA");

            baf.setControlData(controlResult.getData());
        }

        Assertions.assertNotNull(ordresBaf);
    }

    @Test
    public void testFNouvelOrdre() {
        FunctionResult result = this.functionOrdreRepository.fNouvelOrdre("SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertFalse(result.getData().isEmpty());
        Assertions.assertNotNull(result.getData().get("ls_nordre"));
    }

    @Test
    public void testFNouvelOrdreInvalid() {
        FunctionResult result = this.functionOrdreRepository.fNouvelOrdre("TRUC");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes());
        Assertions.assertEquals("%%% f_nouvel_ordre : société TRUC inconnue", result.getMsg());
    }

    @Test
    public void testFGenereDluo() {
        FunctionResult result = this.functionOrdreRepository
                .fGenereDluo("%DEMdd%", LocalDate.parse("2020-07-16"), LocalDate.parse("2020-07-17"));

        Assertions.assertNotNull(result.getData().get("arg_dluo"));
    }

    @Test
    public void testOfInitArtrefGrp() {
        FunctionResult result = this.functionOrdreRepository
                .ofInitArtrefGrp("002068");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOfInitArticle() {
        FunctionResult result = this.functionOrdreRepository
                .ofInitArticle("1434640", "046353", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOfInitArticleWithAssociated() {
        FunctionResult result = this.functionOrdreRepository
                .ofInitArticle("000922", "028514", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testfInitBlocageOrdreWithGeoClient2() {
        FunctionResult result = this.functionOrdreRepository
                .fInitBlocageOrdre("1434640", "LINO");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertEquals(false, result.getData().get("bloquer"));
    }

    @Test
    public void testfInitBlocageOrdreTypeRPF() {
        FunctionResult result = this.functionOrdreRepository
                .fInitBlocageOrdre("1503751", "VEGA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertEquals(true, result.getData().get("bloquer"));
    }

    @Test
    public void testfInitBlocageOrdreWithUnknownOrdreType() {
        FunctionResult result = this.functionOrdreRepository
                .fInitBlocageOrdre("1674878", "ADRIEN");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertEquals(false, result.getData().get("bloquer"));
    }

    @Test
    public void testOfVerifLogistiqueDepart() {
        FunctionResult result = this.functionOrdreRepository
                .ofVerifLogistiqueDepart("001822");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOfRepartitionPalette() {
        FunctionResult result = this.functionOrdreRepository
                .ofRepartitionPalette("002075", "F", "ADRIEN");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOfCalculRegimeTvaEncours() {
        FunctionResult result = this.functionOrdreRepository
                .ofCalculRegimeTvaEncours("000211", "");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOfSauveOrdre() {
        FunctionResult result = this.functionOrdreRepository
                .ofSauveOrdre("000927");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangeCdeNbPalWithSecteurFrance() {
        FunctionResult result = this.functionOrdreRepository
                .onChangeCdeNbPal("9714FC", "F");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangeDemipalInd() {
        FunctionResult result = this.functionOrdreRepository
                .onChangeDemipalInd("9714FC", "ADRIEN");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangePalNbCol() {
        FunctionResult result = this.functionOrdreRepository
                .onChangePalNbCol("9714FC", "ADRIEN");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangeCdeNbCol() {
        FunctionResult result = this.functionOrdreRepository
                .onChangeCdeNbCol("9714FC", "ADRIEN");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangeProprCode() {
        FunctionResult result = this.functionOrdreRepository
                .onChangeProprCode("004962", "ADRIEN", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangeFouCode() {
        FunctionResult result = this.functionOrdreRepository
                .onChangeFouCode("9F31AC", "ADRIEN", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes());
    }

    @Test
    public void testOnChangeVtePu() {
        FunctionResult result = this.functionOrdreRepository
                .onChangeVtePu("003098");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangePalCode() {
        FunctionResult result = this.functionOrdreRepository
                .onChangePalCode("9F28EC", "ADRIEN", "SA");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangePalinterCode() {
        FunctionResult result = this.functionOrdreRepository
                .onChangePalinterCode("003098");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }
}
