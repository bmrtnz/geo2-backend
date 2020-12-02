package fr.microtec.geo2.persistance.repository.common;

import fr.microtec.geo2.persistance.entity.common.GeoGridConfig;
import fr.microtec.geo2.persistance.entity.common.GeoGridConfigKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoGridConfigRepository extends GeoRepository<GeoGridConfig, GeoGridConfigKey> {
}