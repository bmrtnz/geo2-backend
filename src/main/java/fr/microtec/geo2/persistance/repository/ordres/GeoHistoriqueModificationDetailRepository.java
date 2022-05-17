package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoHistoriqueModificationDetail;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoHistoriqueModificationDetailRepository
        extends GeoRepository<GeoHistoriqueModificationDetail, String> {
}