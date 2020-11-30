package fr.microtec.geo2.persistance.repository;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Optional;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

public class SimpleGeoRepository<T, ID extends Serializable>
		extends SimpleJpaRepository<T, ID>
		implements GeoRepository<T, ID> {

	private final JpaEntityInformation<T, ?> entityInformation;
	private final EntityManager em;

	public SimpleGeoRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityInformation = entityInformation;
		this.em = entityManager;
	}

	public SimpleGeoRepository(Class<T> domainClass, EntityManager em) {
		this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
	}

	@Override
	public Page<T> findAll(Specification<T> specification, Pageable pageable, EntityGraph entityGraph) {
		TypedQuery<T> query = this.getQuery(specification, pageable, entityGraph);

		return pageable.isUnpaged() ? new PageImpl<T>(query.getResultList())
				: readPage(query, getDomainClass(), pageable, specification);
	}

	@Override
	public Page<T> findAll(Pageable pageable, EntityGraph entityGraph) {
		if (pageable.isUnpaged()) {
			return new PageImpl<T>(findAll());
		}

		return findAll((Specification<T>) null, pageable);
	}

	@Override
	public Optional<T> findById(ID id, EntityGraph entityGraph) {
		Class<T> domainType = getDomainClass();
		TypedQuery<T> query = this.getQuery(null, domainType, Sort.unsorted(), entityGraph);

		return Optional.ofNullable(query.getSingleResult());
	}

	protected TypedQuery<T> getQuery(@Nullable Specification<T> spec, Pageable pageable, EntityGraph entityGraph) {
		Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();

		return getQuery(spec, getDomainClass(), sort, entityGraph);
	}

	protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass, Sort sort, EntityGraph entityGraph) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<S> query = builder.createQuery(domainClass);

		Root<S> root = applySpecificationToCriteria(spec, domainClass, query);
		query.select(root);

		if (sort.isSorted()) {
			query.orderBy(toOrders(sort, root, builder));
		}

		return this.em.createQuery(query);
	}

	/**
	 * Applies the given {@link Specification} to the given {@link CriteriaQuery}.
	 *
	 * @param spec can be {@literal null}.
	 * @param domainClass must not be {@literal null}.
	 * @param query must not be {@literal null}.
	 * @return
	 */
	private <S, U extends T> Root<U> applySpecificationToCriteria(@Nullable Specification<U> spec, Class<U> domainClass,
	                                                              CriteriaQuery<S> query) {
		Assert.notNull(domainClass, "Domain class must not be null!");
		Assert.notNull(query, "CriteriaQuery must not be null!");

		Root<U> root = query.from(domainClass);

		if (spec == null) {
			return root;
		}

		CriteriaBuilder builder = em.getCriteriaBuilder();
		Predicate predicate = spec.toPredicate(root, query, builder);

		if (predicate != null) {
			query.where(predicate);
		}

		return root;
	}
}
