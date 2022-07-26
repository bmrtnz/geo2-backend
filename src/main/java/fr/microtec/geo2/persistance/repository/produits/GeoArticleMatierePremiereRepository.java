package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produits.GeoArticleMatierePremiere;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import fr.microtec.geo2.persistance.repository.produits.matcher.GeoArticlePartMatchable;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoArticleMatierePremiereRepository extends GeoArticlePartRepository<GeoArticleMatierePremiere, String> {
}
