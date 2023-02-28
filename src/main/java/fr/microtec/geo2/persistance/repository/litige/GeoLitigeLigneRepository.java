package fr.microtec.geo2.persistance.repository.litige;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.litige.GeoLitige;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigne;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneFait;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneForfait;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneTotaux;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoLitigeLigneRepository extends GeoRepository<GeoLitigeLigne, String> {
    @Query("SELECT new fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneTotaux(" +
            "SUM(ll.clientPrixUnitaire * ll.clientQuantite) as avoirClient," +
            "SUM(ll.clientPrixUnitaire * ll.clientQuantite * ll.litige.ordreOrigine.tauxDevise) as avoirClientTaux," +
            "SUM(ll.devisePrixUnitaire * ll.responsableQuantite) as avoirFournisseur," +
            "SUM(ll.devisePrixUnitaire * ll.responsableQuantite * ll.deviseTaux) as avoirFournisseurTaux," +
            "(ll.litige.totalMontantRistourne * ll.litige.ordreOrigine.tauxDevise) as ristourneTaux," +
            "ll.litige.fraisAnnexes," +
            "ll.litige.totalMontantRistourne," +
            "ll.litige.ordreOrigine.devise" +
            ") " +
            "FROM #{#entityName} ll " +
            "WHERE ll.litige = :litige " +
            "GROUP BY " +
            "ll.litige," +
            "ll.litige.fraisAnnexes," +
            "ll.litige.ordreOrigine.devise," +
            "ll.litige.totalMontantRistourne," +
            "(ll.litige.totalMontantRistourne * ll.litige.ordreOrigine.tauxDevise)")
    Optional<GeoLitigeLigneTotaux> getTotaux(GeoLitige litige);

    @Query(name = "Litige.allLitigeLigneFait", nativeQuery = true)
    List<GeoLitigeLigneFait> allLitigeLigneFait(
            @Param("ar_lit_ref") String litigeID,
            @Param("ar_orl_lit") String numeroLigne);

    @Query(name = "Litige.allLitigeLigneForfait", nativeQuery = true)
    List<GeoLitigeLigneForfait> allLitigeLigneForfait(
            @Param("arg_lit_ref") String litRef);

    @Transactional
    @Modifying
    void deleteAllByIdIn(List<String> id);
}
