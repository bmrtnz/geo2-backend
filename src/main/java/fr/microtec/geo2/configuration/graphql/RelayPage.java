package fr.microtec.geo2.configuration.graphql;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.relay.Page;

public interface RelayPage<T> extends Page<T> {

	@GraphQLQuery
	long getTotalCount();

	@GraphQLQuery
	long getTotalPage();

}
