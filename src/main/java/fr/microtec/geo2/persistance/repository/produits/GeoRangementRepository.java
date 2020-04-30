package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produit.GeoRangement;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoRangementRepository extends GeoGraphRepository<GeoRangement, String> {
}
