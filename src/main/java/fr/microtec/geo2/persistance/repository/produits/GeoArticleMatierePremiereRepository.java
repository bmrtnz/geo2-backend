package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.entity.produit.GeoArticleCahierDesCharge;
import fr.microtec.geo2.persistance.entity.produit.GeoArticleMatierePremiere;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoArticleMatierePremiereRepository extends GeoGraphRepository<GeoArticleMatierePremiere, String> {
}
