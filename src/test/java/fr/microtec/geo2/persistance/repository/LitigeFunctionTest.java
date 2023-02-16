package fr.microtec.geo2.persistance.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.litige.GeoFunctionLitigeRepository;

@SpringBootTest(classes = Geo2Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class LitigeFunctionTest {

    private static final String SOCIETE_SA = "SA";

    @Autowired
    private GeoFunctionLitigeRepository functionLitigeRepository;

    @Test
    public void testOfClotureLitigeClient() {
        FunctionResult result = this.functionLitigeRepository
                .ofClotureLitigeClient("004897", SOCIETE_SA, "", "", "");

        Assertions.assertNotNull(result);
    }

    @Test
    public void testOfClotureLitigeResponsable() {
        FunctionResult result = this.functionLitigeRepository
                .ofClotureLitigeResponsable("004897", SOCIETE_SA, "", "", "");

        Assertions.assertNotNull(result);
    }

    @Test
    public void testOfClotureLitigeGlobale() {
        FunctionResult result = this.functionLitigeRepository
                .ofClotureLitigeGlobale("004897", SOCIETE_SA, "", "", "", "");

        Assertions.assertNotNull(result);
    }

}
