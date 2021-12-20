package fr.microtec.geo2.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoMouvementEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoMouvementFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoRecapitulatifEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoRecapitulatifFournisseur;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service()
public class EntrepotService extends GeoAbstractGraphQLService<GeoEntrepot, String> {

  @PersistenceContext
  private EntityManager entityManager;

  private final GeoEntrepotRepository entrepotRepository;

  public EntrepotService(
    GeoEntrepotRepository entrepotRepository
  ) {
    super(entrepotRepository, GeoEntrepot.class);
    this.entrepotRepository = entrepotRepository;
  }

  public List<GeoMouvementFournisseur> allMouvementFournisseur(
    LocalDateTime dateMin,
    String codeSociete,
    String codeEntrepot,
    String codeCommercial
  ) {
    return this.entrepotRepository
    .allMouvementFournisseur(
      dateMin,
      codeSociete,
      codeEntrepot,
      codeCommercial
    );
  }

  public List<GeoMouvementEntrepot> allMouvementEntrepot(
    LocalDateTime dateMin,
    String codeSociete,
    String codeEntrepot,
    String codeCommercial
  ) {
    return this.entrepotRepository
    .allMouvementEntrepot(
      dateMin,
      codeSociete,
      codeEntrepot,
      codeCommercial
    );
  }

  public List<GeoRecapitulatifFournisseur> allRecapitulatifFournisseur(
    LocalDateTime dateMin,
    String codeSociete,
    String codeEntrepot,
    String codeCommercial
  ) {
    return this.entrepotRepository
    .allRecapitulatifFournisseur(
      dateMin,
      codeSociete,
      codeEntrepot,
      codeCommercial
    );
  }

  public List<GeoRecapitulatifEntrepot> allRecapitulatifEntrepot(
    LocalDateTime dateMin,
    String codeSociete,
    String codeEntrepot,
    String codeCommercial
  ) {
    return this.entrepotRepository
    .allRecapitulatifEntrepot(
      dateMin,
      codeSociete,
      codeEntrepot,
      codeCommercial
    );
  }
}
