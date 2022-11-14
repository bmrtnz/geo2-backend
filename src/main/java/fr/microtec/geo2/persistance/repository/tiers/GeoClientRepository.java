package fr.microtec.geo2.persistance.repository.tiers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientEnCours;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoClientRepository extends GeoRepository<GeoClient, String> {
    Optional<GeoClient> getOneByCode(String code);

    @Query(name = "Client.allClientEnCours", nativeQuery = true)
    List<GeoClientEnCours> allClientEnCours(
            @Param("ra_cli_ref") String clientRef,
            @Param("ra_dev_code_soc") String deviseCodeRef);
}
