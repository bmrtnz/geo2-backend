package fr.microtec.geo2.configuration.graphql;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum SummaryType implements StringEnum {
	sum("sum"),
	min("min"),
	max("max"),
	count("count"),
	avg("avg");

	private String key;

	SummaryType(String key) {
		this.key = key;
	}

	public Expression<Double> buildExpression(Expression<?> inputExpression, CriteriaBuilder builder) {
		if (this == sum)
			return builder.sum(CriteriaUtils.cast(inputExpression));
		else if (this == avg)
			return builder.avg(CriteriaUtils.cast(inputExpression));
		else if (this == count)
			return builder.count(CriteriaUtils.cast(inputExpression)).as(Double.class);
		else if (this == min)
			return builder.min(CriteriaUtils.cast(inputExpression));
		else if (this == max)
			return builder.max(CriteriaUtils.cast(inputExpression));
		else
			throw new RuntimeException("Unknown Summary aggregate function");
		}
}