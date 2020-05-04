package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoCalibreMarquage;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoCalibreMarquageRepository extends GeoGraphRepository<GeoCalibreMarquage, String> {
}
