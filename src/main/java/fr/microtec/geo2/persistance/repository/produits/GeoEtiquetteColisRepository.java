package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoEtiquetteColis;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoEtiquetteColisRepository extends GeoRepository<GeoEtiquetteColis, GeoProduitWithEspeceId> {
}
