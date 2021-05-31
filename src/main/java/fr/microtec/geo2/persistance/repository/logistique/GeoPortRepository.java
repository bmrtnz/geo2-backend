package fr.microtec.geo2.persistance.repository.logistique;

import fr.microtec.geo2.persistance.entity.logistique.GeoPort;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoPortRepository extends GeoRepository<GeoPort, String> {
}
