package fr.microtec.geo2.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
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
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service
public class PaysService extends GeoAbstractGraphQLService<GeoPays, String> {

    @PersistenceContext
    private EntityManager entityManager;

    public PaysService(
            GeoPaysRepository paysRepository) {
        super(paysRepository, GeoPays.class);
    }

    public Float fetchSum(GeoPays pays, String fieldPath) {
        return this.fetchSum(pays, fieldPath, false);
    }

    public Float fetchSum(GeoPays pays, String fieldPath, Boolean ignoreNegative) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Float> criteriaQuery = criteriaBuilder.createQuery(Float.class);
        Root<GeoPays> root = criteriaQuery.from(GeoPays.class);

        Expression<Float> exp = CriteriaUtils.toExpressionRecursively(root, fieldPath, false);
        Predicate equalID = criteriaBuilder.equal(root.get("id"), pays.getId());
        Predicate isValid = criteriaBuilder.equal(root.get("valide"), true);
        Predicate isNegative = criteriaBuilder.greaterThan(exp, Float.valueOf(0));

        criteriaQuery
                .select(criteriaBuilder.sum(exp))
                .where(criteriaBuilder.and(equalID, isValid));

        if (ignoreNegative)
            criteriaQuery.where(criteriaBuilder.and(equalID, isValid, isNegative));

        TypedQuery<Float> q = this.entityManager.createQuery(criteriaQuery);
        final Float singleResult = q.getSingleResult();
        return (singleResult == null) ? 0 : singleResult;
    }

    public RelayPage<GeoPays> fetchDistinctPays(String search, Pageable pageable) {
        if (pageable == null)
            pageable = PageRequest.of(0, 20);

        Specification<GeoPays> spec = Specification.where(null);

        spec = (root, query, cb) -> {
            query = query.distinct(true);
            return null;
        };

        if (search != null && !search.isBlank())
            spec = spec.and(this.parseSearch(search));

        Page<GeoPays> page = this.repository.findAll(spec, pageable);

        return PageFactory.asRelayPage(page);
    }

}
