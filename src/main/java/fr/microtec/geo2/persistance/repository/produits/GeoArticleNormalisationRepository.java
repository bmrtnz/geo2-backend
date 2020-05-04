package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoArticleNormalisation;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoArticleNormalisationRepository extends GeoGraphRepository<GeoArticleNormalisation, String> {
}
