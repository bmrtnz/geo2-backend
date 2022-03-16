package fr.microtec.geo2.persistance.repository.tiers;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.tiers.GeoDeviseRef;
import fr.microtec.geo2.persistance.entity.tiers.GeoDeviseRefKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoDeviseRefRepository extends GeoRepository<GeoDeviseRef, GeoDeviseRefKey> {
}
