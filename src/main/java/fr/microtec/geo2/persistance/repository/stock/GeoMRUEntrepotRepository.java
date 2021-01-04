package fr.microtec.geo2.persistance.repository.stock;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.stock.GeoMRUEntrepot;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoMRUEntrepotRepository extends GeoRepository<GeoMRUEntrepot, String> {}
