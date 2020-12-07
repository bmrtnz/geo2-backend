package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoCalibreFournisseur;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoCalibreFournisseurRepository extends GeoRepository<GeoCalibreFournisseur, String> {
}
