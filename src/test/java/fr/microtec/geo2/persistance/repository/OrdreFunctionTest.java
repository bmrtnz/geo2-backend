package fr.microtec.geo2.persistance.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;

@SpringBootTest(classes = Geo2Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class OrdreFunctionTest {

    private static final String SOCIETE_SA = "SA";

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
        FunctionResult result = this.functionOrdreRepository.fRecupFrais("REGACA", "A", "F", "F", 2, "F");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(144, result.getRes(), result.getMsg());
    }

    @Test
    public void testFRecupFraisNotFound() {
        FunctionResult result = this.functionOrdreRepository.fRecupFrais("TRUC", "TRUC", "TRUC", "TRUC", -1, "TRUC");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes(), result.getMsg());
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFCalculPerequationSecteurCommercialIndustrie() {
        FunctionResult result = this.functionOrdreRepository.fCalculPerequation("001061", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertTrue(StringUtils.isEmpty(result.getMsg()));
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFCalculPerequation() {
        FunctionResult result = this.functionOrdreRepository.fCalculPerequation("1218222", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testFCalculMargePrevi() {
        FunctionResult result = this.functionOrdreRepository
                .fCalculMargePrevi("1218222", SOCIETE_SA);

        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertNotNull(result.getData().get("result"));
    }

    @Test
    public void testFAfficheOrdreBaf() {
        FunctionResult result = this.functionOrdreRepository.fAfficheOrdreBaf(
                SOCIETE_SA, "F", "", "",
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
        FunctionResult result = this.functionOrdreRepository.fVerifOrdreWarning("1370744", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        System.out.println(result.getMsg());
    }

    @Test
    public void testFControleOrdreBaf() {
        FunctionResult result = this.functionOrdreRepository.fControlOrdreBaf("1370744", SOCIETE_SA);

        Assertions.assertNotNull(result);
        System.out.println(result.getMsg());
    }

    @Test
    public void testFControleOrdreBaf2() {
        FunctionResult result = this.functionOrdreRepository.fControlOrdreBaf("1429565", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertTrue(result.getMsg().startsWith("(A) Aucune confirmation n'a été effectuée"));
        System.out.println(result.getMsg());
    }

    @Test
    public void testFAfficheBafControl() {
        FunctionResult result = this.functionOrdreRepository.fAfficheOrdreBaf(
                SOCIETE_SA, "F", "", "",
                LocalDate.of(2020, 2, 1),
                LocalDate.of(2020, 3, 1),
                "", "");

        List<GeoOrdreBaf> ordresBaf = result.getCursorDataAs(GeoOrdreBaf.class);
        Assertions.assertNotNull(ordresBaf);
    }

    @Test
    public void testFNouvelOrdre() {
        FunctionResult result = this.functionOrdreRepository.fNouvelOrdre(SOCIETE_SA);

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
    public void testFGenereDluoExplicit() {
        FunctionResult result = this.functionOrdreRepository
                .fGenereDluo("16/07/20", LocalDate.parse("2020-07-16"), LocalDate.parse("2020-07-17"));

        Assertions.assertEquals("16/07/20", result.getData().get("arg_dluo"));
    }

    @Test
    public void testFGenereDluoCasino() {
        FunctionResult result = this.functionOrdreRepository
                .fGenereDluo("%DEdd/mm/yy%", LocalDate.parse("2020-07-16"), LocalDate.parse("2020-07-17"));

        Assertions.assertEquals("16/07/20", result.getData().get("arg_dluo"));
    }

    @Test
    public void testFGenereDluoScafruit() {
        FunctionResult result = this.functionOrdreRepository
                .fGenereDluo("%DLddmm%", LocalDate.parse("2020-07-16"), LocalDate.parse("2020-07-17"));

        Assertions.assertEquals("1707", result.getData().get("arg_dluo"));
    }

    @Test
    public void testFGenereDluoCarrefour() {
        FunctionResult result = this.functionOrdreRepository
                .fGenereDluo("%DLMdd%", LocalDate.parse("2020-07-16"), LocalDate.parse("2020-07-17"));

        Assertions.assertEquals("G17", result.getData().get("arg_dluo"));
    }

    @Test
    public void testOfInitArtrefGrp() {
        FunctionResult result = this.functionOrdreRepository
                .ofInitArtrefGrp("002068");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    // On ne test pas l'insertion parcequ'elle finira par échouer aprés 100 lignes
    @Test
    public void testOfInitArticleUpdate() {
        FunctionResult result = this.functionOrdreRepository
                .ofInitArticle("001088", "087187", SOCIETE_SA, "002002");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOfInitArticleWithAssociated() {
        FunctionResult result = this.functionOrdreRepository
                .ofInitArticle("000922", "028514", SOCIETE_SA, null);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData().get("art_ass"));
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
    public void testFResaUneLigne() {
        FunctionResult result = this.functionOrdreRepository
                .fResaUneLigne(
                        "STANOR",
                        "STANOR",
                        "094821",
                        "BRUNO",
                        2,
                        "1976111",
                        "8B2A3D",
                        "[arg_desc]",
                        "P17");

        Assertions.assertNotNull(result);
    }

    @Test
    public void testFResaAutoOrdre() {
        FunctionResult result = this.functionOrdreRepository
                .fResaAutoOrdre("1976111", "BRUNO");

        Assertions.assertNotNull(result);
    }

    @Test
    public void testFGetInfoResa() {
        FunctionResult result = this.functionOrdreRepository
                .fGetInfoResa("F2230C");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertFalse(result.getData().isEmpty());
    }

    @Test
    public void testOfSauveOrdre() {
        FunctionResult result = this.functionOrdreRepository
                .ofSauveOrdre("000927");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testFVerifLogistiqueOrdre() {
        FunctionResult result = this.functionOrdreRepository
                .fVerifLogistiqueOrdre("000211");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testFAjoutOrdLog() {
        FunctionResult result = this.functionOrdreRepository
                .fAjoutOrdlog("B2894A", "G", "MATHSA");

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGeoPrepareEnvoisOrdre() {
        FunctionResult result = this.functionOrdreRepository
                .geoPrepareEnvois("1675112", "ORDRE", 'O', 'N', "BRUNO");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
        Assertions.assertFalse(result.getCursorData().isEmpty());
    }

    @Test
    public void testOfAREnvois() {
        FunctionResult result = this.functionOrdreRepository
                .ofAREnvois("1675112");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testOnChangeAchDevPu() {
        FunctionResult result = this.functionOrdreRepository
                .onChangeAchDevPu("9F291C", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testFDetailsExpOnClickAuto() {
        FunctionResult result = this.functionOrdreRepository
                .fDetailsExpOnClickAuto("9F1F7C");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testFChgtQteArtRet() {
        FunctionResult result = this.functionOrdreRepository
                .fChgtQteArtRet("001819");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testFDetailsExpClickModifier() {
        FunctionResult result = this.functionOrdreRepository
                .fDetailsExpClickModifier("1058713", "A8E75B", "1075");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

    @Test
    public void testFConfirmationCommande() {
        FunctionResult result = this.functionOrdreRepository
                .fConfirmationCommande("1150382", SOCIETE_SA, "STEPHANE");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getRes());
    }

    @Test
    public void testFDocumentEnvoiDetailsExp() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiDetailsExp("000895", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiConfirmationPrixAchat() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiConfirmationPrixAchat("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiFichesPalette() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiFichesPalette("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiGenereTraca() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiGenereTraca("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiAfficheCMR() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiAfficheCMR("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiBonLivraison() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiBonLivraison("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiProforma() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiProforma("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiCominv() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiCominv("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiShipmentBuyco() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiShipmentBuyco("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDocumentEnvoiFactureDouaniere() {
        FunctionResult result = this.functionOrdreRepository
                .fDocumentEnvoiFactureDouaniere("000895");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFClotureLogGrp() {
        FunctionResult result = this.functionOrdreRepository
                .fClotureLogGrp("002615", "QUESOL", 'O');

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFSetDetailKitArticle() {
        FunctionResult result = this.functionOrdreRepository
                .fSetDetailKitArticle("002615", "QUESOL");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFActualiseNbPalettesSol() {
        FunctionResult result = this.functionOrdreRepository
                .fActualiseNbPalettesSol("002615", "QUESOL");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFGetQttPerBta() {
        FunctionResult result = this.functionOrdreRepository
                .fGetQttPerBta("004484", "CAMION", 10d, 50d, 500d);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertFalse(result.getData().isEmpty());
        Assertions.assertNotNull(result.getData().get("ld_qte"));
    }

    @Test
    public void tesFSubmitEnvoiDetailSeccom() {
        FunctionResult result = this.functionOrdreRepository
                .fSubmitEnvoiDetailSeccom("000927", "CONFOU", "unit_test");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void tesFDetailEnteteSauve() {
        FunctionResult result = this.functionOrdreRepository
                .fDetailEnteteSauve("000503", "B_CLOTURER", 'O');

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void tesFTracabiliteDecloturer() {
        FunctionResult result = this.functionOrdreRepository
                .fTracabiliteCloturer("001864", 'O');

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void tesFDetailsExpOnCheckCloturer() {
        FunctionResult result = this.functionOrdreRepository
                .fDetailsExpOnCheckCloturer("001977", "001", "BRUNO", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFBonAFacturerPrepareDejaFacture() {
        FunctionResult result = this.functionOrdreRepository.fBonAFacturerPrepare("001864", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes(), result.getMsg());
        Assertions.assertTrue(result.getMsg().contains("l'ordre est déja bon à facturer"));
    }

    @Test
    public void testFBonAFacturerDejaFacture() {
        FunctionResult result = this.functionOrdreRepository.fBonAFacturer("001864", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes(), result.getMsg());
        Assertions.assertEquals("problème technique validation bon à facturer : l'ordre est déja bon à facturer !",
                result.getMsg(), result.getMsg());
    }

    @Test
    public void testFSuppressionOrdreAvecEnvoie() {
        FunctionResult result = this.functionOrdreRepository.fSuppressionOrdre("001864", "STEPHANE", "Un commentaire");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes(), result.getMsg());
        Assertions.assertTrue(result.getMsg().contains("Impossible de supprimer l'ordre car des flux ont été générés"),
                result.getMsg());
    }

    @Test
    public void testFTestAnnuleOrdreDejaCloture() {
        FunctionResult result = this.functionOrdreRepository.fTestAnnuleOrdre("001864");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRes(), result.getMsg());
        Assertions.assertTrue(result.getMsg().contains("ordre 092000 détail(s) déjà clôturé(s)"), result.getMsg());
    }

    @Test
    public void testFTestAnnulationOrdreDejaCloture() {
        FunctionResult result = this.functionOrdreRepository.fAnnulationOrdre("001864", "BW");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFCreeOrdreComplementaireOrdreV2() {
        FunctionResult result = this.functionOrdreRepository.fCreeOrdreComplementaire("1434967", SOCIETE_SA,
                "STEPHANE");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertNotNull(result.getData().get("ls_ord_ref_compl"));
    }

    @Test
    public void testFCreeOrdreComplementaireOrdreV3() {
        FunctionResult result = this.functionOrdreRepository.fCreeOrdreComplementaire("1972625", SOCIETE_SA,
                "STEPHANE");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertNotNull(result.getData().get("ls_ord_ref_compl"));
    }

    @Test
    @Disabled("This test fail after running many time, need reset GEO_ORDRE.LIST_NORDRE_REGUL field")
    public void testFCreeOrdreRegularisation() {
        String[] listOrlRef = new String[] { "9CA9FB", "9CAA0B", "9CAA2B" };
        FunctionResult result = this.functionOrdreRepository.fCreeOrdreRegularisation(
                "1038117", SOCIETE_SA, "F28", "RPR", true, "STEPHANE", listOrlRef);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertNotNull(result.getData().get("ls_ord_ref_regul"));
    }

    @Test
    public void testFCreeOrdreEdi() {
        FunctionResult result = this.functionOrdreRepository.fCreateOrdresEdi(
                "27344",
                "23",
                SOCIETE_SA,
                "000446",
                "015887",
                "70055655",
                "2023-01-07",
                "BRUNO");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        // Assertions.assertNotNull(result.getData().get("ls_nordre_tot"));
    }

    @Test
    public void testWDupliqueOrdreOnDuplique() {
        FunctionResult result = this.functionOrdreRepository.wDupliqueOrdreOnDuplique(
                "002207",
                "BRUNO",
                SOCIETE_SA,
                "013128",
                LocalDateTime.of(2022, 5, 10, 8, 30, 10),
                LocalDate.of(2022, 5, 12),
                false,
                true,
                false,
                true,
                false,
                true,
                false,
                true,
                false,
                true);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertNotNull(result.getData().get("nordre"));
    }

    @Test
    public void testOfInitRegimeTva() {
        FunctionResult result = this.functionOrdreRepository
                .ofInitRegimeTva("001675", "L");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testSetTransporteurBassin() {
        FunctionResult result = this.functionOrdreRepository
                .setTransporteurBassin("8B8E8D");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDecomptePalox() {
        FunctionResult result = this.functionOrdreRepository.fDecomptePalox(
                1l,
                "COURTO",
                "PA120",
                "000141",
                "EMBALL",
                LocalDate.now(),
                SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFAjustPallox() {
        FunctionResult result = this.functionOrdreRepository
                .fAjustPallox(
                        SOCIETE_SA,
                        "DESFRUTAPAL",
                        "BURATTI",
                        "PAPL120",
                        "POMME",
                        1,
                        LocalDate.now(),
                        "YO",
                        "DESFRUTAPAL");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFCreeOrdreV4() {
        FunctionResult result = this.functionOrdreRepository.fCreateOrdreV4(
                SOCIETE_SA,
                "000989",
                "002681",
                "CHABAS",
                "[REF_CLI]",
                false,
                false,
                LocalDateTime.now(),
                "ORD",
                LocalDateTime.now().plusDays(1),
                "[LOAD_REF]");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFCreateLigneOrdre() {
        FunctionResult result = this.functionOrdreRepository.fCreateLigneOrdre(
                "001304",
                "000001",
                "APIFOO",
                "002702",
                20d,
                2d,
                40d,
                2d,
                1.89,
                "ORCHARD",
                "46QSD");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFnMajOrdreRegroupementV2() {
        FunctionResult result = this.functionOrdreRepository.fnMajOrdreRegroupementV2(
                "1139463",
                "SA",
                true,
                "BRUNO");

        Assertions.assertNotNull(result);
    }

    @Test
    public void testFEnvoiBLAuto() {
        FunctionResult result = this.functionOrdreRepository.fEnvoiBLAuto(
                "SA",
                "F",
                LocalDate.of(2022, 12, 26),
                LocalDate.of(2022, 12, 27),
                "BRUNO");

        Assertions.assertNotNull(result);
    }

    @Test
    public void testFTakeOptionStock() {
        FunctionResult result = this.functionOrdreRepository
                .fTakeOptionStock(1, "943317", "DABISSECDV", "P17", "YO");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDuplicationBukSa() {
        FunctionResult result = this.functionOrdreRepository
                .fDuplicationBukSa("1550218", "BUK", "BRUNO", "FERRYFAST");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getRes(), result.getMsg());
    }

    @Test
    public void testFDelRegroupement() {
        FunctionResult result = this.functionOrdreRepository.fDelRegroupement("1550218");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFCreeOrdreReplacement() {
        FunctionResult result = this.functionOrdreRepository
                .fCreeOrdreReplacement("1685592", "007683", "BRUNO", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFCreeOrdreReplacementLigne() {
        FunctionResult resultOR = this.functionOrdreRepository
                .fCreeOrdreReplacement("1685592", "007683", "BRUNO", SOCIETE_SA);
        String ordre_replacement = resultOR.getData().get("ls_ord_ref_replace").toString();
        FunctionResult result = this.functionOrdreRepository
                .fCreeOrdreReplacementLigne("199933", ordre_replacement, "1631127", "C64A2C", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFCreeOrdreReedFact() {
        FunctionResult result = this.functionOrdreRepository
                .fCreeOrdreReedFact("1685592", SOCIETE_SA, "BRUNO");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFCreeOrdreReedFactLigne() {
        FunctionResult result = this.functionOrdreRepository
                .fCreeOrdreReedFactLigne("1685618", "1686253", SOCIETE_SA);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFReturnForfaitsTrp() {
        FunctionResult result = this.functionOrdreRepository
                .fReturnForfaitsTrp("000915", "CIF", "ORD");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testPrcGenFraisDedimp() {
        FunctionResult result = this.functionOrdreRepository
                .prcGenFraisDedimp("000589");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testOnChangeTrpDevCode() {
        FunctionResult result = this.functionOrdreRepository
                .onChangeTrpDevCode("2091208", "EUR", "BUK", 0.58F);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
    }

    @Test
    public void testFCreatePreordre() {
        FunctionResult result = this.functionOrdreRepository.fCreatePreordre(
                SOCIETE_SA,
                "000115",
                "016121",
                "LUNDE",
                "CDE 666",
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                "hello",
                "AJ",
                "SJ");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes(), result.getMsg());
        Assertions.assertFalse(result.getData().isEmpty());
    }

    @Test
    public void testFCreateLignePreordre() {
        FunctionResult result = this.functionOrdreRepository.fCreateLignePreordre(
                "2091363",
                "016121",
                100d,
                10d,
                10d,
                "-",
                0d,
                "076058",
                "BWINDEMNISAT",
                "BWINDEMNISAT",
                1.2d,
                1.1d,
                "COLIS",
                "COLIS");

        // On teste juste l'execution,
        // parce qu'on arrivera à terme au nombre maximal de lignes
        Assertions.assertNotNull(result);
    }

    @Test
    public void testWAjoutArtRecapEdiColibri() {
        FunctionResult result = this.functionOrdreRepository.wAjoutArtRecapEdiColibri(
                "050502",
                "BIOTROPIC",
                "BIOTROPIC",
                1,
                null);

        // On teste juste l'execution,
        Assertions.assertNotNull(result);
    }

    @Test
    public void testOfReadOrdEdiColibri() {
        FunctionResult result = this.functionOrdreRepository.ofReadOrdEdiColibri(
                new BigDecimal(16064),
                "23",
                'D');

        // On teste juste l'execution,
        Assertions.assertNotNull(result);
    }

    @Test
    public void testOfControleSelArt() {
        FunctionResult result = this.functionOrdreRepository.ofControleSelArt(
                new BigDecimal(16064),
                "23");

        // On teste juste l'execution,
        Assertions.assertNotNull(result);
    }

    @Test
    public void testOfControleQteArt() {
        FunctionResult result = this.functionOrdreRepository.ofControleQteArt(
                new BigDecimal(16064),
                "23");

        // On teste juste l'execution,
        Assertions.assertNotNull(result);
    }

    @Test
    public void testClearTraca() {
        FunctionResult result = this.functionOrdreRepository.clearTraca("9F1F4C");

        // On teste juste l'execution,
        Assertions.assertNotNull(result);
    }

    @Test
    public void testFCreateEdiEsp() {
        FunctionResult result = this.functionOrdreRepository.fCreateEdiEsp(
                BigDecimal.valueOf(15995),
                SOCIETE_SA,
                "000448",
                "-",
                "BRUNO");

        // On teste juste l'execution,
        Assertions.assertNotNull(result);
    }

    @Test
    public void testSupprLignesNonExped() {
        FunctionResult result = this.functionOrdreRepository.supprLignesNonExped("2200197");

        // On teste juste l'execution,
        Assertions.assertNotNull(result);
    }

}
