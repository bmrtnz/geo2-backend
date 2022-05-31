package fr.microtec.geo2.persistance.repository.stock;

import fr.microtec.geo2.persistance.entity.stock.GeoStock;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticle;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoStockRepository extends GeoRepository<GeoStock, String> {
    @Query(name = "Stock.allStockArticle", nativeQuery = true)
    List<GeoStockArticle> allStockArticle(
            @Param("arg_espece") String espece,
            @Param("arg_variete") String variete,
            @Param("arg_origine") String origine,
            @Param("arg_mode_culture") String modeCulture,
            @Param("arg_emballage") String emballage,
            @Param("arg_bureau_achat") String bureauAchat);
}
