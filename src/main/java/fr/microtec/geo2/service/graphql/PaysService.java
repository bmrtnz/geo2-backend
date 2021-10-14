package fr.microtec.geo2.service.graphql;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import fr.microtec.geo2.persistance.repository.tiers.GeoPaysRepository;

@Service
public class PaysService extends GeoAbstractGraphQLService<GeoPays, String> {
  
  @PersistenceContext
  private EntityManager entityManager;

  public PaysService(
    GeoPaysRepository paysRepository
  ) {
    super(paysRepository, GeoPays.class);
  }

  public Number fetchSum(GeoPays pays, String fieldPath){
    CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Number> criteriaQuery = criteriaBuilder.createQuery(Number.class);
    Root<GeoPays> root = criteriaQuery.from(GeoPays.class);

		criteriaQuery
    .select(criteriaBuilder.sum(CriteriaUtils.toExpressionRecursively(root, fieldPath, false)))
    .where(criteriaBuilder.and(
      criteriaBuilder.equal(root.get("id"), pays.getId()),
      criteriaBuilder.equal(root.get("valide"), true)
    ));

		TypedQuery<Number> q = this.entityManager.createQuery(criteriaQuery);
    return q.getSingleResult();
  }

  public RelayPage<GeoPays> fetchDistinctPays(String search, Pageable pageable) {
		if (pageable == null)
			pageable = PageRequest.of(0, 20);

    Specification<GeoPays> spec = Specification.where(null);

      spec = (root, query, cb) -> {
        query = query.distinct(true);
        return null;
      };

    if(search != null && !search.isBlank())
      spec = spec.and(this.parseSearch(search));
    
    Page<GeoPays> page = this.repository.findAll(spec, pageable);

		return PageFactory.fromPage(page);
  }

}
