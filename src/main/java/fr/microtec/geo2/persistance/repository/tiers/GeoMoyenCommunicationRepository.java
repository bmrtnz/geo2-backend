package fr.microtec.geo2.persistance.repository.tiers;

import fr.microtec.geo2.persistance.entity.tiers.GeoMoyenCommunication;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoMoyenCommunicationRepository extends GeoRepository<GeoMoyenCommunication, String> {
}
