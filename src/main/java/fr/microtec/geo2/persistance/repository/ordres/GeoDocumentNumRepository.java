package fr.microtec.geo2.persistance.repository.ordres;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoDocumentNum;
import fr.microtec.geo2.persistance.entity.ordres.GeoDocumentNumKey;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoDocumentNumRepository extends GeoRepository<GeoDocumentNum, GeoDocumentNumKey> {
    @Transactional
    Integer deleteByIdAndOrdreLigneAndTypeDocument(String id, GeoOrdreLigne ordreLigne, String typeDocument);
}
