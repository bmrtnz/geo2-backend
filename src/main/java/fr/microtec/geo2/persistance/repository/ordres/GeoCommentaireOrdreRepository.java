package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoCommentaireOrdre;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoCommentaireOrdreRepository extends GeoRepository<GeoCommentaireOrdre, String> {}