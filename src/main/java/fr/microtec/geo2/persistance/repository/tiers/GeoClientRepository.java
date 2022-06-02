package fr.microtec.geo2.persistance.repository.tiers;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoClientRepository extends GeoRepository<GeoClient, String> {
    Optional<GeoClient> getOneByCode(String code);
}
