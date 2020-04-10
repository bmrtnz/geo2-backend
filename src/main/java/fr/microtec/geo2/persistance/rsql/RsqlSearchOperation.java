package fr.microtec.geo2.persistance.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Define RSQL supported operator.
 */
@Getter
public enum RsqlSearchOperation {
	EQUALS(RSQLOperators.EQUAL),
	NOT_EQUALS(RSQLOperators.NOT_EQUAL),
	LESS_THAN(RSQLOperators.LESS_THAN),
	LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
	GREATER_THAN(RSQLOperators.GREATER_THAN),
	GREATER_THAN_OR_EQUALS(RSQLOperators.GREATER_THAN_OR_EQUAL),
	IN(RSQLOperators.IN),
	NOT_IN(RSQLOperators.NOT_IN),
	IS_NULL(new ComparisonOperator("=isnull=", "=null=")),
	IS_NOT_NULL(new ComparisonOperator("=isnotnull=", "=notnull=")),
	LIKE(new ComparisonOperator("=like=")),
	NOT_LIKE(new ComparisonOperator("=notlike=")),
	BETWEEN(new ComparisonOperator("=bt=", "=between=", true)),
	NOT_BETWEEN(new ComparisonOperator("=nbt=", "=notbetween=", true));
	// CONTAIN(new ComparisonOperator("=c=", "=contain=")),
	// NOT_CONTAIN(new ComparisonOperator("=nc=", "=notcontain="));

	private ComparisonOperator operator;

	RsqlSearchOperation(final ComparisonOperator operator) {
		this.operator = operator;
	}

	public static RsqlSearchOperation getSimpleOperator(final ComparisonOperator operator) {
		return Arrays.stream(values())
				.filter(o -> o.getOperator().equals(operator))
				.findFirst()
				.orElse(null);
	}

	public static Set<ComparisonOperator> supportedOperators() {
		return Arrays.stream(values()).map(RsqlSearchOperation::getOperator).collect(Collectors.toSet());
	}
}
