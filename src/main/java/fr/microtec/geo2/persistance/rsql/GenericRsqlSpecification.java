package fr.microtec.geo2.persistance.rsql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.entity.ordres.GeoCahierDesCharges;
import fr.microtec.geo2.persistance.entity.ordres.GeoFactureAvoir;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreType;
import fr.microtec.geo2.persistance.entity.ordres.GeoStatus;
import fr.microtec.geo2.persistance.entity.ordres.GeoStatusGEO;
import fr.microtec.geo2.persistance.entity.tiers.GeoRole;

/**
 * Generic specification for Rsql query.
 */
public class GenericRsqlSpecification<T> implements Specification<T> {

	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd['T'HH:mm:ss]"; // ISO 8601 with optional time
	private static final DateTimeFormatter DATE_TIME_FORMATTER;
	static {
		// Default value of optional time.
		// TODO use : DateTimeFormatter.ISO_INSTANT ?
		DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
				.appendPattern(DATE_TIME_PATTERN)
				.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
				.parseDefaulting(ChronoField.MINUTE_OF_DAY, 0)
				.parseDefaulting(ChronoField.SECOND_OF_DAY, 0)
				.toFormatter();
	}

	private final String property;
	private final RsqlSearchOperation operator;
	private final List<String> arguments;

	public GenericRsqlSpecification(String property, ComparisonOperator operator, List<String> arguments) {
		this.property = property;
		this.operator = RsqlSearchOperation.getSimpleOperator(operator);
		this.arguments = arguments;
	}

	/**
	 * Convert this specification to predicate.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		Expression<?> expression = this.parseExpression(root, criteriaBuilder);

		List<Object> args = this.castArgument(expression);

		RsqlSearchOperation operator = this.operator.isNegative() || this.operator.isCaseInsensitive() ?
				this.operator.getReelOperator() : this.operator;

		Predicate predicate = null;
		switch (operator) {
			case EQUALS:
				predicate = criteriaBuilder.equal(expression, args.get(0));
				break;
			case NOT_EQUALS:
				predicate = criteriaBuilder.notEqual(expression, args.get(0));
				break;
			case GREATER_THAN:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.greaterThan(cast(expression), parseToLocalDate(args.get(0)));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.greaterThan(cast(expression), parseToLocalDateTime(args.get(0)));
				else
					predicate = criteriaBuilder.greaterThan(cast(expression), args.get(0).toString());
				break;
			case GREATER_THAN_OR_EQUALS:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.greaterThanOrEqualTo(cast(expression), parseToLocalDate(args.get(0)));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.greaterThanOrEqualTo(cast(expression), parseToLocalDateTime(args.get(0)));
				else
					predicate = criteriaBuilder.greaterThanOrEqualTo(cast(expression), args.get(0).toString());
				break;
			case LESS_THAN:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.lessThan(cast(expression), parseToLocalDate(args.get(0)));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.lessThan(cast(expression), parseToLocalDateTime(args.get(0)));
				else
					predicate = criteriaBuilder.lessThan(cast(expression), args.get(0).toString());
				break;
			case LESS_THAN_OR_EQUAL:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.lessThanOrEqualTo(cast(expression), parseToLocalDate(args.get(0)));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.lessThanOrEqualTo(cast(expression), parseToLocalDateTime(args.get(0)));
				else
					predicate = criteriaBuilder.lessThanOrEqualTo(cast(expression), args.get(0).toString());
				break;
			case IN:
				predicate = expression.in(args);
				break;
			case IS_NULL:
				predicate = criteriaBuilder.isNull(expression);
				break;
			case IS_NOT_NULL:
				predicate = criteriaBuilder.isNotNull(expression);
				break;
			case LIKE:
				predicate = criteriaBuilder.like(cast(expression), args.get(0).toString());
				break;
			case BETWEEN:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.between(cast(expression), parseToLocalDate(args.get(0)), parseToLocalDate(args.get(1)));
				else
					predicate = criteriaBuilder.between(cast(expression), args.get(0).toString(), args.get(1).toString());
				break;
			default:
				throw new RsqlException(String.format("Unimplemented RSQL operator '%s'", this.operator));
		}

		if (this.operator.isNegative()) {
			predicate = predicate.not();
		}

		return predicate;
	}

	/**
	 * Parse property to expression.
	 * If current operator has case-insensitive condition, wrap expression to upper case function.
	 *
	 * @param root The root type.
	 * @param builder The criteria builder.
	 * @return Parsed expression.
	 */
	private <Y> Expression<Y> parseExpression(Root<?> root, CriteriaBuilder builder) {
		Expression<Y> expression = CriteriaUtils.toExpressionRecursively(root, this.property, false);

		if (this.operator.isCaseInsensitive()) {
			expression = cast(builder.upper(cast(expression)));
		}

		return expression;
	}

	/**
	 * Cast unknown generic expression type to require generic type.
	 *
	 * @param expression The expression to cast.
	 * @param <Y> The required type.
	 * @return Expression casted with Y require type.
	 */
	private <Y> Expression<Y> cast(Expression<?> expression) {
		return (Expression<Y>) expression;
	}

	/**
	 * Cast argument string value to property class type.
	 * If current operator has case-insensitive condition, convert string to upper case.
	 *
	 * @param expression Property expression.
	 * @return Parsed arguments.
	 */
	private List<Object> castArgument(Expression<?> expression) {
		Class<?> type = expression.getJavaType();

		return arguments.stream().map(arg -> {
			try {
				if (type.equals(Integer.class)) {
					return Integer.parseInt(arg);
				} else if (type.equals(Long.class)) {
					return Long.parseLong(arg);
				} else if (type.equals(Float.class)) {
					return Float.parseFloat(arg);
				} else if (type.equals(BigDecimal.class)) {
					return BigDecimal.valueOf(Double.parseDouble(arg));
				} else if (type.equals(Boolean.class)) {
					return Boolean.parseBoolean(arg);
				} else if (type.equals(LocalDate.class)) {
					return parseToLocalDate(arg);
				} else if (type.equals(LocalDateTime.class)) {
					return parseToLocalDateTime(arg);
				} else if (type.equals(Character.class)) {
					return arg.charAt(0);
				} else if (type.equals(String.class)) {
					if (this.operator.isCaseInsensitive()) {
						arg = arg.toUpperCase();
					}

					return arg;
				}
				// TODO Make enums generic
				else if (type.equals(GeoFactureAvoir.class)) {
					return GeoFactureAvoir.findByAbbr(arg);
				} else if (type.equals(GeoRole.class)) {
					return GeoRole.findByAbbr(arg);
				} else if (type.equals(GeoOrdreType.class)) {
					return GeoOrdreType.findByAbbr(arg);
				} else if (type.equals(GeoStatusGEO.class)) {
					return GeoStatusGEO.findByAbbr(arg);
				} else if (type.equals(GeoStatus.class)) {
					return GeoStatus.findByAbbr(arg);
				} else if (type.equals(GeoCahierDesCharges.class)) {
					return GeoCahierDesCharges.findByAbbr(arg);
				} else {
					throw new RsqlException(String.format("Unknown type '%s' for parsing", type.getSimpleName()));
				}
			} catch (Exception e) {
				throw new RsqlException("Error in value parsing", e);
			}
		}).collect(Collectors.toList());
	}

	/**
	 * Parse Object to ISO LocalDate
	 * 
	 * @param o Object argument
	 * @return ISO LocalDate
	 */
	private static LocalDate parseToLocalDate(Object o) {
		return LocalDate.parse(o.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	}

	/**
	 * Parse Object to ISO LocalDateTime
	 * 
	 * @param o Object argument
	 * @return ISO LocalDateTime
	 */
	private static LocalDateTime parseToLocalDateTime(Object o) {
		return LocalDateTime.parse(o.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
