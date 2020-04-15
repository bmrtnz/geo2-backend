package fr.microtec.geo2.configuration.graphql;

import graphql.relay.Edge;
import graphql.relay.PageInfo;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.relay.generic.GenericPage;

import java.util.List;

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
	public long getTotalPage() { return this.totalPage; }
}
