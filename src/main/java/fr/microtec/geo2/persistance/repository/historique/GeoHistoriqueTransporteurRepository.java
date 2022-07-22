package fr.microtec.geo2.persistance.repository.historique;

import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueTransporteur;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

@Repository
public interface GeoHistoriqueTransporteurRepository extends GeoRepository<GeoHistoriqueTransporteur, BigDecimal> {
}
