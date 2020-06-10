package fr.microtec.geo2.persistance.repository.common;

import fr.microtec.geo2.persistance.entity.common.GeoModeCulture;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoModeCultureRepository extends GeoGraphRepository<GeoModeCulture, Integer> {
}
