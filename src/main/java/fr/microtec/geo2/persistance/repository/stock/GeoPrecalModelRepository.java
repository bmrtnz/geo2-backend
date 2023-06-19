package fr.microtec.geo2.persistance.repository.stock;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.stock.GeoPrecalModel;
import fr.microtec.geo2.persistance.entity.stock.GeoPrecalModelVariete;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoPrecalModelRepository extends GeoRepository<GeoPrecalModel, String> {
    @Query(name = "Stock.allPrecaEspece", nativeQuery = true)
    List<String> allPrecaEspece();

    @Query(name = "Stock.allPrecaVariete", nativeQuery = true)
    List<GeoPrecalModelVariete> allPrecaVariete(@Param("arg_espece") String espece);
}
