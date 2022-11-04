package fr.microtec.geo2.persistance;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_MANY;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_ONE;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_MANY;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_ONE;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import fr.microtec.geo2.common.CustomUtils;
import fr.microtec.geo2.configuration.graphql.Summary;
import fr.microtec.geo2.persistance.entity.Distinct;

/**
 * This code is principally copied from Spring Data QueryUtils class because the code is inaccessible.
 * @see org.springframework.data.jpa.repository.query.QueryUtils
 */
public class CriteriaUtils {

	private static final Map<Attribute.PersistentAttributeType, Class<? extends Annotation>> ASSOCIATION_TYPES;

	static {
		Map<Attribute.PersistentAttributeType, Class<? extends Annotation>> persistentAttributeTypes = new HashMap<>();
		persistentAttributeTypes.put(ONE_TO_ONE, OneToOne.class);
		persistentAttributeTypes.put(ONE_TO_MANY, null);
		persistentAttributeTypes.put(MANY_TO_ONE, ManyToOne.class);
		persistentAttributeTypes.put(MANY_TO_MANY, null);
		persistentAttributeTypes.put(ELEMENT_COLLECTION, null);

		ASSOCIATION_TYPES = Collections.unmodifiableMap(persistentAttributeTypes);
	}

	/**
	 * Create distinct and count query.
	 *
	 * @param cb Criteria builder.
	 * @param entityClass Target entity class.
	 * @param requestedField Request property path.
	 * @param spec Specification clause.
	 * @return Distinct count query.
	 */
	public static CriteriaQuery<Distinct> selectCountDistinct(CriteriaBuilder cb, Class<?> entityClass, String requestedField, String descriptionField, Specification<?> spec) {
        List<Selection<?>> selectList = new ArrayList<>();
        List<Expression<?>> distinctList = new ArrayList<>();
		CriteriaQuery<Distinct> query = cb.createQuery(Distinct.class);
		Root<?> root = applySpecification(cb, query, entityClass, spec);

		Expression<?> distinctExpression = toExpressionRecursively(root, requestedField, true);
        Expression<?> idExpression = getIdExpression(root);

        selectList.add(distinctExpression);
        distinctList.add(distinctExpression);

        if (descriptionField != null) {
            Expression<?> descriptionExpression = toExpressionRecursively(root, descriptionField, true);

            distinctList.add(descriptionExpression);
            selectList.add(descriptionExpression);
        }

        selectList.add(cb.count(idExpression));

        query
            .multiselect(selectList)
            .groupBy(distinctList)
            .distinct(true);

		return query;
	}

	public static CriteriaQuery<Long> countDistinct(CriteriaBuilder cb, Class<?> entityClass, String requestedField, Specification<?> spec) {
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Long> root = (Root<Long>) applySpecification(cb, query, entityClass, spec);

		Expression<?> distinctExpression = toExpressionRecursively(root, requestedField, true);
		Expression<?> idExpression = getIdExpression(root);

		query.multiselect(cb.count(idExpression)).groupBy(distinctExpression);

		return query;
	}

	public static Specification<?> groupedBySelection(Set<String> selection) {
    return (root, criteriaQuery, criteriaBuilder) -> {

      List<Expression<?>> expressions = CustomUtils
      .getSelectionExpressions(selection, root);

      criteriaQuery.groupBy(expressions);
      return Specification.where(null)
      .toPredicate(root, criteriaQuery, criteriaBuilder);

    };
  }

	public static <E> CriteriaQuery<?> createSummariesQuery(
		CriteriaBuilder builder,
		Class<E> entityClass,
		Specification<?> spec,
		Set<String> fields,
		List<Summary> summaries
	) {
		CriteriaQuery<?> query = builder.createQuery();
		Root<E> root = (Root<E>) applySpecification(builder, query, entityClass, spec);
		List<Expression<?>> expressions = CustomUtils
		.getSelectionExpressions(fields, root);
		List<Selection<?>> aggregatesSelections = new ArrayList<>();

		expressions.stream().forEach( expression -> {
			Optional<Summary> summary = summaries.stream()
			.filter( s -> s.getSelector().equals(expression.getAlias()))
			.findAny();
			if (summary.isPresent()) {
				Expression<?> aggregateExpression = summary.get()
				.getSummaryType()
				.buildExpression(expression, builder);
				aggregateExpression.alias(expression.getAlias());
				aggregatesSelections.add(aggregateExpression);
			}
		});

		aggregatesSelections.sort((a, b) -> {
			int aIndex = summaries.stream().map(s -> s.getSelector()).collect(Collectors.toList()).indexOf(a.getAlias());
			int bIndex = summaries.stream().map(s -> s.getSelector()).collect(Collectors.toList()).indexOf(b.getAlias());
			return aIndex - bIndex;
		});

		return query.multiselect(aggregatesSelections);
  }

	/**
	 * Get id expression from Root.
	 *
	 * @param root Root.
	 * @return Id expression.
	 */
	public static Expression<?> getIdExpression(Root<?> root) {
		String attributeName;
		Type<?> type = root.getModel().getIdType();
		if(type == null){
			attributeName = root.getModel().getIdClassAttributes().iterator().next().getName();
		}
		else {
			Class<?> clazz = type.getJavaType();
			attributeName = root.getModel().getId(clazz).getName();
		}
		return root.get(attributeName);
	}

	public static Root<?> applySpecification(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> rootClass, Specification<?> spec) {
		Root<?> root = query.from(rootClass);

		if (spec != null) {
			Predicate predicate = spec.toPredicate((Root) root, query, cb);

			if (predicate != null) {
				query.where(predicate);
			}
		}

		return root;
	}

	/**
	 * Find correct root for given class.
	 *
	 * @param query Criteria query.
	 * @param clazz Searched class.
	 * @param <T> Searched class type.
	 * @return Find root.
	 */
	public static <T> Root<T> findRoot(CriteriaQuery<?> query, Class<T> clazz) {
		for (Root<?> root : query.getRoots()) {
			if (clazz.equals(root.getJavaType())) {
				return (Root<T>) root;
			}
		}

		return null;
	}

	/**
	 * Convert spring sort to criteria order.
	 *
	 * @param sort Spring sort.
	 * @param from Query from.
	 * @param cb Criteria builder.
	 * @return List of criteria order.
	 */
	public static List<javax.persistence.criteria.Order> toOrders(Sort sort, From<?, ?> from, CriteriaBuilder cb) {
		List<Order> orders = new ArrayList<>();

		for (org.springframework.data.domain.Sort.Order order : sort) {
			orders.add(toJpaOrder(order, from, cb));
		}

		return orders;
	}

	/**
	 * Convert spring order to criteria order.
	 *
	 * @param order Spring order.
	 * @param from Query from.
	 * @param cb Criteria builder.
	 * @return Criteria order.
	 */
	static Order toJpaOrder(org.springframework.data.domain.Sort.Order order, From<?, ?> from, CriteriaBuilder cb) {
		PropertyPath property = PropertyPath.from(order.getProperty(), from.getJavaType());
		Expression<?> expression = toExpressionRecursively(from, property, false);

		if (order.isIgnoreCase() && String.class.equals(expression.getJavaType())) {
			Expression<String> lower = cb.lower((Expression<String>) expression);
			return order.isAscending() ? cb.asc(lower) : cb.desc(lower);
		} else {
			return order.isAscending() ? cb.asc(expression) : cb.desc(expression);
		}
	}

	public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, String property, boolean isForSelection) {
		PropertyPath propertyPath = PropertyPath.from(property, from.getJavaType());
		return toExpressionRecursively(from, propertyPath, isForSelection);
	}

	public static Expression<Object> toExpressionRecursively(Path<Object> path, PropertyPath property) {
		Path<Object> result = path.get(property.getSegment());
		return property.hasNext() ? toExpressionRecursively(result, property.next()) : result;
	}

	public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property, boolean isForSelection) {
		Bindable<?> propertyPathModel;
		Bindable<?> model = from.getModel();
		String segment = property.getSegment();

		if (model instanceof ManagedType) {

			/*
			 *  Required to keep support for EclipseLink 2.4.x. TODO: Remove once we drop that (probably Dijkstra M1)
			 *  See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=413892
			 */
			propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(segment);
		} else {
			propertyPathModel = from.get(segment).getModel();
		}

		if (requiresOuterJoin(propertyPathModel, model instanceof PluralAttribute, !property.hasNext(), isForSelection)
				&& !isAlreadyFetched(from, segment)) {
			Join<?, ?> join = getOrCreateJoin(from, segment);
			return (Expression<T>) (property.hasNext() ? toExpressionRecursively(join, property.next(), isForSelection)
					: join);
		} else {
			Path<Object> path = from.get(segment);
			return (Expression<T>) (property.hasNext() ? toExpressionRecursively(path, property.next()) : path);
		}
	}

	/**
	 * Return whether the given {@link From} contains a fetch declaration for the attribute with the given name.
	 *
	 * @param from the {@link From} to check for fetches.
	 * @param attribute the attribute name to check.
	 * @return
	 */
	static boolean isAlreadyFetched(From<?, ?> from, String attribute) {

		for (Fetch<?, ?> fetch : from.getFetches()) {

			boolean sameName = fetch.getAttribute().getName().equals(attribute);

			if (sameName && fetch.getJoinType().equals(JoinType.LEFT)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Return whether the given {@link From} contains a join declaration for the attribute with the given name.
	 *
	 * @param from the {@link From} to check for joins.
	 * @param attribute the attribute name to check.
	 * @return
	 */
	public static boolean isAlreadyJoined(From<?, ?> from, String attribute) {

		for (Join<?, ?> join : from.getJoins()) {

			boolean sameName = join.getAttribute().getName().equals(attribute);

			if (sameName) return true;
		}

		return false;
	}

	/**
	 * Returns whether the given {@code propertyPathModel} requires the creation of a join. This is the case if we find a
	 * optional association.
	 *
	 * @param propertyPathModel may be {@literal null}.
	 * @param isPluralAttribute is the attribute of Collection type?
	 * @param isLeafProperty is this the final property navigated by a {@link PropertyPath}?
	 * @param isForSelection is the property navigated for the selection part of the query?
	 * @return whether an outer join is to be used for integrating this attribute in a query.
	 */
	static boolean requiresOuterJoin(@Nullable Bindable<?> propertyPathModel, boolean isPluralAttribute,
	                                         boolean isLeafProperty, boolean isForSelection) {
		if (propertyPathModel == null && isPluralAttribute) {
			return true;
		}

		if (!(propertyPathModel instanceof Attribute)) {
			return false;
		}

		Attribute<?, ?> attribute = (Attribute<?, ?>) propertyPathModel;

		if (!ASSOCIATION_TYPES.containsKey(attribute.getPersistentAttributeType())) {
			return false;
		}

		// if this path is an optional one to one attribute navigated from the not owning side we also need an explicit
		// outer join to avoid https://hibernate.atlassian.net/browse/HHH-12712 and
		// https://github.com/eclipse-ee4j/jpa-api/issues/170
		boolean isInverseOptionalOneToOne = Attribute.PersistentAttributeType.ONE_TO_ONE == attribute.getPersistentAttributeType()
				&& StringUtils.hasText(getAnnotationProperty(attribute, "mappedBy", ""));

		// if this path is part of the select list we need to generate an explicit outer join in order to prevent Hibernate
		// to use an inner join instead.
		// see https://hibernate.atlassian.net/browse/HHH-12999.
		if (isLeafProperty && !isForSelection && !attribute.isCollection() && !isInverseOptionalOneToOne) {
			return false;
		}

		return getAnnotationProperty(attribute, "optional", true);
	}

	private static <T> T getAnnotationProperty(Attribute<?, ?> attribute, String propertyName, T defaultValue) {

		Class<? extends Annotation> associationAnnotation = ASSOCIATION_TYPES.get(attribute.getPersistentAttributeType());

		if (associationAnnotation == null) {
			return defaultValue;
		}

		Member member = attribute.getJavaMember();

		if (!(member instanceof AnnotatedElement)) {
			return defaultValue;
		}

		Annotation annotation = AnnotationUtils.getAnnotation((AnnotatedElement) member, associationAnnotation);
		return annotation == null ? defaultValue : (T) AnnotationUtils.getValue(annotation, propertyName);
	}

	public static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {
		return getOrCreateJoin(from, attribute, JoinType.INNER);
	}

	public static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute, JoinType joinType) {

		for (Join<?, ?> join : from.getJoins()) {

			boolean sameName = join.getAttribute().getName().equals(attribute);

			if (sameName && join.getJoinType().equals(joinType)) {
				return join;
			}
		}

		return from.join(attribute, joinType);
	}

	/**
	 * Cast unknown generic expression type to require generic type.
	 *
	 * @param expression The expression to cast.
	 * @param <Y> The required type.
	 * @return Expression casted with Y require type.
	 */
	public static <Y> Expression<Y> cast(Expression<?> expression) {
		return (Expression<Y>) expression;
	}

}
