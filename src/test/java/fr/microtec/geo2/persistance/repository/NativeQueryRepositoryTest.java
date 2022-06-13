package fr.microtec.geo2.persistance.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningTransporteur;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticle;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.stock.GeoStockRepository;

@DataJpaTest
@ContextConfiguration(classes = Geo2Application.class)
@EnableJpaRepositories(repositoryBaseClass = GeoCustomRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NativeQueryRepositoryTest {

    @Autowired
    private GeoOrdreRepository ordreRepository;
    @Autowired
    private GeoStockRepository stockRepository;

    @Test
    public void testAllPlanningTransporteurs() {
        List<GeoPlanningTransporteur> list = this.ordreRepository.allPlanningTransporteurs(
                LocalDateTime.of(2020, 8, 1, 0, 0, 0),
                LocalDateTime.of(2020, 8, 3, 23, 59, 59),
                "SA",
                "VERAY");

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

}
