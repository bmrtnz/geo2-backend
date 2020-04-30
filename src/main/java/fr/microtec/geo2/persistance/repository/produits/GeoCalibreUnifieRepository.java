package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produit.GeoCalibreUnifie;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoCalibreUnifieRepository extends GeoGraphRepository<GeoCalibreUnifie, String> {
}
