package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produit.GeoCalibreFournisseur;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoCalibreFournisseurRepository extends GeoGraphRepository<GeoCalibreFournisseur, String> {
}
