package fr.microtec.geo2.persistance.repository.tiers;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.tiers.GeoEnvois;
import fr.microtec.geo2.persistance.entity.tiers.GeoFlux;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoEnvoisRepository extends GeoRepository<GeoEnvois, String> {
  Long countByOrdreAndFlux(GeoOrdre ordre, GeoFlux flux);
}
