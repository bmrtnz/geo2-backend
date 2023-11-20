package fr.microtec.geo2.persistance.repository.common;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.common.GeoAlerte;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoAlerteRepository extends GeoRepository<GeoAlerte, BigDecimal> {
    Optional<GeoAlerte> findTopByOrderByDateCreationDesc();
}
