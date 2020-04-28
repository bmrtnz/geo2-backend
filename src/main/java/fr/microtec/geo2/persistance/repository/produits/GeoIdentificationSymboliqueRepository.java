package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produit.GeoIdentificationSymbolique;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoIdentificationSymboliqueRepository extends GeoGraphRepository<GeoIdentificationSymbolique, String> {
}
