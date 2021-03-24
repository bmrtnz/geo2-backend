package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLogistique;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoOrdreLogistiqueRepository extends GeoRepository<GeoOrdreLogistique, String> {}
