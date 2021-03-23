package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigne;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface GeoLitigeLigneRepository extends GeoRepository<GeoLitigeLigne, String> {}