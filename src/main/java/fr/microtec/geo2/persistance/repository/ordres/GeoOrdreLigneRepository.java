package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoOrdreLigneRepository extends GeoRepository<GeoOrdreLigne, String> {
  @Query("SELECT DISTINCT ol.article FROM #{#entityName} ol")
  List<GeoArticle> findDistinctArticle();
}