package fr.microtec.geo2.persistance.repository.common;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.common.GeoZoneGeographique;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoZoneGeographiqueRepository extends GeoRepository<GeoZoneGeographique, Integer> {
}
