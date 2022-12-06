package fr.microtec.geo2.persistance.repository.ordres;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoLigneChargement;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoLigneChargementRepository extends GeoRepository<GeoLigneChargement, Integer> {
    @Query(name = "Ordre.allLignesChargement", nativeQuery = true)
    List<GeoLigneChargement> allLignesChargement(
            @Param("arg_code_chargement") String codeChargement,
            @Param("arg_cam_code") String campagne);
}
