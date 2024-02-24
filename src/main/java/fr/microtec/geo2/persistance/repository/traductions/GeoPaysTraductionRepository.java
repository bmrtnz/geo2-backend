package fr.microtec.geo2.persistance.repository.traductions;

import fr.microtec.geo2.persistance.entity.traductions.GeoPaysTraduction;
import fr.microtec.geo2.persistance.entity.traductions.GeoPaysTraductionId;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoPaysTraductionRepository extends GeoRepository<GeoPaysTraduction, GeoPaysTraductionId> {
}
