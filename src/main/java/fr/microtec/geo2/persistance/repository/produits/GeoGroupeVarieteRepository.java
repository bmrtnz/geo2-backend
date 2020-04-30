package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produit.GeoGroupeVariete;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoGroupeVarieteRepository extends GeoGraphRepository<GeoGroupeVariete, String> {
}
