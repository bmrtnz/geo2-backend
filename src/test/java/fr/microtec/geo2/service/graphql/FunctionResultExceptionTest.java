package fr.microtec.geo2.service.graphql;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.configuration.PersistanceConfiguration;
import fr.microtec.geo2.persistance.repository.common.GeoUtilisateurRepository;
import fr.microtec.geo2.service.graphql.ordres.GeoFunctionsOrdreGraphQLService;
import graphql.GraphQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * This test file is for testing Aspect around GeoFunctionXXGraphQLService.
 * This do throw GraphQLException when FunctionResult.res isn't equals to 1.
 */
@WithMockUser(username = "STEPHANE", password = "geo2", roles = "USER")
@SpringBootTest(classes = { Geo2Application.class, PersistanceConfiguration.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class FunctionResultExceptionTest {

    @Autowired
    private GeoFunctionsOrdreGraphQLService geoFunctionsOrdreGraphQLService;

    @Test
    public void testThrowEception() {
        Assertions.assertThrows(GraphQLException.class, () -> this.geoFunctionsOrdreGraphQLService.ofValideEntrepotForOrdre("002701"));
    }

    @Test
    public void testDoNotThrowEception() {
        Assertions.assertDoesNotThrow(() -> this.geoFunctionsOrdreGraphQLService.ofValideEntrepotForOrdre("011809"));
    }

}
