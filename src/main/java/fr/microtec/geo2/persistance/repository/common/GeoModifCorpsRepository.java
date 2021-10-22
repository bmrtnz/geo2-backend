package fr.microtec.geo2.persistance.repository.common;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.common.GeoModificationCorps;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoModifCorpsRepository extends GeoRepository<GeoModificationCorps, BigDecimal> {
}
