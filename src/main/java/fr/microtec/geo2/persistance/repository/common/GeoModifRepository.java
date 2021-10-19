package fr.microtec.geo2.persistance.repository.common;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.common.GeoModif;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoModifRepository extends GeoRepository<GeoModif, BigDecimal> {
}
