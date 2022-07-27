package fr.microtec.geo2.persistance.repository.tiers;

import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface GeoFournisseurRepository extends GeoRepository<GeoFournisseur, String> {
    Optional<GeoFournisseur> getOneByCode(String code);
}
