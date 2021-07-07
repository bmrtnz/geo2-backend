package fr.microtec.geo2.configuration.graphql;

import java.util.List;

import io.leangen.graphql.annotations.GraphQLQuery;

public interface SummarisedRelayPage<T> extends RelayPage<T> {

	@GraphQLQuery
	List<Double> getSummary();

}
