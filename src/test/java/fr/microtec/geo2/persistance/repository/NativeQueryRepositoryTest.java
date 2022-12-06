package fr.microtec.geo2.persistance.repository;

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
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningDepartMaritime;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningTransporteur;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticle;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientDepassementEnCours;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientEdi;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientEnCours;
import fr.microtec.geo2.persistance.repository.ordres.GeoEdiOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.stock.GeoStockRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoClientRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;

@DataJpaTest
@Import(PersistanceTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NativeQueryRepositoryTest {

    @Autowired
    private GeoOrdreRepository ordreRepository;
    @Autowired
    private GeoStockRepository stockRepository;
    @Autowired
    private GeoEdiOrdreRepository geoEdiOrdreRepository;
    @Autowired
    private GeoClientRepository geoClientRepository;
    @Autowired
    private GeoEntrepotRepository entrepotRepository;

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
        List<GeoPlanningDepartMaritime> list = this.ordreRepository
                .allPlanningDepartMaritime(
                        "SA",
                        LocalDateTime.of(2021, 10, 25, 0, 0, 0),
                        LocalDateTime.of(2021, 10, 26, 23, 59, 59));

        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testAllLignesChargement() {
        this.ordreRepository
                .allLignesChargement("TES345/BWXD1C/TW49/22", "21");
    }

}
