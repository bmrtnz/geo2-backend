package fr.microtec.geo2.service;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.repository.ordres.GeoMRUOrdreRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service()
public class MRUOrdreService extends GeoAbstractGraphQLService<GeoMRUOrdre, GeoMRUOrdreKey> {

  @PersistenceContext
  private EntityManager entityManager;

  private final GeoMRUOrdreRepository mruOrdreRepository;

  public MRUOrdreService(GeoMRUOrdreRepository mruOrdreRepository) {
    super(mruOrdreRepository, GeoMRUOrdre.class);
    this.mruOrdreRepository = mruOrdreRepository;
  }

  private Specification<GeoMRUOrdre> groupedByNumero() {
    return (root, criteriaQuery, criteriaBuilder) -> {

      Subquery<GeoMRUOrdre> subquery = criteriaQuery.subquery(GeoMRUOrdre.class);
      Root<GeoMRUOrdre> r = subquery.from(GeoMRUOrdre.class);

      subquery.select(r.get("numero"))
          .having(criteriaBuilder.lessThanOrEqualTo(
              criteriaBuilder.greatest(root.<LocalDateTime>get("dateModification")), LocalDateTime.now()))
          .groupBy(r.get("numero")).distinct(true);

      return criteriaBuilder.in(root.get("numero")).value(subquery);
    };
  }

  public RelayPage<GeoMRUOrdre> fetchGroupedMRUOrdre(String search, Pageable pageable) {
    Page<GeoMRUOrdre> page;

    if (pageable == null)
      pageable = PageRequest.of(0, 20);

    Specification<GeoMRUOrdre> spec = this.groupedByNumero().and(this.parseSearch(search));

    page = this.mruOrdreRepository.findAll(spec, pageable);

    return PageFactory.fromPage(page);
  }

}
