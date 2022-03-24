package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoCodePromo;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoCodePromoRepository extends GeoRepository<GeoCodePromo, String> {
}