package fr.microtec.geo2.persistance.repository.litige;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.litige.GeoLitige;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeAPayer;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoLitigeRepository extends GeoRepository<GeoLitige, String> {
    @Query(name = "Litige.allLitigeAPayer", nativeQuery = true)
    List<GeoLitigeAPayer> allLitigeAPayer(@Param("arg_lit_ref") String litigeID);
}
