package fr.microtec.geo2.persistance.repository.tiers;

import fr.microtec.geo2.persistance.entity.tiers.GeoLieuPassageAQuai;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoLieuPassageAQuaiRepository extends GeoGraphRepository<GeoLieuPassageAQuai, String> {
}
