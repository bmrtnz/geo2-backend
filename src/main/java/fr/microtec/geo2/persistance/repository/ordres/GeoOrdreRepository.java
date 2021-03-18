package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoOrdreRepository extends GeoRepository<GeoOrdre, String> {
}