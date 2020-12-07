package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoMarque;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoMarqueRepository extends GeoRepository<GeoMarque, GeoProduitWithEspeceId> {
}
