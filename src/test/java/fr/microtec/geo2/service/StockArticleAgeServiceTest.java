package fr.microtec.geo2.service;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.configuration.PersistanceConfiguration;
import fr.microtec.geo2.persistance.entity.produits.GeoEmballage;
import fr.microtec.geo2.persistance.entity.produits.GeoOrigine;
import fr.microtec.geo2.persistance.entity.produits.GeoVariete;

@SpringBootTest(classes = { Geo2Application.class, PersistanceConfiguration.class })
public class StockArticleAgeServiceTest {

    @Autowired
    StockArticleAgeService service;

    static Stream<Arguments> testSubDistinctProvider() {
        return Stream.of(
                arguments("POIRE", GeoVariete.class),
                arguments("CH", GeoOrigine.class),
                arguments("P90", GeoEmballage.class));
    }

    @ParameterizedTest
    @MethodSource("testSubDistinctProvider")
    public <T> void testSubDistinct(String especeID, Class<T> clazz) {
        List<T> actual = service.subDistinct(especeID, clazz);

        Assertions.assertEquals(ArrayList.class, actual.getClass());
    }

}
