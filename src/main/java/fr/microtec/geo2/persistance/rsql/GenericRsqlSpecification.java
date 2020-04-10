package fr.microtec.geo2.persistance.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic specification for Rsql query.
 */
public class GenericRsqlSpecification<T> implements Specification<T> {

	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd['T'HH:mm:ss]"; // ISO 8601 with optional time
	private static final DateTimeFormatter DATE_TIME_FORMATTER;
	static {
		DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
				.appendPattern(DATE_TIME_PATTERN)
				.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
				.parseDefaulting(ChronoField.MINUTE_OF_DAY, 0)
				.parseDefaulting(ChronoField.SECOND_OF_DAY, 0)
				.toFormatter();
	}

	private final String property;
	private final ComparisonOperator operator;
	private final List<String> arguments;

	public GenericRsqlSpecification(String property, ComparisonOperator operator, List<String> arguments) {
		this.property = property;
		this.operator = operator;
		this.arguments = arguments;
	}

	/**
	 * Convert this specification to predicate.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		List<Object> args = this.castArgument(root);

		switch (RsqlSearchOperation.getSimpleOperator(this.operator)) {
			case EQUALS:
				return criteriaBuilder.equal(root.get(this.property), args.get(0));
			case NOT_EQUALS:
				return criteriaBuilder.notEqual(root.get(this.property), args.get(0));
			case GREATER_THAN:
				return criteriaBuilder.greaterThan(root.get(this.property), args.get(0).toString());
			case GREATER_THAN_OR_EQUALS:
				return criteriaBuilder.greaterThanOrEqualTo(root.get(this.property), args.get(0).toString());
			case LESS_THAN:
				return criteriaBuilder.lessThan(root.get(this.property), args.get(0).toString());
			case LESS_THAN_OR_EQUAL:
				return criteriaBuilder.lessThanOrEqualTo(root.get(this.property), args.get(0).toString());
			case IN:
				return root.get(this.property).in(args);
			case NOT_IN:
				return root.get(this.property).in(args).not();
			case IS_NULL:
				return criteriaBuilder.isNull(root.get(this.property));
			case IS_NOT_NULL:
				return criteriaBuilder.isNotNull(root.get(this.property));
			case LIKE:
				return criteriaBuilder.like(root.get(this.property), args.get(0).toString());
			case NOT_LIKE:
				return criteriaBuilder.notLike(root.get(this.property), args.get(0).toString());
			case BETWEEN:
				return criteriaBuilder.between(root.get(this.property), args.get(0).toString(), args.get(1).toString());
			case NOT_BETWEEN:
				return criteriaBuilder.between(root.get(this.property), args.get(0).toString(), args.get(1).toString()).not();
		}

		return null;
	}

	/**
	 * Cast argument string value to property class type.
	 *
	 * @param root Root type.
	 * @return Parsed arguments.
	 */
	private List<Object> castArgument(Root<T> root) {
		Class<?> type = root.get(this.property).getJavaType();

		return arguments.stream().map(arg -> {
			try {
				if (type.equals(Integer.class)) {
					return Integer.parseInt(arg);
				} else if (type.equals(Long.class)) {
					return Long.parseLong(arg);
				} else if (type.equals(Boolean.class)) {
					return Boolean.parseBoolean(arg);
				} else if (type.equals(LocalDate.class)) {
					return LocalDate.parse(arg, DateTimeFormatter.ISO_LOCAL_DATE);
				} else if (type.equals(LocalDateTime.class)) {
					return LocalDateTime.parse(arg, DATE_TIME_FORMATTER);
				} else if (type.equals(String.class)) {
					return arg;
				} else {
					throw new RsqlException(String.format("Unknown type '%s' for parsing", type.getSimpleName()));
				}
			} catch (Exception e) {
				throw new RsqlException("Error in value parsing", e);
			}
		}).collect(Collectors.toList());
	}
}
