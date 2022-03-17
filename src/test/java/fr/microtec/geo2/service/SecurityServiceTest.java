package fr.microtec.geo2.service;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.configuration.PersistanceConfiguration;
import fr.microtec.geo2.service.security.SecurityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(classes = { Geo2Application.class, PersistanceConfiguration.class })
public class SecurityServiceTest {

    static Stream<Arguments> testNormalizeLoginProvider() {
        return Stream.of(
            arguments("Françoise", "FRANCOISE"),
            arguments("Jean-Mïchel", "JEAN-MICHEL"),
            arguments("Céline", "CELINE"),
            arguments("microtec", "MICROTEC")
        );
    }

    @ParameterizedTest
    @MethodSource("testNormalizeLoginProvider")
    public void testNormalizeLogin(String login, String expecped) {
        String actual = SecurityService.normalizeLogin(login);

        Assertions.assertEquals(expecped, actual);
    }

}
