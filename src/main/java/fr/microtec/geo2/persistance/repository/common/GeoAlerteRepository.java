package fr.microtec.geo2.persistance.repository.common;

import fr.microtec.geo2.persistance.entity.common.GeoAlerte;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

@Repository
public interface GeoAlerteRepository extends GeoRepository<GeoAlerte, BigDecimal> {
}
