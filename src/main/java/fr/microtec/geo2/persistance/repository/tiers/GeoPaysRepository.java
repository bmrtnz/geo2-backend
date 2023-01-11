package fr.microtec.geo2.persistance.repository.tiers;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import fr.microtec.geo2.persistance.entity.tiers.GeoPaysDepassement;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoPaysRepository extends GeoRepository<GeoPays, String> {
    @Query(name = "Pays.allPaysDepassement", nativeQuery = true)
    List<GeoPaysDepassement> allPaysDepassement(
            @Param("arg_sco_code") String secteurCode,
            @Param("arg_soc_code") String societeCode,
            @Param("arg_com_code") String commercialCode);

    @Query(name = "Pays.countPaysDepassement", nativeQuery = true)
    long countPaysDepassement(
            @Param("arg_sco_code") String secteurCode,
            @Param("arg_soc_code") String societeCode);
}
