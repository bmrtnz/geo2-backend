package fr.microtec.geo2.persistance.repository.tiers;

import fr.microtec.geo2.persistance.entity.tiers.GeoMoyenPaiement;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoMoyenPaiementRepository extends GeoGraphRepository<GeoMoyenPaiement, String> {
}
