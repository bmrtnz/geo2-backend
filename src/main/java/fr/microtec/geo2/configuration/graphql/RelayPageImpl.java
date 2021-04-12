package fr.microtec.geo2.configuration.graphql;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import graphql.relay.Edge;
import graphql.relay.PageInfo;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.relay.CursorProvider;
import io.leangen.graphql.execution.relay.generic.GenericPage;

public class RelayPageImpl<T> extends GenericPage<T> implements RelayPage<T> {

	private long totalCount;
	private long totalPage;

	public RelayPageImpl(List<Edge<T>> edges, PageInfo pageInfo, long count, long pageCount) {
		super(edges, pageInfo);
		this.totalCount = count;
		this.totalPage = pageCount;
	}

	@Override
	@GraphQLQuery
	public long getTotalCount() {
		return this.totalCount;
	}

	@Override
	@GraphQLQuery
	public long getTotalPage() {
		return this.totalPage;
	}

	public RelayPage<T> mapNodes(Function<T, T> mapper, Pageable pageable) {
		CursorProvider<T> cursorProvider = PageFactory.offsetBasedCursorProvider(pageable.getOffset());
		List<T> nodes = this.getEdges().stream().map(edge -> edge.getNode()).map(mapper).collect(Collectors.toList());
		List<Edge<T>> edges = PageFactory.createEdges(nodes, cursorProvider);
		return new RelayPageImpl<>(edges, this.getPageInfo(), this.totalCount, this.totalPage);
	}
}
