package fr.microtec.geo2.configuration.graphql;

import java.util.List;
import java.util.function.Function;

import graphql.GraphQLException;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.execution.relay.generic.GenericPage;

public class RelayPageImpl<E> extends GenericPage<E> implements RelayPage<E> {

	private long totalCount;
	private long totalPage;
	private Function<List<Summary>, ?> summaryResolver;

	public RelayPageImpl(
		List<Edge<E>> edges,
		PageInfo pageInfo,
		long count,
		long pageCount
	) {
		super(edges, pageInfo);
		this.totalCount = count;
		this.totalPage = pageCount;
	}

	public RelayPageImpl(
		List<Edge<E>> edges,
		PageInfo pageInfo,
		long count,
		long pageCount,
		Function<List<Summary>, ?> summaryResolver
	) {
		super(edges, pageInfo);
		this.totalCount = count;
		this.totalPage = pageCount;
		this.summaryResolver = summaryResolver;
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

	@Override
	@GraphQLQuery
	public Object getSummary(
		@GraphQLNonNull List<Summary> summaries,
		@GraphQLNonNull String of,
		@GraphQLEnvironment final ResolutionEnvironment env
	) {

		if (summaries.isEmpty())
			throw new GraphQLException("Error fetching summary, no summary specified :" + of);

		if(this.summaryResolver == null)
			throw new GraphQLException("Error fetching summary, summary resolver is null :" + of);

		return this.summaryResolver.apply(summaries);
	}
	
}
