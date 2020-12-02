package fr.microtec.geo2.persistance.repository.stock;

import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoStockArticleAgeRepository extends GeoRepository<GeoStockArticleAge, GeoStockArticleAgeKey> {

  static final String findDistinctArticleInOrdreLigneQuery = "SELECT saa FROM #{#entityName} saa WHERE saa.article IN (SELECT DISTINCT ol.article FROM GeoOrdreLigne ol)";

  @Query(findDistinctArticleInOrdreLigneQuery)
  Page<GeoStockArticleAge> findByDistinctArticleInOrdreLigne(@Nullable Specification<GeoStockArticleAge> search, Pageable pageable);

}