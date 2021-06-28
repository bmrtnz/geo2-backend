package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.ordres.GeoLitige;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigne;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigneTotaux;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoLitigeLigneRepository extends GeoRepository<GeoLitigeLigne, String> {
  @Query(
    "SELECT new fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigneTotaux("+
      "SUM(ll.clientPrixUnitaire * ll.clientQuantite) as avoirClient,"+
      "SUM(ll.clientPrixUnitaire * ll.clientQuantite * ll.litige.ordreOrigine.tauxDevise) as avoirClientTaux,"+
      "SUM(ll.devisePrixUnitaire * ll.responsableQuantite) as avoirFournisseur,"+
      "SUM(ll.devisePrixUnitaire * ll.responsableQuantite * ll.deviseTaux) as avoirFournisseurTaux,"+
      "(ll.litige.totalMontantRistourne * ll.litige.ordreOrigine.tauxDevise) as ristourneTaux,"+
      "ll.litige.fraisAnnexes,"+
      "ll.litige.totalMontantRistourne,"+
      "ll.litige.ordreOrigine.devise"+
    ") "+
    "FROM #{#entityName} ll " + 
    "WHERE ll.litige = :litige "+
    "GROUP BY " +
    "ll.litige," +
    "ll.litige.fraisAnnexes," +
    "ll.litige.ordreOrigine.devise," +
    "ll.litige.totalMontantRistourne," +
    "(ll.litige.totalMontantRistourne * ll.litige.ordreOrigine.tauxDevise)"
  )
  Optional<GeoLitigeLigneTotaux> getTotaux(GeoLitige litige);
}