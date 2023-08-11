package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleStatistiqueClient;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleStatistiqueFournisseur;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GeoArticleRepository extends GeoRepository<GeoArticle, String> {
  @Procedure("SYNC_ARTICLE")
  void syncArticle(@Param("avi_art_ref")String articleId);

    @Query(name = "ArticleStatistique.allArticleStatistiqueClients", nativeQuery = true)
    List<GeoArticleStatistiqueClient> allArticleStatistiqueClients(
        @Param("arg_art_ref") String article,
        @Param("arg_soc_code") String societe,
        @Param("arg_date_min") LocalDate dateMin,
        @Param("arg_date_max") LocalDate dateMax
    );

    @Query(name = "ArticleStatistique.allArticleStatistiqueFournisseurs", nativeQuery = true)
    List<GeoArticleStatistiqueFournisseur> allArticleStatistiqueFournisseurs(
        @Param("arg_art_ref") String article,
        @Param("arg_soc_code") String societe,
        @Param("arg_date_min") LocalDate dateMin,
        @Param("arg_date_max") LocalDate dateMax
    );

}
