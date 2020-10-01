package fr.microtec.geo2.persistance.repository.stock;

import fr.microtec.geo2.persistance.entity.stock.GeoOrdre;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoOrdreRepository extends GeoGraphRepository<GeoOrdre, String> {
}