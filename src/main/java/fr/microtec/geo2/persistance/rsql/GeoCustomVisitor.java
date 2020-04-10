package fr.microtec.geo2.persistance.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.jpa.domain.Specification;

public class GeoCustomVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

	private final GenericRsqlSpecificationBuilder<T> builder;

	public GeoCustomVisitor() {
		this.builder = new GenericRsqlSpecificationBuilder<>();
	}

	@Override
	public Specification<T> visit(AndNode andNode, Void aVoid) {
		return this.builder.createSpecification(andNode);
	}

	@Override
	public Specification<T> visit(OrNode orNode, Void aVoid) {
		return this.builder.createSpecification(orNode);
	}

	@Override
	public Specification<T> visit(ComparisonNode comparisonNode, Void aVoid) {
		return this.builder.createSpecification(comparisonNode);
	}
}
