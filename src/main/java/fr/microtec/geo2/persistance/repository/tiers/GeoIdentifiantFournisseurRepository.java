package fr.microtec.geo2.persistance.repository.tiers;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.tiers.GeoIdentifiantFournisseur;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoIdentifiantFournisseurRepository extends GeoRepository<GeoIdentifiantFournisseur, Integer> {
}
