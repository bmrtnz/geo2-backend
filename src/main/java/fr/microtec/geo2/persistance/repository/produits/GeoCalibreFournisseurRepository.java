package fr.microtec.geo2.persistance.repository.produits;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.produits.GeoCalibreFournisseur;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoCalibreFournisseurRepository extends GeoRepository<GeoCalibreFournisseur, String> {
    Optional<GeoCalibreFournisseur> findOneByIdAndEspeceId(String id, String especeId);
}
