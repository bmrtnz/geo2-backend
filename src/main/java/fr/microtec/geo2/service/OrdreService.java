package fr.microtec.geo2.service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.ordres.GeoMRUOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.graphql.ordres.GeoOrdreGraphQLService;

@Service()
public class OrdreService extends GeoAbstractGraphQLService<GeoMRUOrdre, GeoMRUOrdreKey> {

  @PersistenceContext
  private EntityManager entityManager;

  private final GeoOrdreRepository ordreRepository;
  private final GeoMRUOrdreRepository mruOrdreRepository;

  public OrdreService(
    GeoOrdreRepository ordreRepository,
    GeoMRUOrdreRepository mruOrdreRepository
  ) {
    super(mruOrdreRepository);
    this.ordreRepository = ordreRepository;
    this.mruOrdreRepository = mruOrdreRepository;
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

  public GeoOrdre clone(GeoOrdre chunk) {
    GeoOrdre original = this.ordreRepository.getOne(chunk.getId());
    GeoOrdre clone = original.duplicate();
    return this.save(clone);
  }

  public Specification<GeoMRUOrdre> groupedByNumero() {
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

}
