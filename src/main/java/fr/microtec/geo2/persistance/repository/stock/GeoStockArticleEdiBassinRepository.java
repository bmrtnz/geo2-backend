package fr.microtec.geo2.persistance.repository.stock;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleEdiBassin;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoStockArticleEdiBassinRepository extends GeoRepository<GeoStockArticleEdiBassin, BigDecimal> {
    @Transactional
    void deleteAllByOrdreEdiId(BigDecimal ordreEdiId);
}
