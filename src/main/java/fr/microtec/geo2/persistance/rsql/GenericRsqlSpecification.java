package fr.microtec.geo2.persistance.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import fr.microtec.geo2.persistance.EntityUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
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
				predicate = criteriaBuilder.greaterThan(cast(expression), args.get(0).toString());
				break;
			case GREATER_THAN_OR_EQUALS:
				predicate = criteriaBuilder.greaterThanOrEqualTo(cast(expression), args.get(0).toString());
				break;
			case LESS_THAN:
				predicate = criteriaBuilder.lessThan(cast(expression), args.get(0).toString());
				break;
			case LESS_THAN_OR_EQUAL:
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
		Expression<Y> expression = EntityUtils.parseExpression(root, this.property);
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
				} else if (type.equals(Boolean.class)) {
					return Boolean.parseBoolean(arg);
				} else if (type.equals(LocalDate.class)) {
					return LocalDate.parse(arg, DateTimeFormatter.ISO_LOCAL_DATE);
				} else if (type.equals(LocalDateTime.class)) {
					return LocalDateTime.parse(arg, DATE_TIME_FORMATTER);
				} else if (type.equals(Character.class)) {
					return arg.charAt(0);
				} else if (type.equals(String.class)) {
					if (this.operator.isCaseInsensitive()) {
						arg = arg.toUpperCase();
					}

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
