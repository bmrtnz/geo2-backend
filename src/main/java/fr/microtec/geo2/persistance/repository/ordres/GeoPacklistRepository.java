package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoPacklistEntete;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoPacklistRepository extends GeoRepository<GeoPacklistEntete, Integer> {
}
