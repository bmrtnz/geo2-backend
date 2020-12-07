package fr.microtec.geo2.persistance.repository.stock;

import fr.microtec.geo2.persistance.entity.stock.GeoStockConsolide;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoStockConsolideRepository extends GeoRepository<GeoStockConsolide, String> {
}