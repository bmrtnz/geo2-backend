package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoArticleRepository extends GeoRepository<GeoArticle, String> {
  @Procedure("SYNC_ARTICLE")
  void syncArticle(@Param("avi_art_ref")String articleId);
}
