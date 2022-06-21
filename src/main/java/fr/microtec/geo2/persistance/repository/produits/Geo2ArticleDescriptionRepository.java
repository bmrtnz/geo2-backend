package fr.microtec.geo2.persistance.repository.produits;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.produits.Geo2ArticleDescription;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface Geo2ArticleDescriptionRepository extends GeoRepository<Geo2ArticleDescription, String> {
}
