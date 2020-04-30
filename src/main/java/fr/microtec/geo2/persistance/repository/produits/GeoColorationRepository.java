package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produit.GeoColoration;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoColorationRepository extends GeoGraphRepository<GeoColoration, String> {
}
