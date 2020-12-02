package fr.microtec.geo2.persistance.repository.historique;

import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueArticle;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoHistoriqueArticleRepository extends GeoRepository<GeoHistoriqueArticle, String> {
}
