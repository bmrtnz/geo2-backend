package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoArticleEmballage;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoArticleEmballageRepository extends GeoArticlePartRepository<GeoArticleEmballage, String> {
}
