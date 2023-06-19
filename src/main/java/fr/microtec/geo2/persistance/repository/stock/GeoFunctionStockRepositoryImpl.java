package fr.microtec.geo2.persistance.repository.stock;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.function.AbstractFunctionsRepositoryImpl;

@Repository
public class GeoFunctionStockRepositoryImpl extends AbstractFunctionsRepositoryImpl
        implements GeoFunctionStockRepository {

    @Override
    public FunctionResult refreshStockHebdo() {
        return this.build("REFRESH_STOCK_HEBDO").fetch();
    }

}
