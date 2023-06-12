package fr.microtec.geo2.persistance.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import fr.microtec.geo2.configuration.PersistanceTestConfiguration;
import fr.microtec.geo2.persistance.entity.ordres.GeoCommandeEdi;
import fr.microtec.geo2.persistance.entity.ordres.GeoDeclarationFraude;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningDepart;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningMaritime;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningTransporteur;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticle;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientDepassementEnCours;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientEdi;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientEnCours;
import fr.microtec.geo2.persistance.repository.litige.GeoLitigeLigneRepository;
import fr.microtec.geo2.persistance.repository.litige.GeoLitigeRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoEdiOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoLigneChargementRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.stock.GeoPrecalModelRepository;
import fr.microtec.geo2.persistance.repository.stock.GeoStockRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoClientRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoPaysRepository;

@DataJpaTest
@Import(PersistanceTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NativeQueryRepositoryTest {

    @Autowired
    private GeoOrdreRepository ordreRepository;
    @Autowired
    private GeoOrdreLigneRepository ordreLigneRepository;
    @Autowired
    private GeoLigneChargementRepository ligneChargementRepository;
    @Autowired
    private GeoStockRepository stockRepository;
    @Autowired
    private GeoEdiOrdreRepository geoEdiOrdreRepository;
    @Autowired
    private GeoClientRepository geoClientRepository;
    @Autowired
    private GeoEntrepotRepository entrepotRepository;
    @Autowired
    private GeoLitigeRepository litigeRepository;
    @Autowired
    private GeoLitigeLigneRepository litigeLigneRepository;
    @Autowired
    private GeoPaysRepository paysRepository;
    @Autowired
    private GeoPrecalModelRepository precalModelRepository;

    @Test
    public void testAllPlanningTransporteurs() {
        List<GeoPlanningTransporteur> list = this.ordreRepository.allPlanningTransporteurs(
                LocalDateTime.of(2021, 10, 25, 0, 0, 0),
                LocalDateTime.of(2021, 10, 26, 23, 59, 59),
                "SA",
                "TRANSI");

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllStockArticle() {
        List<GeoStockArticle> list = this.stockRepository
                .allStockArticleList(
                        "POMME",
                        "%",
                        "%",
                        "%",
                        "%",
                        "%");

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllStockReservation() {
        this.stockRepository
                .allStockReservationList("002021");
    }

    @Test
    public void testAllLigneReservation() {
        this.stockRepository
                .allLigneReservationList("9F2FDC");
    }

    @Test
    public void testAllCommandeEdi() {
        List<GeoCommandeEdi> list = this.geoEdiOrdreRepository.allCommandeEdi("F", "%", "%",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2021, 1, 1, 23, 59, 59),
                "%", "%", "%", "TINA");

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllClientEdi() {
        List<GeoClientEdi> list = this.geoEdiOrdreRepository
                .allClientEdi("F", "%", "%");

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllClientEnCours() {
        List<GeoClientEnCours> list = this.geoClientRepository
                .allClientEnCours("000150", "EUR");

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllClientDepassementEnCours() {
        List<GeoClientDepassementEnCours> list = this.geoClientRepository
                .allClientDepassementEnCours("AFA", "SA");

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllMouvementEntrepot() {
        this.entrepotRepository
                .allMouvementEntrepot(LocalDateTime.now(), "SA", "004874", null, null);
    }

    @Test
    public void testAllRecapitulatifFournisseur() {
        this.entrepotRepository
                .allRecapitulatifFournisseur(LocalDateTime.now(), "SA", "004874", null);
    }

    @Test
    public void testAllPlanningDepartMaritime() {
        List<GeoPlanningMaritime> list = this.ordreRepository
                .allPlanningDepartMaritime(
                        "SA",
                        LocalDateTime.of(2021, 10, 25, 0, 0, 0),
                        LocalDateTime.of(2021, 10, 26, 23, 59, 59));

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllPlanningArriveMaritime() {
        List<GeoPlanningMaritime> list = this.ordreRepository
                .allPlanningArriveMaritime(
                        "SA",
                        LocalDateTime.of(2021, 10, 25, 0, 0, 0),
                        LocalDateTime.of(2021, 10, 26, 23, 59, 59));

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllLignesChargement() {
        this.ligneChargementRepository
                .allLignesChargement("TES345/BWXD1C/TW49/22", "21");
    }

    @Test
    public void testWLitigePickOrdreOrdligV2() {
        this.ordreLigneRepository.wLitigePickOrdreOrdligV2("1504433");
    }

    @Test
    public void testAllLitigeAPayer() {
        this.litigeRepository.allLitigeAPayer("004915");
    }

    @Test
    public void testAllLitigeLigneFait() {
        this.litigeLigneRepository.allLitigeLigneFait("119517", "01");
    }

    @Test
    public void testAllSupervisionLitige() {
        this.litigeRepository.allSupervisionLitige("A", "MAR");
    }

    @Test
    public void testAllPaysDepassementEnCours() {
        this.paysRepository
                .allPaysDepassement('O', "F", "SA", "BV");
    }

    @Test
    public void testAllPlanningDepart() {
        List<GeoPlanningDepart> list = this.ordreRepository
                .allPlanningDepart(
                        "SA",
                        "F",
                        LocalDateTime.of(2022, 10, 25, 0, 0, 0),
                        LocalDateTime.of(2022, 10, 26, 23, 59, 59));

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllLitigeLigneForfait() {
        this.litigeLigneRepository.allLitigeLigneForfait("138306");
    }

    @Test
    public void testGenNumLot() {
        this.litigeRepository.genNumLot("138306");
    }

    @Test
    public void testCountCauseConseq() {
        this.litigeRepository.countCauseConseq("2089876");
    }

    @Test
    public void testCountLinkedOrders() {
        this.litigeRepository.countLinkedOrders("2089876");
    }

    @Test
    public void testAllDeclarationFraude() {
        List<GeoDeclarationFraude> result = this.ordreRepository.allDeclarationFraude(
                "F",
                "SA",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 2),
                // On evite le drame grace à la nano-seconde 💣
                LocalDateTime.of(2023, 1, 1, 0, 0, 0, 1),
                "007728",
                "%",
                "%",
                "%",
                "%");
        assert (!result.isEmpty());
    }

    @Test
    public void testAllStockPreca() {
        this.stockRepository.allPreca(
                "2323",
                "ARIANE",
                "MARTINOISE",
                "POMME");
    }

    @Test
    public void testAllPrecaEspece() {
        this.precalModelRepository.allPrecaEspece();
    }

}
