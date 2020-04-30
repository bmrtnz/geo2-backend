package fr.microtec.geo2.persistance.repository.common;

import fr.microtec.geo2.persistance.entity.common.GeoGenre;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoGenreRepository extends GeoGraphRepository<GeoGenre, String> {
}
