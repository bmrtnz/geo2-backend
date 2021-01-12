package fr.microtec.geo2.persistance.repository.stock;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.stock.GeoMRUOrdre;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoMRUOrdreRepository extends GeoRepository<GeoMRUOrdre, String> {}
