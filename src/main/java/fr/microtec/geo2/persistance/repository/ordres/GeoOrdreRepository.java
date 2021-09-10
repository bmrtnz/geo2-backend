package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreSuiviDeparts;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoOrdreRepository extends GeoRepository<GeoOrdre, String> {
  @Query("SELECT o FROM #{#entityName} o WHERE o.id = :id AND o.ordrePere.id = o.id")
  Optional<GeoOrdre> findByIdAndMatchingOrdrePere(String id);

  // @Query(
  //   "SELECT new fr.microtec.geo2.persistance.entity.ordres.GeoOrdreSuiviDeparts("+
  //     "o.id as id, " +
  //     "o.numero as numero, " +
  //     "o.codeClient as codeClient, " +
  //     "o.versionDetail as versionDetail, " +
  //     "o.codeAlphaEntrepot as codeAlphaEntrepot, " +
  //     "o.assistante as assistante, " +
  //     "o.commercial as commercial, " +
  //     "o.transporteur as transporteur, " +
  //     "o.dateLivraisonPrevue as dateLivraisonPrevue, " +
  //     "olo.fournisseur as fournisseur, " +
  //     "olo.dateDepartPrevueFournisseur as dateDepartPrevueFournisseur, " +
  //     "olo.dateDepartReelleFournisseur as dateDepartReelleFournisseur, " +
  //     "olo.fournisseurReferenceDOC as fournisseurReferenceDOC, " +
  //     "olo.expedieStation as expedieStation, " +
  //     "olo.totalPalettesExpediees as totalPalettesExpediees, " +
  //     "olo.nombrePalettesAuSol as nombrePalettesAuSol, " +
  //     "olo.nombrePalettes100x120 as nombrePalettes100x120, " +
  //     "olo.nombrePalettes80x120 as nombrePalettes80x120, " +
  //     "olo.nombrePalettes60X80 as nombrePalettes60X80, " +
  //     "SUM(oli.nombreColisCommandes) as nombreColisCommandes, "+
  //     "SUM(oli.nombreColisExpedies) as nombreColisExpedies"+
  //   ") "+
  //   "FROM #{#entityName} o " + 
  //   "JOIN o.lignes oli " + 
  //   "JOIN o.logistiques olo " + 
  //   "WHERE olo.dateDepartPrevueFournisseur BETWEEN :minDate AND :maxDate " + 
  //   "GROUP BY " +
  //     "o.id, " +
  //     "o.numero, " +
  //     "o.codeClient, " +
  //     "o.versionDetail, " +
  //     "o.codeAlphaEntrepot, " +
  //     "o.assistante, " +
  //     "o.commercial, " +
  //     "o.transporteur, " +
  //     "o.dateLivraisonPrevue, " +
  //     "olo.fournisseur, " +
  //     "olo.dateDepartPrevueFournisseur, " +
  //     "olo.dateDepartReelleFournisseur, " +
  //     "olo.fournisseurReferenceDOC, " +
  //     "olo.expedieStation, " +
  //     "olo.totalPalettesExpediees, " +
  //     "olo.nombrePalettesAuSol, " +
  //     "olo.nombrePalettes100x120, " +
  //     "olo.nombrePalettes80x120, " +
  //     "olo.nombrePalettes60X80 "
  // )
  // Page<GeoOrdreSuiviDeparts> findSuiviDeparts(
  //   @Param("minDate") LocalDate minDate,
  //   @Param("maxDate") LocalDate maxDate,
  //   Pageable pageable
  // );

  // @EntityGraph(value = "SuiviDeparts")
  // Page<GeoOrdre> findAll(@Nullable Specification<GeoOrdre> spec, Pageable pageable);
}