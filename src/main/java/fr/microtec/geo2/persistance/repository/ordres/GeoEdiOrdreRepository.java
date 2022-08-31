package fr.microtec.geo2.persistance.repository.ordres;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoCommandeEdi;
import fr.microtec.geo2.persistance.entity.ordres.GeoEdiOrdre;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoEdiOrdreRepository extends GeoRepository<GeoEdiOrdre, String> {

    @Query(name = "OrdreEdi.allCommandeEdi", nativeQuery = true)
    List<GeoCommandeEdi> allCommandeEdi(
            @Param("arg_sco_code") String scoCode,
            @Param("arg_cli_code") String cliCode,
            @Param("arg_status") String status,
            @Param("arg_date_min") LocalDateTime dateMin,
            @Param("arg_date_max") LocalDateTime dateMax,
            @Param("arg_assist") String assistante,
            @Param("arg_com") String commercial,
            @Param("arg_ref_edi_ord") String arg_ref_edi_ord,
            @Param("arg_user") String nomUtilisateur);

    @Query(name = "OrdreEdi.allClientEdi", nativeQuery = true)
    List<GeoClient> allClientEdi(
            @Param("arg_sco_code") String scoCode,
            @Param("arg_ass_code") String assCode,
            @Param("arg_com_code") String comCode);

}
