package fr.microtec.geo2.persistance.repository.historique;

import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueFournisseur;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoHistoriqueFournisseurRepository extends GeoGraphRepository<GeoHistoriqueFournisseur, String> {
}
