package fr.microtec.geo2.persistance.repository.common;

import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoCampagneRepository extends GeoRepository<GeoCampagne, String> {
}
