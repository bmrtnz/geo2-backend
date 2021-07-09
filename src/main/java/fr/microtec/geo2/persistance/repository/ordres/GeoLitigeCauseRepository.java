package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeCause;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoLitigeCauseRepository extends GeoRepository<GeoLitigeCause, String> {}