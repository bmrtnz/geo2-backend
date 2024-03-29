package fr.microtec.geo2.persistance.repository.produits;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.produits.GeoEdiArticleClient;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoEdiArticleClientRepository extends GeoRepository<GeoEdiArticleClient, BigDecimal> {
}
