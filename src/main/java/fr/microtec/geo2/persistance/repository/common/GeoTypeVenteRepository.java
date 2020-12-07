package fr.microtec.geo2.persistance.repository.common;

import fr.microtec.geo2.persistance.entity.common.GeoTypeVente;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoTypeVenteRepository extends GeoRepository<GeoTypeVente, String> {
}
