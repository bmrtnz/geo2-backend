package fr.microtec.geo2.persistance.repository.stock;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.stock.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoOrdreLigneRepository extends GeoGraphRepository<GeoOrdreLigne, String> {
  @Query("SELECT DISTINCT ol.article FROM #{#entityName} ol")
  List<GeoArticle> findDistinctArticle();
}