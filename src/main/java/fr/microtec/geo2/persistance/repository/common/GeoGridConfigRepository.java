package fr.microtec.geo2.persistance.repository.common;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.common.GeoGridConfig;
import fr.microtec.geo2.persistance.entity.common.GeoGridConfigKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoGridConfigRepository extends GeoRepository<GeoGridConfig, GeoGridConfigKey> {
    Optional<GeoGridConfig> getOneByGridAndUtilisateurNomUtilisateurAndSocieteId(
            String grid,
            String nomUtilisateur,
            String societe);
}
