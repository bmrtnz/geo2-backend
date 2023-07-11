package fr.microtec.geo2.persistance.repository.produits;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.produits.GeoEdiArticleClient;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoEdiArticleClientRepository extends GeoRepository<GeoEdiArticleClient, Integer> {
}
