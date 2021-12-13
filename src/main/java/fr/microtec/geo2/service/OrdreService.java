package fr.microtec.geo2.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.GeoSequenceGenerator;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitige;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigneTotaux;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.ordres.GeoLitigeLigneRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoLitigeRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoSocieteRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.graphql.ordres.GeoOrdreGraphQLService;
import lombok.val;

@Service()
public class OrdreService extends GeoAbstractGraphQLService<GeoOrdre, String> {

  @PersistenceContext
  private EntityManager entityManager;

  private final GeoOrdreRepository ordreRepository;
  private final GeoLitigeRepository litigeRepository;
  private final GeoLitigeLigneRepository litigeLigneRepository;
  private final GeoSocieteRepository societeRepository;

  public OrdreService(
    GeoOrdreRepository ordreRepository,
    GeoLitigeRepository litigeRepository,
    GeoLitigeLigneRepository litigeLigneRepository,
    GeoSocieteRepository societeRepository
  ) {
    super(ordreRepository, GeoOrdre.class);
    this.ordreRepository = ordreRepository;
    this.litigeRepository = litigeRepository;
    this.litigeLigneRepository = litigeLigneRepository;
    this.societeRepository = societeRepository;
  }

  private String fetchNumero(GeoSociete societe) {
    Properties params = new Properties();

    params.put("sequenceName", String.format("seq_nordre_%s", societe.getId()));
    params.put("isSequence", true);
    params.put("mask", "FM099999");

    return (String) GeoSequenceGenerator.generate(this.entityManager, params);
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
        

  public Float fetchSommeColisCommandes(GeoOrdre ordre) {
    return this
    .fetchSum(ordre, "logistiques.lignes.nombreColisCommandes")
    .floatValue();
  }

  public Float fetchSommeColisExpedies(GeoOrdre ordre) {
    return this
    .fetchSum(ordre, "logistiques.lignes.nombreColisExpedies")
    .floatValue();
  }

  public Number fetchSum(GeoOrdre ordre, String path){
    CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Number> criteriaQuery = criteriaBuilder.createQuery(Number.class);
    Root<GeoOrdre> root = criteriaQuery.from(GeoOrdre.class);

		criteriaQuery.select(criteriaBuilder.sum(CriteriaUtils.toExpressionRecursively(root, path, false)));
    criteriaQuery.where(criteriaBuilder.equal(root.get("id"), ordre.getId()));

		TypedQuery<Number> q = this.entityManager.createQuery(criteriaQuery);
    final Number singleResult = q.getSingleResult();
    return (singleResult == null) ? 0 : singleResult;
  }

  public RelayPage<GeoOrdre> fetchOrdreSuiviDeparts(String search, Pageable pageable, Boolean onlyColisDiff) {
		if (pageable == null)
			pageable = PageRequest.of(0, 20);

    Specification<GeoOrdre> spec = Specification.where(null);

    if(onlyColisDiff)
      spec = (root, query, cb) -> {
        Path<Object> id = root.get("id");

        Subquery<Number> sccSubquery = query.subquery(Number.class);
        Root<GeoOrdre> sccRoot = sccSubquery.from(GeoOrdre.class);
        Expression<Number> scc = cb.sum(CriteriaUtils.toExpressionRecursively(sccRoot, "logistiques.lignes.nombreColisCommandes", false));
        sccSubquery.select(scc);
        sccSubquery.where(cb.equal(id, sccRoot.get("id")));
        
        Subquery<Number> sceSubquery = query.subquery(Number.class);
        Root<GeoOrdre> sceRoot = sceSubquery.from(GeoOrdre.class);
        Expression<Number> sce = cb.sum(CriteriaUtils.toExpressionRecursively(sceRoot, "logistiques.lignes.nombreColisExpedies", false));
        sceSubquery.select(sce);
        sceSubquery.where(cb.equal(id, sceRoot.get("id")));
        
        return cb.notEqual(sccSubquery,sceSubquery);
      };

    if(search != null && !search.isBlank())
      spec = spec.and(this.parseSearch(search));
    
    Page<GeoOrdre> page = this.repository.findAll(spec, pageable);

		return PageFactory.asRelayPage(page);
  }

  public RelayPage<GeoOrdre> fetchOrdresPlanningTransporteurs(String search, Pageable pageable, final Set<String> fields) {

    Specification<GeoOrdre> spec = (Specification<GeoOrdre>)CriteriaUtils.groupedBySelection(fields);

    if(search != null && !search.isBlank())
      spec = spec.and(this.parseSearch(search));

    Page<GeoOrdre> page = this.repository
    .findAllWithPagination(spec, pageable, GeoOrdre.class, fields);
    
		return PageFactory.asRelayPage(page);
  }

  public Optional<GeoLitigeLigneTotaux> fetchLitigeLignesTotaux(String litigeID) {
    GeoLitige litige = this.litigeRepository.getOne(litigeID);
    return this.litigeLigneRepository.getTotaux(litige);
  }

  public Optional<GeoOrdre> getByNumeroAndSociete(String numero, String societeID) {
    GeoSociete societe = this.societeRepository.getOne(societeID);
    return this.ordreRepository.findByNumeroAndSociete(numero, societe);
  }

  /**
   * Return the number of order not closed
   * @param search the search string
   * @return The number of order not closed
   */
  public long fetchNombreOrdreNonCloture(final String search) {

    Specification<GeoOrdre> spec = null;

    if(StringUtils.hasText(search)) {
      spec = Specification.where(this.parseSearch(search));
    }

    return this.repository.count(spec);
  }

  public RelayPage<GeoOrdre> getPlanningTransporteurs(
    String search,
    Pageable pageable,
    LocalDateTime dateMin,
    LocalDateTime dateMax,
    String societeCode,
    String transporteurCode
  ) {
    pageable = PageRequest.of(0, (pageable == null) ? 20 : pageable.getPageSize());
    
    val page = this.ordreRepository
    .getPlanningTransporteurs(
      dateMin,
      dateMax,
      societeCode,
      transporteurCode,
      search.isEmpty() ? null : this.parseSearch(search),
      pageable
    );

		return PageFactory.asRelayPage(page);
  }
}
