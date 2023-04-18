package fr.microtec.geo2.persistance.repository.ordres;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.entity.ordres.GeoDeclarationFraude;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningDepart;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningMaritime;
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

    @Query(name = "Ordre.allPlanningDepartMaritime", nativeQuery = true)
    List<GeoPlanningMaritime> allPlanningDepartMaritime(
            @Param("arg_soc_code") String societeCode,
            @Param("arg_date_min") LocalDateTime dateMin,
            @Param("arg_date_max") LocalDateTime dateMax);

    @Query(name = "Ordre.allPlanningArriveMaritime", nativeQuery = true)
    List<GeoPlanningMaritime> allPlanningArriveMaritime(
            @Param("arg_soc_code") String societeCode,
            @Param("arg_date_min") LocalDateTime dateMin,
            @Param("arg_date_max") LocalDateTime dateMax);

    @Query(name = "Ordre.allPlanningDepart", nativeQuery = true)
    List<GeoPlanningDepart> allPlanningDepart(
            @Param("arg_soc_code") String societeCode,
            @Param("arg_sco_code") String secteurCode,
            @Param("arg_date_min") LocalDateTime dateMin,
            @Param("arg_date_max") LocalDateTime dateMax);

    @Query(name = "Ordre.createChargement", nativeQuery = true)
    @Modifying
    @Transactional
    void createChargement(
            @Param("arg_nordre") String numeroOrdre,
            @Param("arg_code_chargement") String codeChargement,
            @Param("arg_ord_original_ref") String ordreOriginal);

    @Query(name = "Ordre.allDeclarationFraude", nativeQuery = true)
    @Modifying
    @Transactional
    List<GeoDeclarationFraude> allDeclarationFraude(
            @Param("ra_tiers_code") String secteur,
            @Param("ra_soc_code") String societe,
            @Param("ra_date_min") LocalDate dateMin,
            @Param("ra_date_max") LocalDate dateMax,
            @Param("ra_date_crea") LocalDateTime dateCreation,
            @Param("ra_cli_ref") String client,
            @Param("ra_trp_code") String transporteur,
            @Param("ra_fou_code") String fournisseur,
            @Param("ra_bac_code") String bureauAchat,
            @Param("ra_cen_ref") String entrepot);

}
