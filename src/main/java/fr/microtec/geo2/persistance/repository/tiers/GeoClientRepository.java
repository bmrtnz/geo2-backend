package fr.microtec.geo2.persistance.repository.tiers;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoClientRepository extends GeoRepository<GeoClient, String> {
}
