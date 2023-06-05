package fr.microtec.geo2.persistance.repository.ordres;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoMRUOrdreRepository extends GeoRepository<GeoMRUOrdre, GeoMRUOrdreKey> {
    @Query(value = "SELECT * FROM ( SELECT mru.* FROM GEO_MRU_ORDRE mru INNER JOIN geo_ordre o ON o.ORD_REF = mru.ord_ref WHERE mru.SOC_CODE = :societe AND mru.ORD_REF IS NOT NULL AND mru.NOM_UTILISATEUR = :utilisateur AND mru.MOD_DATE >= :dateMax ORDER BY mru.MOD_DATE DESC ) WHERE rownum <= :count", nativeQuery = true)
    List<GeoMRUOrdre> findHead(String societe, String utilisateur, LocalDateTime dateMax, Long count);
}
