package fr.microtec.geo2.persistance.repository.produits;

import fr.microtec.geo2.persistance.repository.GeoRepository;
import fr.microtec.geo2.persistance.repository.produits.matcher.GeoArticlePartMatchable;

import java.io.Serializable;

/**
 *
 * This repository abstract GeoArticlePartMatchable for repository of tables avi_art_*,
 * but not avi_art_gestion (This is master table).
 *
 */
public interface GeoArticlePartRepository<T, ID extends Serializable>
    extends GeoRepository<T, ID>, GeoArticlePartMatchable<T> {
}
