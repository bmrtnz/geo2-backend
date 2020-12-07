package fr.microtec.geo2.persistance.repository.stock;

import fr.microtec.geo2.persistance.entity.stock.GeoStockHebdomadaire;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoStockHebdomadaireRepository extends GeoRepository<GeoStockHebdomadaire, String> {
}