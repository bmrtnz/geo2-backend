package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoMRUOrdreRepository extends GeoRepository<GeoMRUOrdre, GeoMRUOrdreKey> {}
