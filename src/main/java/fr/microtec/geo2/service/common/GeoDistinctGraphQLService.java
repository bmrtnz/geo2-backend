package fr.microtec.geo2.service.common;

import cz.jirutka.rsql.parser.RSQLParser;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.EntityUtils;
import fr.microtec.geo2.persistance.entity.Distinct;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Service
@GraphQLApi
public class GeoDistinctGraphQLService {

	private final EntityManager entityManager;
	private final RSQLParser rsqlParser;

	public GeoDistinctGraphQLService(EntityManager entityManager, RSQLParser rsqlParser) {
		this.entityManager = entityManager;
		this.rsqlParser = rsqlParser;
	}

	@GraphQLQuery
	public RelayPage<Distinct> getDistinct(
			@GraphQLArgument(name = "type") String inputType,
			@GraphQLArgument(name = "field") String requestField,
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		Specification<?> spec = null;
		if (search != null && !search.isBlank()) {
			spec = this.rsqlParser.parse(search).accept(new GeoCustomVisitor<>());
		}

		Page<Distinct> result = this.readPage(spec, pageable, inputType, requestField);

		return PageFactory.fromPage(result);
	}

	/**
	 * Read page with criteria query.
	 * Query select distinct {requestedField} from associated table on {inputType} entity.
	 * Apply specification if present and paging parameters.
	 *
	 * @param spec Specification to apply.
	 * @param pageable Pageable parameter.
	 * @param inputType Entity class name.
	 * @param requestedField Entity requested field.
	 * @return Request page data.
	 */
	private Page<Distinct> readPage(
			Specification<?> spec, Pageable pageable,
			String inputType, String requestedField
	) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Distinct> criteriaQuery = criteriaBuilder.createQuery(Distinct.class);

		final Class<?> entityClass = EntityUtils.getEntityClassFromName(inputType);
		Root root = this.applySpecification(spec, criteriaQuery, entityClass);
		Expression<?> requestedFieldExpr = root.get(requestedField);
		Expression<Long> countIdExpr = criteriaBuilder.count(root.get(root.getModel().getDeclaredId(String.class)));

		criteriaQuery
				.multiselect(requestedFieldExpr, countIdExpr)
				.groupBy(requestedFieldExpr)
				.distinct(true);

		// Order
		Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
		if (sort.isSorted()) {
			criteriaQuery.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));
		}

		TypedQuery<Distinct> query = this.entityManager.createQuery(criteriaQuery);

		if (pageable.isPaged()) {
			query.setFirstResult((int) pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
		}

		TypedQuery<Long> countQuery = this.buildCountQuery(spec, entityClass, requestedFieldExpr);

		return PageableExecutionUtils.getPage(
				query.getResultList(),
				pageable,
				() -> this.executeCountQuery(countQuery)
		);
	}

	/**
	 * Build count query.
	 *
	 * @param spec Specification to apply.
	 * @param entityClass Target entity class.
	 * @param requestedFieldExpr Entity requested field expression.
	 * @return Count query.
	 */
	private TypedQuery<Long> buildCountQuery(Specification<?> spec, Class<?> entityClass, Expression<?> requestedFieldExpr) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		this.applySpecification(spec, query, entityClass);
		query.select(builder.countDistinct(requestedFieldExpr));

		return this.entityManager.createQuery(query);
	}

	/**
	 * Execute and fetch count query.
	 *
	 * @param query Query to execute.
	 * @return Count result.
	 */
	private long executeCountQuery(TypedQuery<Long> query) {
		List<Long> totals = query.getResultList();
		long total = 0L;

		for (Long element : totals) {
			total += element == null ? 0 : element;
		}

		return total;
	}

	/**
	 * Apply specification to criteria query.
	 *
	 * @param spec Specification to apply.
	 * @param query Criteria query.
	 * @param entityClass Target entity class.
	 * @return Query root.
	 */
	private Root<?> applySpecification(Specification<?> spec, CriteriaQuery<?> query, Class<?> entityClass) {
		Root root = query.from(entityClass);

		if (spec == null) {
			return root;
		}

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		Predicate predicate = spec.toPredicate(root, query, builder);

		if (predicate != null) {
			query.where(predicate);
		}

		return root;
	}

}
