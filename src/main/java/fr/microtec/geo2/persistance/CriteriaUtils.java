package fr.microtec.geo2.persistance;

import fr.microtec.geo2.persistance.entity.Distinct;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.*;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

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
	public static CriteriaQuery<Distinct> selectCountDistinct(CriteriaBuilder cb, Class<?> entityClass, String requestedField, Specification<?> spec) {
		CriteriaQuery<Distinct> query = cb.createQuery(Distinct.class);
		Root<?> root = applySpecification(cb, query, entityClass, spec);

		Expression<?> distinctExpression = toExpressionRecursively(root, requestedField, true);
		Expression<?> idExpression = root.get(root.getModel().getDeclaredId(root.getModel().getIdType().getJavaType()).getName());

		query.multiselect(distinctExpression, cb.count(idExpression)).groupBy(distinctExpression).distinct(true);

		return query;
	}

	public static CriteriaQuery<Long> countDistinct(CriteriaBuilder cb, Class<?> entityClass, String requestedField, Specification<?> spec) {
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Long> root = (Root<Long>) applySpecification(cb, query, entityClass, spec);

		Expression<?> distinctExpression = toExpressionRecursively(root, requestedField, true);
		Expression<?> idExpression = root.get(root.getModel().getDeclaredId(root.getModel().getIdType().getJavaType()).getName());

		query.multiselect(cb.count(idExpression)).groupBy(distinctExpression);

		return query;
	}

	private static Root<?> applySpecification(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> rootClass, Specification<?> spec) {
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

	static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {

		for (Join<?, ?> join : from.getJoins()) {

			boolean sameName = join.getAttribute().getName().equals(attribute);

			if (sameName && join.getJoinType().equals(JoinType.LEFT)) {
				return join;
			}
		}

		return from.join(attribute, JoinType.LEFT);
	}

}
