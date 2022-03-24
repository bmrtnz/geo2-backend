package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoDefCodePromo;
import fr.microtec.geo2.persistance.entity.ordres.GeoDefCodePromoId;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoDefCodePromoRepository extends GeoRepository<GeoDefCodePromo, GeoDefCodePromoId> {
}