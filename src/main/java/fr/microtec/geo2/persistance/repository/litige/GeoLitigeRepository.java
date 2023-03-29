package fr.microtec.geo2.persistance.repository.litige;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.litige.GeoLitige;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeAPayer;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeSupervision;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoLitigeRepository extends GeoRepository<GeoLitige, String> {
    @Query(name = "Litige.allLitigeAPayer", nativeQuery = true)
    List<GeoLitigeAPayer> allLitigeAPayer(@Param("arg_lit_ref") String litigeID);

    @Query(name = "Litige.allSupervisionLitige", nativeQuery = true)
    List<GeoLitigeSupervision> allSupervisionLitige(
            @Param("arg_typ_search") String type,
            @Param("arg_cod_search") String code);

    @Query(name = "Litige.genNumLot", nativeQuery = true)
    String genNumLot(@Param("is_lit_ref") String litigeID);

    @Query(name = "Litige.countCauseConseq", nativeQuery = true)
    int[] countCauseConseq(@Param("is_ord_ref") String ordreID);
}
