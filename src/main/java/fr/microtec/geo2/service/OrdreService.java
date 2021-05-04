package fr.microtec.geo2.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoGenre;
import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.entity.ordres.GeoFactureAvoir;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigneCumul;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigneSummed;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLogistique;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreType;
import fr.microtec.geo2.persistance.entity.tiers.GeoFlux;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.ordres.GeoMRUOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreFraisRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLogistiqueRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoEnvoisRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoFluxRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.graphql.ordres.GeoOrdreGraphQLService;

@Service()
public class OrdreService extends GeoAbstractGraphQLService<GeoMRUOrdre, GeoMRUOrdreKey> {

  @PersistenceContext
  private EntityManager entityManager;

  private final GeoOrdreRepository ordreRepository;
  private final GeoMRUOrdreRepository mruOrdreRepository;
  private final GeoOrdreLigneRepository ordreLigneRepository;
  private final GeoOrdreLogistiqueRepository ordreLogistiqueRepository;
  private final GeoOrdreFraisRepository ordreFraisRepository;
  private final GeoEnvoisRepository envoisRepository;
  private final GeoFluxRepository fluxRepository;
  private static final Logger logger = LoggerFactory.getLogger(OrdreService.class);

  public OrdreService(
    GeoOrdreRepository ordreRepository,
    GeoMRUOrdreRepository mruOrdreRepository,
    GeoOrdreLigneRepository ordreLigneRepository,
    GeoOrdreLogistiqueRepository ordreLogistiqueRepository,
    GeoOrdreFraisRepository ordreFraisRepository,
    GeoEnvoisRepository envoisRepository,
    GeoFluxRepository fluxRepository
  ) {
    super(mruOrdreRepository);
    this.ordreRepository = ordreRepository;
    this.mruOrdreRepository = mruOrdreRepository;
    this.ordreLigneRepository = ordreLigneRepository;
    this.ordreLogistiqueRepository = ordreLogistiqueRepository;
    this.ordreFraisRepository = ordreFraisRepository;
    this.envoisRepository = envoisRepository;
    this.fluxRepository = fluxRepository;
  }

  private String fetchNumero(GeoSociete societe) {

    String societeId = societe.getId();
    String sequenceQuery = String.format("SELECT TO_CHAR(seq_nordre_%s.NEXTVAL,'FM099999') FROM DUAL", societeId);
    Session session = this.entityManager.unwrap(Session.class);
    SessionFactory factory = session.getSessionFactory();
    NativeQuery query = factory.openSession().createNativeQuery(sequenceQuery);

    return query.getSingleResult().toString();
  }

  public GeoOrdre save(GeoOrdre ordreChunk) {
    if (ordreChunk.getId() == null) {
      ordreChunk.setNumero(this.fetchNumero(ordreChunk.getSociete()));
      return this.ordreRepository.save(ordreChunk);
    } else {
      Optional<GeoOrdre> ordre = this.ordreRepository.findById(ordreChunk.getId());
      GeoOrdre merged = GeoOrdreGraphQLService.merge(ordreChunk, ordre.get(), null);
      return this.ordreRepository.save(merged);
    }
  }

  public List<GeoOrdre> save(List<GeoOrdre> ordresChunk) {
    Stream<GeoOrdre> mappedOrdres = ordresChunk.stream()
    .map(chunk -> {
      if (chunk.getId() == null) {
        chunk.setNumero(this.fetchNumero(chunk.getSociete()));
        return chunk;
      }
      Optional<GeoOrdre> ordre = this.ordreRepository.findById(chunk.getId());
      return GeoOrdreGraphQLService.merge(chunk, ordre.get(), null);
    });

    return this.ordreRepository.saveAll(mappedOrdres.collect(Collectors.toList()));

  }

  public GeoOrdre clone(GeoOrdre chunk) {
    GeoOrdre original = this.ordreRepository.getOne(chunk.getId());
    GeoOrdre clone = original.duplicate();
    return this.save(clone);
  }

  private Specification<GeoMRUOrdre> groupedByNumero() {
		return (root, criteriaQuery, criteriaBuilder) -> {

      Subquery<GeoMRUOrdre> subquery = criteriaQuery.subquery(GeoMRUOrdre.class);
      Root<GeoMRUOrdre> r = subquery.from(GeoMRUOrdre.class);

      subquery.select(r.get("numero"))
      .having(criteriaBuilder.lessThanOrEqualTo(
        criteriaBuilder.greatest(root.<LocalDateTime>get("dateModification")),
        LocalDateTime.now()
      ))
      .groupBy(r.get("numero"))
      .distinct(true);

      return criteriaBuilder.in(root.get("numero")).value(subquery);
		};
  }
  
  public RelayPage<GeoMRUOrdre> fetchGroupedMRUOrdre(String search, Pageable pageable) {
    Page<GeoMRUOrdre> page;

    if (pageable == null)
      pageable = PageRequest.of(0, 20);
    
    Specification<GeoMRUOrdre> spec = this
    .groupedByNumero()
    .and(this.parseSearch(search));

    page = this.mruOrdreRepository.findAll(spec, pageable);

    return PageFactory.fromPage(page);
  }

  /**
   * Call `calculMarge` and catch errors
   * @param ordreChunk
   * @return True if OK, false in case of error
   */
  public Boolean filterCalculMarge(GeoOrdre ordreChunk) {
    try {
      this.calculMarge(ordreChunk.getId());
    } catch (Exception e) {
      OrdreService.logger.info("Ordre calcul marge: " + "( Ordre ID " + ordreChunk.getId() + " ) " + e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Ordre calcul marge
   * @param ordreID
   * @throws RuntimeException
   */
  public void calculMarge(String ordreID) {

    Optional<GeoOrdre> foundOrdre = this.ordreRepository.findById(ordreID);
    if(!foundOrdre.isPresent())
      throw new RuntimeException("ordre inconnu");

    GeoOrdre ordre = foundOrdre.get();
		GeoFactureAvoir factureAvoir;

		// variables de calcul au niveau de l"entête (ordre)
		Double
      totalTransport,
      totalFraisMarketing,
      totalTransit,
      totalCourtage;

		// variables de totalisation définitive au niveau de l"entete (ordre)
		Double totalVente, totalAchat;
		Double totalFraisAdditionnels;
		Float nombrePalettesAuSol, nombrePalettes100x120, nombrePalettes80x120, nombrePalettes60x80;

		// indicateurs de recalcul nécessaire au niveau des lignes
		Boolean
			ya_trp = false, // indicateur de recalcul des lignes sur transport au camion (unité)
			ya_trs = false, // idem transit
			ya_crt = false, // idem courtage
			ya_frd = false, // idem frais marketing
			ya_fad = false; // frais additionnels sur l"ordre

    String baseTarifTransportCode = ordre.getBaseTarifTransport() != null ?
      ordre.getBaseTarifTransport().getId() : "";
    String baseTarifTransitCode = ordre.getBaseTarifTransit() != null ?
      ordre.getBaseTarifTransit().getId() : "";
    String baseTarifCourtageCode = ordre.getBaseTarifCourtage() != null ?
      ordre.getBaseTarifCourtage().getId() : "";

    Optional<Float> prixUnitaireTransport = Optional
    .ofNullable(ordre.getPrixUnitaireTarifTransport());
    Optional<Float> prixUnitaireTransit = Optional
    .ofNullable(ordre.getPrixUnitaireTarifTransit());
    Optional<Float> prixUnitaireCourtage = Optional
    .ofNullable(ordre.getPrixUnitaireTarifCourtage());
    
    Optional<Double> fraisPrixUnitaire = Optional
    .ofNullable(ordre.getFraisPrixUnitaire());
    String fraisUnite = ordre.getFraisUnite() != null ?
      ordre.getFraisUnite().getId() : "";

		// on calcule le chiffre d"affaires, les objectifs de marge par fournisseur et les frais au niveau des lignes
    List<GeoOrdreLigne> ordreLignes = this.ordreLigneRepository
    .findByOrdreAndMatchingFournisseur(ordre);

    if(ordreLignes.isEmpty())
      throw new RuntimeException("aucunes lignes trouvées");

    for (GeoOrdreLigne ordreLigne : ordreLignes)
      this.updateLigne(ordreLigne);

    // on récupère les cumuls de quantités et montant des lignes de l'ordre
    Optional<GeoOrdreLigneCumul> cumulLignes = this.getLignesCumul(ordre);

    if(cumulLignes.isEmpty())
      throw new RuntimeException("echec des calculs du cumul des lignes");

    // calcul transport
    switch (baseTarifTransportCode) {
      case "COLIS":
        totalTransport = cumulLignes.get().getNombreColisExpedies() * prixUnitaireTransport.orElse(0f).doubleValue();
        break;
      case "PAL":
        totalTransport = cumulLignes.get().getNombrePalettesAuSolExpediees() * prixUnitaireTransport.orElse(0f);
        break;
      case "KILO":
        totalTransport = cumulLignes.get().getPoidsNetExpedie() * prixUnitaireTransport.orElse(0f).doubleValue();
        break;
      case "TONNE":
        totalTransport = cumulLignes.get().getPoidsNetExpedie() * prixUnitaireTransport.orElse(0f).doubleValue() / 1000;
        break;
      default:
        totalTransport = prixUnitaireTransport.orElse(0f).doubleValue();
        break;
    }

    if(totalTransport != 0) ya_trp = true;

    // calcul transit
    switch (baseTarifTransitCode) {
      case "COLIS":
        totalTransit = cumulLignes.get().getNombreColisExpedies() * prixUnitaireTransit.orElse(0f).doubleValue();
        break;
      case "PAL":
        totalTransit = cumulLignes.get().getNombrePalettesExpediees()* prixUnitaireTransit.orElse(0f).doubleValue();
        break;
      case "KILO":
        totalTransit = cumulLignes.get().getPoidsNetExpedie() * prixUnitaireTransit.orElse(0f).doubleValue();
        break;
      case "TONNE":
        totalTransit = cumulLignes.get().getPoidsNetExpedie() * prixUnitaireTransit.orElse(0f).doubleValue() / 1000;
        break;
      default:
        totalTransit = prixUnitaireTransit.orElse(0f).doubleValue();
        break;
    }

    if(totalTransit != 0) ya_trs = true;

    // calcul courtage
    switch (baseTarifCourtageCode) {
      case "COLIS":
        totalCourtage = cumulLignes.get().getNombreColisExpedies() * prixUnitaireCourtage.orElse(0f).doubleValue();
        break;
      case "PAL":
        totalCourtage = cumulLignes.get().getNombrePalettesExpediees() * prixUnitaireCourtage.orElse(0f).doubleValue();
        break;
      case "KILO":
        totalCourtage = cumulLignes.get().getPoidsNetExpedie() * prixUnitaireCourtage.orElse(0f).doubleValue();
        break;
      case "TONNE":
        totalCourtage = cumulLignes.get().getPoidsNetExpedie() * prixUnitaireCourtage.orElse(0f).doubleValue() / 1000;
        break;
      case "PCENT":
        totalCourtage = cumulLignes.get().getTotalVenteBrut() * prixUnitaireCourtage.orElse(0f) / 100;
        break;
      default:
        totalCourtage = prixUnitaireCourtage.orElse(0f).doubleValue();
        break;
    }

    if(totalCourtage != 0) ya_crt = true;

    // calcul frais marketing au niveau de l'ordre
    switch (fraisUnite) {
      case "COLIS":
        totalFraisMarketing = cumulLignes.get().getNombreColisExpedies() * fraisPrixUnitaire.orElse(0d);
        break;
      case "PAL":
        totalFraisMarketing = cumulLignes.get().getNombrePalettesExpediees() * fraisPrixUnitaire.orElse(0d);
        break;
      case "KILO":
        totalFraisMarketing = cumulLignes.get().getPoidsNetExpedie() * fraisPrixUnitaire.orElse(0d);
        break;
      case "TONNE":
        totalFraisMarketing = cumulLignes.get().getPoidsNetExpedie() * fraisPrixUnitaire.orElse(0d) / 1000;
        break;
      case "PCENT":
        totalFraisMarketing = cumulLignes.get().getTotalVenteBrut() * fraisPrixUnitaire.orElse(0d) / 100;
        break;
      default:
        totalFraisMarketing = fraisPrixUnitaire.orElse(0d);
        break;
    }

    if(totalFraisMarketing != 0) ya_frd = true;

    // frais additionnels ordre
    Optional<Float> cumulFrais = this.ordreFraisRepository
    .findByOrdre(ordre)
    .stream()
    .map( f -> f.getMontant() * f.getDeviseTaxe())
    .reduce((acm, crt) -> acm += crt);
    if(cumulFrais.isPresent()) {
      totalFraisAdditionnels = cumulFrais.get().doubleValue();
      ya_fad = true;
    }
    else totalFraisAdditionnels = 0d;

    totalAchat = cumulLignes.get().getTotalAchat();	// cumul lignes
    totalVente = cumulLignes.get().getTotalVenteBrut();	// cumul lignes

    if(totalVente < 0) factureAvoir = GeoFactureAvoir.AVOIR;
    // Cas des litiges vente a zéro mais avoir fournisseur, pour les minis il faut que l'ordre soit identifié comme un avoir
    else if(totalVente == 0 && totalAchat < 0)
		  factureAvoir = GeoFactureAvoir.AVOIR;
    else factureAvoir = GeoFactureAvoir.FACTURE;

    Optional<GeoOrdreLogistique> cumulLogistique = ordre.getLogistiques()
    .stream()
    .reduce((acm, crt) -> {
      acm.setNombrePalettesAuSol(acm.getNombrePalettesAuSol() + crt.getNombrePalettesAuSol());
      acm.setNombrePalettes100x120(acm.getNombrePalettes100x120() + crt.getNombrePalettes100x120());
      acm.setNombrePalettes80x120(acm.getNombrePalettes80x120() + crt.getNombrePalettes80x120());
      acm.setNombrePalettes60X80(acm.getNombrePalettes60X80() + crt.getNombrePalettes60X80());
      return acm;
    });

    nombrePalettesAuSol = cumulLogistique.get().getNombrePalettesAuSol();
    nombrePalettes100x120 = cumulLogistique.get().getNombrePalettes100x120();
    nombrePalettes80x120 = cumulLogistique.get().getNombrePalettes80x120();
    nombrePalettes60x80 = cumulLogistique.get().getNombrePalettes60X80();

    // Update ordre
    ordre.setFactureAvoir(factureAvoir);
    ordre.setTotalPalette(cumulLignes.get().getNombrePalettesExpediees());
    ordre.setTotalColis(cumulLignes.get().getNombreColisExpedies());
    ordre.setTotalPoidsNet(cumulLignes.get().getPoidsNetExpedie());
    ordre.setTotalPoidsBrut(cumulLignes.get().getPoidsBrutExpedie().floatValue());
    ordre.setTotalVente(totalVente.floatValue());
    ordre.setTotalRemise(cumulLignes.get().getTotalRemise());
    ordre.setTotalRestitue(cumulLignes.get().getTotalRestitue());
    ordre.setTotalFraisMarketing(cumulLignes.get().getTotalFraisMarketing());
    ordre.setTotalAchat(totalAchat);
    ordre.setTotalObjectifMarge(cumulLignes.get().getTotalObjectifMarge());
    ordre.setTotalTransport(totalTransport.floatValue());
    ordre.setTotalTransit(totalTransit.floatValue());
    ordre.setTotalCourtage(totalCourtage.floatValue());
    ordre.setTotalFraisAdditionnels(totalFraisAdditionnels.floatValue());
    ordre.setTotalNombrePalettesCommandees(cumulLignes.get().getNombrePalettesCommandees());
    ordre.setTotalNombrePalettesExpediees(cumulLignes.get().getNombrePalettesExpediees());
    ordre.setNombrePalettesAuSol(nombrePalettesAuSol);
    ordre.setNombrePalettes100x120(nombrePalettes100x120);
    ordre.setNombrePalettes60X80(nombrePalettes60x80);
    ordre.setNombrePalettes80x120(nombrePalettes80x120);
    this.ordreRepository.save(ordre);

    // Update logistiques
    List<GeoOrdreLigneSummed> summedLignes =  this.ordreLigneRepository
    .getSummedLignesByOrdreGroupByFournisseur(ordre);
    for(GeoOrdreLigneSummed summedLigne : summedLignes) {
      GeoOrdreLogistique logistique = summedLigne.getLogistique();
      logistique.setTotalPalettesCommandees(summedLigne.getNombrePalettesCommandees().floatValue());
      logistique.setTotalPalettesExpediees(summedLigne.getNombrePalettesExpediees().floatValue());
      this.ordreLogistiqueRepository.save(logistique);
    }

    Integer lignesTotal = 0;
    Integer lignesLitige = 0; // On compte les lignes du litige    
    Float totalNombrePalettesAuSolCamion = 0f;
    Float nombrePalettesAuSolCamionExpediees = 0f;
    List<Float> tabRepartitionTransport = new ArrayList<>();
    List<Float> tabRepartitionTransportExp = new ArrayList<>();
    List<String> tabOrlRef = new ArrayList<>();

    if (ya_trp || ya_trs || ya_crt || ya_frd || ya_fad || ordre.getFraisPlateforme() > 0) {

      for (GeoOrdreLigne ligne : ordreLignes) {

        Optional<Float> ligneNombrePalettesExpediees;
        Optional<Float> ligneNombreColisExpedies;
        Optional<Float> lignePoidsNetExpedie;
        Float ligneTotalTransport = 0f;
        Float ligneTotalTransit = 0f;
        Float ligneTotalCourtage = 0f;
        Float ligneTotalFraisAdditionnels = 0f;
        Optional<Double> ligneTotalFraisMarketing;
        Float nombrePalettesAuSolCamion;

        lignesLitige += 1;
  
        if(ligne.getLogistique().getExpedieStation()){
          ligneNombrePalettesExpediees = Optional
          .ofNullable(ligne.getNombrePalettesExpediees());
          ligneNombreColisExpedies = Optional
          .ofNullable(ligne.getNombreColisExpedies());
          lignePoidsNetExpedie = Optional
          .ofNullable(ligne.getPoidsNetExpedie());
        }
        else {
          ligneNombrePalettesExpediees = Optional
          .ofNullable(ligne.getNombrePalettesCommandees());
          ligneNombreColisExpedies = Optional
          .ofNullable(ligne.getNombreColisCommandes());
          lignePoidsNetExpedie = Optional
          .ofNullable(ligne.getPoidsNetCommande());
        }

        ligneTotalTransport = ligne.getTotalTransport();
        ligneTotalTransit = ligne.getTotalTransit();
        ligneTotalCourtage = ligne.getTotalCourtage();
        ligneTotalFraisMarketing = Optional.ofNullable(ligne.getTotalFraisMarketing());
        ligneTotalFraisAdditionnels = ligne.getTotalFraisAdditionnels();

        if(ligne.getNombreColisPalette() == null || ligne.getNombreColisPalette() == 0) {
          nombrePalettesAuSolCamion = ligne.getLogistique().getExpedieStation() ?
            ligne.getNombrePalettesExpediees() :
            ligne.getNombrePalettesCommandees();
        }
        else {
          nombrePalettesAuSolCamion = ligne.getLogistique().getExpedieStation() ?
            ligne.getNombreColisExpedies() :
            ligne.getNombreColisCommandes();
          if(ligne.getIndicateurPalette() == 1) {
            nombrePalettesAuSolCamion /= ligne.getNombreColisPalette() * 2;
          } else {
            nombrePalettesAuSolCamion /= ligne.getNombreColisPalette();
            nombrePalettesAuSolCamion /= ligne.getNombrePalettesIntermediaires() + 1;
          }
        }

        if(ya_trp)
          switch (ordre.getBaseTarifTransport().getId()){
            case "COLIS":
              ligneTotalTransport = ligneNombreColisExpedies.get() * ordre.getPrixUnitaireTarifTransport();
              break;
            case "PAL":
              if(nombrePalettesAuSolCamion > 0){
                lignesTotal += 1;
                tabRepartitionTransport.add(lignesTotal, nombrePalettesAuSolCamion);
                totalNombrePalettesAuSolCamion += nombrePalettesAuSolCamion;
                tabOrlRef.add(lignesTotal,ligne.getId());
                tabRepartitionTransportExp.add(lignesTotal, ligneNombrePalettesExpediees.get());
              }
              nombrePalettesAuSolCamionExpediees += ligneNombrePalettesExpediees.get();
              break;
            case "KILO":
              ligneTotalTransport = lignePoidsNetExpedie.get() * ordre.getPrixUnitaireTarifTransport();
              break;
            case "TONNE":
              ligneTotalTransport = lignePoidsNetExpedie.get() * ordre.getPrixUnitaireTarifTransport() / 1000;
              break;
            default:
              if(ordre.getTotalPoidsNet() != 0)
                ligneTotalTransport = ordre.getTotalTransport() * lignePoidsNetExpedie.get() / ordre.getTotalPoidsNet();
              break;
          }

        if(ya_trs)
          switch (ordre.getBaseTarifTransit().getId()){
            case "COLIS":
              ligneTotalTransit = ligneNombreColisExpedies.get() * ordre.getPrixUnitaireTarifTransit();
              break;
            case "PAL":
              ligneTotalTransit = ligneNombrePalettesExpediees.get() * ordre.getPrixUnitaireTarifTransit();
              break;
            case "KILO":
              ligneTotalTransit = lignePoidsNetExpedie.get() * ordre.getPrixUnitaireTarifTransit();
              break;
            case "TONNE":
              ligneTotalTransit = lignePoidsNetExpedie.get() * ordre.getPrixUnitaireTarifTransit() / 1000;
              break;
            case "PCENT":
              ligneTotalTransit = ligne.getTotalVenteBrut().floatValue() * ordre.getPrixUnitaireTarifTransit() / 100;
              break;
            default:
              if(ordre.getTotalPoidsNet() != 0)
                ligneTotalTransit = ordre.getTotalTransit() * lignePoidsNetExpedie.get() / ordre.getTotalPoidsNet();
              break;
          }
        
        if(ya_crt)
          switch (ordre.getBaseTarifTransit().getId()){
            case "COLIS":
              ligneTotalCourtage = ligneNombreColisExpedies.get() * ordre.getPrixUnitaireTarifCourtage();
              break;
            case "PAL":
              ligneTotalCourtage = ligneNombrePalettesExpediees.get() * ordre.getPrixUnitaireTarifCourtage();
              break;
            case "KILO":
              ligneTotalCourtage = lignePoidsNetExpedie.get() * ordre.getPrixUnitaireTarifCourtage();
              break;
            case "TONNE":
              ligneTotalCourtage = lignePoidsNetExpedie.get() * ordre.getPrixUnitaireTarifCourtage() / 1000;
              break;
            case "PCENT":
              ligneTotalCourtage = ligne.getTotalVenteBrut().floatValue() * ordre.getPrixUnitaireTarifCourtage() / 100;
              break;
            default:
              if(ordre.getTotalPoidsNet() != 0)
                ligneTotalCourtage = ordre.getTotalCourtage() * lignePoidsNetExpedie.get() / ordre.getTotalPoidsNet();
              break;
          }
        
        if(ya_frd)
          switch (ordre.getFraisUnite().getId()){
            case "COLIS":
              ligneTotalFraisMarketing = Optional.of(ligneTotalFraisMarketing.orElse(0d) + ligneNombreColisExpedies.get() * ordre.getFraisPrixUnitaire());
              break;
            case "PAL":
              ligneTotalFraisMarketing = Optional.of(ligneTotalFraisMarketing.orElse(0d) + ligneNombrePalettesExpediees.get() * ordre.getFraisPrixUnitaire());
              break;
            case "KILO":
              ligneTotalFraisMarketing = Optional.of(ligneTotalFraisMarketing.orElse(0d) + lignePoidsNetExpedie.get() * ordre.getFraisPrixUnitaire());
              break;
            case "TONNE":
              ligneTotalFraisMarketing = Optional.of(ligneTotalFraisMarketing.orElse(0d) + lignePoidsNetExpedie.get() * ordre.getFraisPrixUnitaire() / 1000);
              break;
            case "PCENT":
              ligneTotalFraisMarketing = Optional.of(ligneTotalFraisMarketing.orElse(0d) + ligne.getTotalVenteBrut().floatValue() * ordre.getFraisPrixUnitaire() / 100);
              break;
            default:
              if(ordre.getTotalPoidsNet() != 0)
                ligneTotalFraisMarketing = Optional.of(ligneTotalFraisMarketing.orElse(0d) + ordre.getTotalFraisMarketing() * lignePoidsNetExpedie.get() / ordre.getTotalPoidsNet());
              break;
          }

          if(ya_fad)
            // Répartition des frais en fonction de la vente
            if(ordre.getTotalVente() != 0)
              ligneTotalFraisAdditionnels = ordre.getTotalFraisAdditionnels() * ligne.getTotalVenteBrut().floatValue() / ordre.getTotalVente();
            // Si le montant de la vente totale est nul (peu probable mais on sait jamais) les frais annexes ne sont pas répartis sur les lignes mais uniquement sur la premère
            else if(lignesLitige == 1)
              ligneTotalFraisAdditionnels = ordre.getTotalFraisAdditionnels();
          
          ligne.setTotalTransport(ligneTotalTransport);
          ligne.setTotalTransit(ligneTotalTransit);
          ligne.setTotalCourtage(ligneTotalCourtage);
          ligne.setTotalFraisMarketing(ligneTotalFraisMarketing.get());
          ligne.setTotalFraisAdditionnels(ligneTotalFraisAdditionnels);
          this.ordreLigneRepository.save(ligne);

      }

      // REPARTION PAR LIGNE DE LA VOLUMETRIE DES PALETTES DANS LE CAMION
      if(lignesTotal > 0) {
        if(nombrePalettesAuSolCamionExpediees >= totalNombrePalettesAuSolCamion){
          ordre.setTotalTransport(totalNombrePalettesAuSolCamion * ordre.getPrixUnitaireTarifTransport());
          if(totalNombrePalettesAuSolCamion == 0)
            totalNombrePalettesAuSolCamion = 1f;
          Float ligneTotalTransport = 0f;
          for (int i = 1; i < lignesTotal; i++) {
            GeoOrdreLigne ligne = this.ordreLigneRepository
            .getOne(tabOrlRef.get(i));
            if(i != lignesTotal){
              ligne.setTotalTransport((tabRepartitionTransport.get(i) / totalNombrePalettesAuSolCamion) * ordre.getTotalTransport());
              ligneTotalTransport += ligne.getTotalTransport();
            } else
              ligne.setTotalTransport(ordre.getTotalTransport() - ligneTotalTransport);
            this.ordreLigneRepository.save(ligne);
          }
        } else {
          for (int i = 1; i < lignesTotal; i++) {
            GeoOrdreLigne ligne = this.ordreLigneRepository
            .getOne(tabOrlRef.get(i));
            ligne.setTotalTransport(tabRepartitionTransportExp.get(i) * ordre.getPrixUnitaireTarifTransport());
            this.ordreLigneRepository.save(ligne);
          }
          ordre.setTotalTransport(nombrePalettesAuSolCamionExpediees * ordre.getPrixUnitaireTarifTransport());
        }

        this.ordreRepository.save(ordre);

      }

      if(ordre.getFraisPlateforme() > 0){
        Float totalFraisPlateforme = 0f;
        for (GeoOrdreLigne ligne : ordreLignes) {
          
          Float poidsNet = ligne.getLogistique().getExpedieStation() ?
            ligne.getPoidsNetExpedie() :
            ligne.getPoidsNetCommande();
          GeoGenre genre = ligne.getEspece().getGenre();
          if(genre.getId() != "F" || genre.getId() != "L") continue;

          if(poidsNet > 0) {
            ligne.setTotalFraisAdditionnels(ligne.getTotalFraisAdditionnels() + poidsNet * ordre.getFraisPlateforme());
            totalFraisPlateforme += ligne.getTotalFraisAdditionnels();
          }

        }
        ordre.setTotalFraisPlateforme(totalFraisPlateforme);
        ordre.setTotalFraisAdditionnels(ordre.getTotalFraisAdditionnels() + totalFraisPlateforme);
        this.ordreRepository.save(ordre);
      }
      
    }

	}

  /**
   * Calculate quantite, poids-net, expedition
   * @param ordreID
   * @param ordreLigneID
   */
  public void calculQuantite(String ordreID, String ordreLigneID) {
    GeoOrdreLigne ligne = this.ordreLigneRepository.getOne(ordreLigneID);

    Float nombreColisCommandes = ligne.getNombreColisCommandes();
    // TODO WAITING FOR INFORMATION ABOUT PDNET_CLIENT FIELD
    Double poidsNetClient = 0d;
    Float tare = ligne.getArticle().getEmballage().getEmballage().getTare();

    Double poidsNet = poidsNetClient * nombreColisCommandes;
    Double poidsBrut = poidsNet + (tare * nombreColisCommandes);

    ligne.setPoidsBrutCommande(poidsBrut.floatValue());
    ligne.setPoidsNetCommande(poidsNet.floatValue());

    this.ordreLigneRepository.save(ligne);

  }

  private void updateLigne(GeoOrdreLigne ligne) {
    if(ligne.getFournisseur() == null)
        throw new RuntimeException("calcul impossible si il manque des fournisseurs ( ligne " + ligne.getId() + " )");

      Optional<Float> ligneNombrePalettesExpediees;
      Optional<Float> ligneNombreColisExpedies;
      Optional<Float> lignePoidsNetExpedie;
      Optional<Float> ligneVentePrixUnitaire;
      Optional<Double> ligneVenteQuantite;
      Optional<Double> ligneAchatPrixUnitaire;
      Optional<Double> ligneAchatQuantite;
      Double ligneTotalVenteBrut;
      Double ligneTotalRemise;
      Double ligneTotalFraisMarketing;
      Double ligneTotalAchat;
      Double ligneTotalObjectifMarge;
      Optional<Boolean> ligneRistourne;
      Optional<Double> ligneFraisPrixUnitaire;
      String ligneFraisUniteCode;
      Boolean ligneExpedieStation;
      Optional<Float> ligneMargeObjectifEuroKilo;
      Optional<Float> ligneMargeObjectifPourcentCa;

      Optional<Float> tauxRemiseFacture = Optional
      .ofNullable(ligne.getOrdre().getTauxRemiseFacture());
      Optional<Float> tauxRemiseHorsFacture = Optional
      .ofNullable(ligne.getOrdre().getTauxRemiseHorsFacture());
      Optional<Double> tauxDevise = Optional
      .ofNullable(ligne.getOrdre().getTauxDevise());

      if(ligne.getLogistique().getExpedieStation()){
        ligneNombrePalettesExpediees = Optional
        .ofNullable(ligne.getNombrePalettesExpediees());
        ligneNombreColisExpedies = Optional
        .ofNullable(ligne.getNombreColisExpedies());
        lignePoidsNetExpedie = Optional
        .ofNullable(ligne.getPoidsNetExpedie());
      }
      else {
        ligneNombrePalettesExpediees = Optional
        .ofNullable(ligne.getNombrePalettesCommandees());
        ligneNombreColisExpedies = Optional
        .ofNullable(ligne.getNombreColisCommandes());
        lignePoidsNetExpedie = Optional
        .ofNullable(ligne.getPoidsNetCommande());
      }

      ligneVentePrixUnitaire = Optional
      .ofNullable(ligne.getVentePrixUnitaire());
      ligneVenteQuantite = Optional
      .ofNullable(ligne.getVenteQuantite());
      ligneTotalVenteBrut = ligne.getTotalVenteBrut();
      ligneAchatPrixUnitaire = Optional
      .ofNullable(ligne.getAchatPrixUnitaire());
      ligneAchatQuantite = Optional
      .ofNullable(ligne.getAchatQuantite());
      ligneTotalRemise = ligne.getTotalRemise().doubleValue();
      ligneTotalFraisMarketing = ligne.getTotalFraisMarketing();
      ligneTotalAchat = ligne.getTotalAchat();
      ligneTotalObjectifMarge = ligne.getTotalObjectifMarge().doubleValue();
      ligneRistourne = Optional
      .ofNullable(ligne.getRistourne());
      ligneFraisPrixUnitaire = Optional
      .ofNullable(ligne.getFraisPrixUnitaire());
      ligneFraisUniteCode = ligne.getFraisUnite() != null ?
        ligne.getFraisUnite().getId() : "KILO";
      ligneExpedieStation = ligne.getLogistique().getExpedieStation();

      ligneMargeObjectifEuroKilo = Optional
      .ofNullable(ligne.getFournisseur().getMargeObjectifEuroKilo());
      ligneMargeObjectifPourcentCa = Optional
      .ofNullable(ligne.getFournisseur().getMargeObjectifPourcentCa());

      if(ligneExpedieStation)
        this.calculQuantite(ligne.getOrdre().getId(), ligne.getId());
  
      // calculs au niveau ligne
      ligneTotalVenteBrut = ligneVenteQuantite.orElse(0d) * ligneVentePrixUnitaire.orElse(0f) * tauxDevise.orElse(0d); // vente brute en euros
      ligneTotalRemise = ligneRistourne.orElse(false) ?
        (ligneTotalVenteBrut * (tauxRemiseFacture.orElse(0f) + tauxRemiseHorsFacture.orElse(0f))) / 100.0 : 0d;
      ligneTotalAchat	= ligneAchatQuantite.orElse(0d) * ligneAchatPrixUnitaire.orElse(0d);
      ligneTotalObjectifMarge	= ligneTotalVenteBrut * ligneMargeObjectifPourcentCa.orElse(0f) / 100 + ligneMargeObjectifEuroKilo.orElse(0f) * lignePoidsNetExpedie.orElse(0f);
      
      // calcul frais marketing niveau ligne
      switch (ligneFraisUniteCode) {
        case "COLIS":
          ligneTotalFraisMarketing = ligneNombreColisExpedies.orElse(0f) * ligneFraisPrixUnitaire.orElse(0d);
          break;
        case "PAL":
          ligneTotalFraisMarketing = ligneNombrePalettesExpediees.orElse(0f) * ligneFraisPrixUnitaire.orElse(0d);
          break;
        case "KILO":
          ligneTotalFraisMarketing = lignePoidsNetExpedie.orElse(0f) * ligneFraisPrixUnitaire.orElse(0d);
          break;
        case "TONNE":
          ligneTotalFraisMarketing = lignePoidsNetExpedie.orElse(0f) * ligneFraisPrixUnitaire.orElse(0d) / 1000;
          break;
        case "PCENT":
          ligneTotalFraisMarketing = ligneTotalVenteBrut * ligneFraisPrixUnitaire.orElse(0d) / 100;
          break;
        case "UNITE":
          ligneTotalFraisMarketing = ligneFraisPrixUnitaire.orElse(0d);
          break;
        default:
          ligneTotalFraisMarketing = ligneVenteQuantite.orElse(0d) * ligneFraisPrixUnitaire.orElse(0d);
          break;
      }

      ligne.setTotalVenteBrut(ligneTotalVenteBrut);
      ligne.setTotalRemise(ligneTotalRemise.floatValue());
      ligne.setTotalRestitue(0f);
      ligne.setTotalFraisMarketing(ligneTotalFraisMarketing);
      ligne.setTotalAchat(ligneTotalAchat);
      ligne.setTotalObjectifMarge(ligneTotalObjectifMarge.floatValue());
      ligne.setTotalTransport(0f);
      ligne.setTotalTransit(0f);
      ligne.setTotalCourtage(0f);
      ligne.setTotalFraisAdditionnels(0f);
      this.ordreLigneRepository.save(ligne);

  }

  private Optional<GeoOrdreLigneCumul> getLignesCumul(GeoOrdre ordre) {
    return this.ordreLigneRepository
    .findByOrdre(ordre)
    .stream()
    .map(olc -> {
      GeoOrdreLogistique logistique = olc.getLogistique();
      olc.setNombrePalettesExpediees(logistique.getExpedieStation() ? olc.getNombrePalettesExpediees() : olc.getNombrePalettesCommandees());
      olc.setNombreColisExpedies(logistique.getExpedieStation() ? olc.getNombreColisExpedies() : olc.getNombreColisCommandes());
      olc.setPoidsNetExpedie(logistique.getExpedieStation() ? olc.getPoidsNetExpedie() : olc.getPoidsNetCommande());
      olc.setPoidsBrutExpedie(logistique.getExpedieStation() ? olc.getPoidsBrutExpedie() : olc.getPoidsBrutCommande());

      olc.setNombrePalettesAuSolExpediees((logistique.getExpedieStation() ? olc.getNombrePalettesExpediees() : olc.getNombrePalettesCommandees()).doubleValue() - olc.getIndicateurPalette());
      return olc;
    })
    .reduce((acm, crt) -> {
      acm.setNombrePalettesExpediees(acm.getNombrePalettesExpediees() + crt.getNombrePalettesExpediees());
      acm.setNombreColisExpedies(acm.getNombreColisExpedies() + crt.getNombreColisExpedies());
      acm.setPoidsNetExpedie(acm.getPoidsNetExpedie() + crt.getPoidsNetExpedie());
      acm.setPoidsBrutExpedie(acm.getPoidsBrutExpedie() + crt.getPoidsBrutExpedie());
      acm.setTotalVenteBrut(acm.getTotalVenteBrut() + crt.getTotalVenteBrut());
      acm.setTotalRemise(acm.getTotalRemise() + crt.getTotalRemise());
      acm.setTotalRestitue(acm.getTotalRestitue() + crt.getTotalRestitue());
      acm.setTotalFraisMarketing(acm.getTotalFraisMarketing() + crt.getTotalFraisMarketing());
      acm.setTotalAchat(acm.getTotalAchat() + crt.getTotalAchat());
      acm.setTotalObjectifMarge(acm.getTotalObjectifMarge() + crt.getTotalObjectifMarge());
      acm.setNombrePalettesCommandees(acm.getNombrePalettesCommandees() + crt.getNombrePalettesCommandees());
      acm.setNombrePalettesAuSolExpediees(acm.getNombrePalettesAuSolExpediees() + crt.getNombrePalettesAuSolExpediees());
      return acm;
    })
    .map(lc -> {
      lc.setNombrePalettesAuSolExpediees(lc.getNombrePalettesAuSolExpediees() * .5);
      return lc;
    });
  }

  /**
   * Call `calculMarge` and catch errors
   * @param ordreChunk
   * @return True if OK, false in case of error
   */
  public Boolean filterVerifOrdreWarning(GeoOrdre ordreChunk) {
    try {
      this.verifOrdreWarning(ordreChunk.getId());
    } catch (Exception e) {
      OrdreService.logger.info("Ordre verif: " + "( Ordre ID " + ordreChunk.getId() + " ) " + e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Check ordre validity
   * @param ordreID
   */
  public void verifOrdreWarning(String ordreID) {

    List<String> messages = new ArrayList<String>();
    SecurityContext sc = SecurityContextHolder.getContext();
		GeoUtilisateur user = (GeoUtilisateur)sc.getAuthentication().getPrincipal();
    Optional<GeoOrdre> ordre = this.ordreRepository.findById(ordreID);
    if(!ordre.isPresent())
      throw new RuntimeException("ordre inconnu");

    this.ordreLigneRepository.countByOrdre(ordre.get());
    this.ordreLigneRepository.countByOrdreAndGratuitIsTrue(ordre.get());

    // ajuste montant maxi ligne en fonction de la devise de la société
    Integer maxMontantLigne = 1;
    if(ordre.get().getSociete().getDevise().getId() == "EUR")
      maxMontantLigne	= 30000;
    if(ordre.get().getSociete().getDevise().getId() == "GBP")
      maxMontantLigne	= 50000;
    
    String controlReferenceClient = Optional
    .ofNullable(ordre.get().getEntrepot().getControlReferenceClient())
    .orElse(ordre.get().getClient().getControlReferenceClient());
    
    if(ordre.get().getClient().getUsageInterne())
      messages.add("(A) %%% le client est à usage interne pas de facturation");
      
    if(ordre.get().getClient().getFraisRamasse() && ordre.get().getTotalFraisAdditionnels() == 0f)
      messages.add("(T) il n'y a pas de frais de ramasse");
      
    if(ordre.get().getVenteACommission())
      messages.add("(P) c'est une  vente à la commission");

    // verif statut bon à facturer OK	
    GeoFlux fluxOrdre = this.fluxRepository.getOne("ORDRE");
    if(ordre.get().getBonAFacturer())
      messages.add("(A) %%% l'ordre est déja bon à facturer");
    else if(this.envoisRepository.countByOrdreAndFlux(ordre.get(), fluxOrdre) == 0)
      messages.add("(A) Aucune confirmation n'a été effectuée");
      
    // Vérifi si le transporteur est valide
    if(ordre.get().getTransporteur().getId() == "-")
      messages.add("(T) %%% Transporteur à préciser");

    // verif ordre pere bon à facturer si avoir
    if(ordre.get().getFactureAvoir() == GeoFactureAvoir.AVOIR && ordre.get().getOrdrePere().getId() != ""){
      Optional<GeoOrdre> ordrePere = this.ordreRepository
      .findByIdAndMatchingOrdrePere(ordre.get().getId());
      if(ordrePere.isPresent() && !ordrePere.get().getBonAFacturer())
        messages.add("(A) %%%  vous devez clotûrer l'ordre " + Optional.ofNullable(ordre.get().getNumero()).orElse("d'origine") + " avant de clotûrer l'avoir correspondant");
    }

    // Autorisation ORDRE de régulation
    GeoOrdreType[] typesRegulation = {GeoOrdreType.REG,GeoOrdreType.RPR,GeoOrdreType.RPO};
    Boolean typeOK = Arrays.asList(typesRegulation).contains(ordre.get().getType());
    if(!user.getAccessGeoFacture() && typeOK)
      messages.add("(A) %%% Seul ADV peut mettre BAF les ordres de régulation");
      
    // date de livraison vide  ?
    if(ordre.get().getDateLivraisonPrevue() == null)
      messages.add("(D) %%% la date de livraison est obligatoire");

    // date de départ vide  ?
    Boolean oldDate = ordre.get().getDateDepartPrevue().equals(LocalDate.ofYearDay(1900,1));
    if(ordre.get().getDateDepartPrevue() == null || oldDate)
      messages.add("(D) %%% la date de départ est obligatoire");
      
    if(ordre.get().getDateDepartPrevue().compareTo(ordre.get().getDateLivraisonPrevue()) < 0)
      messages.add("(D) %%% la date de départ (" + ordre.get().getDateDepartPrevue().format(DateTimeFormatter.BASIC_ISO_DATE) + ") est incohérente par rapport à la  date de livraison  (" + ordre.get().getDateLivraisonPrevue().format(DateTimeFormatter.BASIC_ISO_DATE) + ")");
      
    // le code incoterm est nécessaire à la facturation
    if(ordre.get().getIncoterm() == null)
      messages.add("(T) %%% le code incoterm est obligatoire");
    
    // coût du transport selon procédure ? que dans le cas des factures pas des avoirs
    if(
      ordre.get().getTransporteur().getId() != "CLIENT" &&
      ordre.get().getTotalTransport() == 0 &&
      ordre.get().getIncoterm().getRenduDepart() != 'D' &&
      ordre.get().getFactureAvoir() == GeoFactureAvoir.FACTURE &&
      this.ordreLigneRepository.countByOrdre(ordre.get()) != this.ordreLigneRepository.countByOrdreAndGratuitIsTrue(ordre.get()) &&
      ordre.get().getSociete().getId() != "BUK"
    )
      messages.add("(T) %%% le coût prévisionnel du transport est obligatoire pour l' incoterm " + ordre.get().getIncoterm().getId());

  }
}
