package fr.microtec.geo2.persistance.repository.ordres;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningTransporteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoOrdreRepository extends GeoRepository<GeoOrdre, String> {
  @Query("SELECT o FROM #{#entityName} o WHERE o.id = :id AND o.ordrePere.id = o.id")
  Optional<GeoOrdre> findByIdAndMatchingOrdrePere(String id);

  Optional<GeoOrdre> findByNumeroAndSociete(String id, GeoSociete societe);

  @Query(
    name = "Ordre.allPlanningTransporteurs",
    nativeQuery = true
  )
  Page<GeoPlanningTransporteur> allPlanningTransporteurs(
    @Param("arg_date_min") LocalDateTime dateMin,
    @Param("arg_date_max") LocalDateTime dateMax,
    @Param("arg_soc_code") String societeCode,
    @Param("arg_trp_code") String transporteurCode,
    // Specification<GeoPlanningTransporteur> specs,
    Pageable pageable
  );
}