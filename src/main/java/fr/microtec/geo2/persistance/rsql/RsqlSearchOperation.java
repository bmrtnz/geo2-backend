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
	EQUALS(new ComparisonOperator("==", "=eq=", true)),
	NOT_EQUALS(new ComparisonOperator("!=", "=neq=", true)),
	CASE_INSENSITIVE_EQUALS(new ComparisonOperator("=ieq="), false, true, EQUALS),
	CASE_INSENSITIVE_NOT_EQUALS(new ComparisonOperator("=ineq="), false, true, NOT_EQUALS),

	LESS_THAN(RSQLOperators.LESS_THAN),
	LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
	GREATER_THAN(RSQLOperators.GREATER_THAN),
	GREATER_THAN_OR_EQUALS(RSQLOperators.GREATER_THAN_OR_EQUAL),

	IN(RSQLOperators.IN),
	NOT_IN(RSQLOperators.NOT_IN, true, false, IN),

	IS_NULL(new ComparisonOperator("=isnull=", "=null=")),
	IS_NOT_NULL(new ComparisonOperator("=isnotnull=", "=notnull=")),

	LIKE(new ComparisonOperator("=like=")),
	NOT_LIKE(new ComparisonOperator("=notlike="), true, false, LIKE),
	CASE_INSENSITIVE_LIKE(new ComparisonOperator("=ilike="), false, true, LIKE),
	CASE_INSENSITIVE_NOT_LIKE(new ComparisonOperator("=inotlike="), true, true, LIKE),

	BETWEEN(new ComparisonOperator("=bt=", "=between=", true)),
	NOT_BETWEEN(new ComparisonOperator("=nbt=", "=notbetween=", true), true, false, BETWEEN);
	// CONTAIN(new ComparisonOperator("=c=", "=contain=")),
	// NOT_CONTAIN(new ComparisonOperator("=nc=", "=notcontain="));

	private final ComparisonOperator operator;
	private final RsqlSearchOperation reelOperator;
	private final boolean negative;
	private final boolean caseInsensitive;

	RsqlSearchOperation(final ComparisonOperator operator) {
		this(operator, false, false, null);
	}
	RsqlSearchOperation(final ComparisonOperator operator, boolean negative, boolean caseInsensitive, RsqlSearchOperation reelOperator) {
		this.operator = operator;
		this.negative = negative;
		this.caseInsensitive = caseInsensitive;
		this.reelOperator = reelOperator;
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
