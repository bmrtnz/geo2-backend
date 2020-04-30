package fr.microtec.geo2.persistance.repository.common;

import fr.microtec.geo2.persistance.entity.common.GeoTypeVente;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoTypeVenteRepository extends GeoGraphRepository<GeoTypeVente, String> {
}
