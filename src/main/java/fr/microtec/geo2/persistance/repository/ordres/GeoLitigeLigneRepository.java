package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.ordres.GeoLitige;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigne;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigneTotaux;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoLitigeLigneRepository extends GeoRepository<GeoLitigeLigne, String> {
  @Query(
    "SELECT new fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigneTotaux("+
      "SUM(ll.clientPrixUnitaire * ll.clientQuantite * ll.litige.ordreOrigine.tauxDevise) as reclamationClientTaux,"+
      "SUM(ll.clientPrixUnitaire * ll.clientQuantite) as reclamationClient,"+
      "SUM(ll.devisePrixUnitaire * ll.responsableQuantite * ll.deviseTaux) as deviseTotalTaux,"+
      "SUM(ll.devisePrixUnitaire * ll.responsableQuantite) as deviseTotal,"+
      "(ll.litige.totalMontantRistourne * ll.litige.ordreOrigine.tauxDevise) as ristourne,"+
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
  Page<GeoLitigeLigneTotaux> getTotaux(GeoLitige litige,Pageable pageable);
}