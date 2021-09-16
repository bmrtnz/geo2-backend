package fr.microtec.geo2.service.graphql;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

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
    super(paysRepository);
  }

  public Float fetchSommeAgrement(GeoPays pays) {
    return this
    .fetchSum(pays, "clients.agrement")
    .floatValue();
  }

  public Number fetchSum(GeoPays pays, String fieldPath){
    CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Number> criteriaQuery = criteriaBuilder.createQuery(Number.class);
    Root<GeoPays> root = criteriaQuery.from(GeoPays.class);

		criteriaQuery.select(criteriaBuilder.sum(CriteriaUtils.toExpressionRecursively(root, fieldPath, false)));
    criteriaQuery.where(criteriaBuilder.equal(root.get("id"), pays.getId()));
    criteriaQuery.where(criteriaBuilder.equal(root.get("valide"), true));

		TypedQuery<Number> q = this.entityManager.createQuery(criteriaQuery);
    return q.getSingleResult();
  }

}
