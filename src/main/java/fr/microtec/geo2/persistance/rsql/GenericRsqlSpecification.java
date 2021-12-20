package fr.microtec.geo2.persistance.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import fr.microtec.geo2.common.TemporalUtils;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.StringEnum;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic specification for Rsql query.
 */
public class GenericRsqlSpecification<T> implements Specification<T> {

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
					predicate = criteriaBuilder.greaterThan(CriteriaUtils.cast(expression), (LocalDate)args.get(0));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.greaterThan(CriteriaUtils.cast(expression), (LocalDateTime)args.get(0));
				else
					predicate = criteriaBuilder.greaterThan(CriteriaUtils.cast(expression), args.get(0).toString());
				break;
			case GREATER_THAN_OR_EQUALS:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.greaterThanOrEqualTo(CriteriaUtils.cast(expression), (LocalDate)args.get(0));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.greaterThanOrEqualTo(CriteriaUtils.cast(expression), (LocalDateTime)args.get(0));
				else
					predicate = criteriaBuilder.greaterThanOrEqualTo(CriteriaUtils.cast(expression), args.get(0).toString());
				break;
			case LESS_THAN:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.lessThan(CriteriaUtils.cast(expression), (LocalDate)args.get(0));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.lessThan(CriteriaUtils.cast(expression), (LocalDateTime)args.get(0));
				else
					predicate = criteriaBuilder.lessThan(CriteriaUtils.cast(expression), args.get(0).toString());
				break;
			case LESS_THAN_OR_EQUAL:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.lessThanOrEqualTo(CriteriaUtils.cast(expression), (LocalDate)args.get(0));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.lessThanOrEqualTo(CriteriaUtils.cast(expression), (LocalDateTime)args.get(0));
				else
					predicate = criteriaBuilder.lessThanOrEqualTo(CriteriaUtils.cast(expression), args.get(0).toString());
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
				predicate = criteriaBuilder.like(CriteriaUtils.cast(expression), args.get(0).toString());
				break;
			case BETWEEN:
				if (expression.getJavaType().equals(LocalDate.class))
					predicate = criteriaBuilder.between(CriteriaUtils.cast(expression), TemporalUtils.parseToLocalDate(args.get(0)), TemporalUtils.parseToLocalDate(args.get(1)));
				else if (expression.getJavaType().equals(LocalDateTime.class))
					predicate = criteriaBuilder.between(CriteriaUtils.cast(expression), TemporalUtils.parseToLocalDateTime(args.get(0)), TemporalUtils.parseToLocalDateTime(args.get(1)));
				else
					predicate = criteriaBuilder.between(CriteriaUtils.cast(expression), args.get(0).toString(), args.get(1).toString());
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
			expression = CriteriaUtils.cast(builder.upper(CriteriaUtils.cast(expression)));
		}

		return expression;
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
					return TemporalUtils.parseToLocalDate(arg);
				} else if (type.equals(LocalDateTime.class)) {
					return TemporalUtils.parseToLocalDateTime(arg);
				} else if (type.equals(Character.class)) {
					return arg.charAt(0);
				} else if (type.equals(String.class)) {
					if (this.operator.isCaseInsensitive()) {
						arg = arg.toUpperCase();
					}

					return arg;
				} else if (StringEnum.class.isAssignableFrom(type)) {
					Class<?>[] interfaces = type.getInterfaces();

					for (Class<?> inter : interfaces) {
						if (StringEnum.class.equals(inter)) {
							return inter.getMethod("getValueOf", Class.class, String.class).invoke(null, type, arg);
						}
					}

					throw new RsqlException(String.format("Unknown enum const '%s' for parsing value '%s'", type.getSimpleName(), arg));
				} else {
					throw new RsqlException(String.format("Unknown type '%s' for parsing", type.getSimpleName()));
				}
			} catch (Exception e) {
				throw new RsqlException("Error in value parsing : " + e.getMessage(), e);
			}
		}).collect(Collectors.toList());
	}

}
