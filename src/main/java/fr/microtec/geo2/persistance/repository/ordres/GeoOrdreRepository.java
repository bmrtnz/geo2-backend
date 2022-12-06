package fr.microtec.geo2.persistance.repository.ordres;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.entity.ordres.GeoLigneChargement;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningTransporteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoOrdreRepository extends GeoRepository<GeoOrdre, String> {
    @Query("SELECT o FROM #{#entityName} o WHERE o.id = :id AND o.ordrePere.id = o.id")
    Optional<GeoOrdre> findByIdAndMatchingOrdrePere(String id);

    Optional<GeoOrdre> findByNumeroAndSocieteAndCampagne(String id, GeoSociete societe, GeoCampagne campagne);

    @Query(name = "Ordre.allPlanningTransporteurs", nativeQuery = true)
    List<GeoPlanningTransporteur> allPlanningTransporteurs(
            @Param("arg_date_min") LocalDateTime dateMin,
            @Param("arg_date_max") LocalDateTime dateMax,
            @Param("arg_soc_code") String societeCode,
            @Param("arg_trp_code") String transporteurCode);

    @Query(name = "Ordre.allLignesChargement", nativeQuery = true)
    List<GeoLigneChargement> allLignesChargement(
            @Param("arg_code_chargement") String codeChargement,
            @Param("arg_cam_code") String campagne);
}
