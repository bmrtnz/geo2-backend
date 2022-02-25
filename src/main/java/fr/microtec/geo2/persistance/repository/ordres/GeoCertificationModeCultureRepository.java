package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoCertificationModeCulture;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoCertificationModeCultureRepository extends GeoRepository<GeoCertificationModeCulture, Integer> {}