package fr.microtec.geo2.configuration.graphql;

import java.util.List;

import graphql.relay.Edge;
import graphql.relay.PageInfo;
import io.leangen.graphql.annotations.GraphQLQuery;

public class SummarisedRelayPageImpl<T> extends RelayPageImpl<T> implements SummarisedRelayPage<T> {

  private List<Double> summary;

	public SummarisedRelayPageImpl(List<Edge<T>> edges, PageInfo pageInfo, long count, long pageCount, List<Double> summary) {
		super(edges, pageInfo, count, pageCount);
    this.summary = summary;
	}

	@GraphQLQuery
	public List<Double> getSummary() {
		return this.summary;
	}
	
}
