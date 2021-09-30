package fr.microtec.geo2.persistance.repository.ordres;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoOrdreRepository extends GeoRepository<GeoOrdre, String> {
  @Query("SELECT o FROM #{#entityName} o WHERE o.id = :id AND o.ordrePere.id = o.id")
  Optional<GeoOrdre> findByIdAndMatchingOrdrePere(String id);

  Optional<GeoOrdre> findByNumeroAndSociete(String id, GeoSociete societe);
}