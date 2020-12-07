package fr.microtec.geo2.persistance.repository.stock;

import fr.microtec.geo2.persistance.entity.stock.GeoStockDemain;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoStockDemainRepository extends GeoRepository<GeoStockDemain, String> {
}