package fr.microtec.geo2.persistance.repository.common;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.common.GeoRegion;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoRegionRepository extends GeoRepository<GeoRegion, Integer> {
}
