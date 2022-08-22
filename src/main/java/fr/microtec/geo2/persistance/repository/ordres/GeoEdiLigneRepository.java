package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.ordres.GeoEdiLigne;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoEdiLigneRepository extends GeoRepository<GeoEdiLigne, String> {
}
