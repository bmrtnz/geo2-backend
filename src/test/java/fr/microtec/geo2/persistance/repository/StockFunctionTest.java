package fr.microtec.geo2.persistance.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.stock.GeoFunctionStockRepository;

@SpringBootTest(classes = Geo2Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class StockFunctionTest {

    @Autowired
    private GeoFunctionStockRepository functionStockRepository;

    @Test
    public void testRefreshStockHebdo() {
        FunctionResult result = this.functionStockRepository.refreshStockHebdo();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRes());
    }

}
