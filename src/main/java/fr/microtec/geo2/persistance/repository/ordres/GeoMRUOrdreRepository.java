package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoMRUOrdreRepository extends GeoRepository<GeoMRUOrdre, GeoMRUOrdreKey> {}
