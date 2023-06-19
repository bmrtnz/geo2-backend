package fr.microtec.geo2.persistance.repository.stock;

import org.springframework.stereotype.Repository;
import fr.microtec.geo2.persistance.entity.FunctionResult;

@Repository
public interface GeoFunctionStockRepository {

    /**
     * Met à jour les valeurs du stock hebdomadaire
     */
    FunctionResult refreshStockHebdo();

}
