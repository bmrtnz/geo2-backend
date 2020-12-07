package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoCategorie;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoCategorieRepository extends GeoRepository<GeoCategorie, GeoProduitWithEspeceId> {
}
