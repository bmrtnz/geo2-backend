package fr.microtec.geo2.persistance.repository.ordres;

import java.util.List;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreFrais;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoOrdreFraisRepository extends GeoRepository<GeoOrdreFrais, String>{
  List<GeoOrdreFrais> findByOrdre(GeoOrdre ordre);
}
