package fr.microtec.geo2.configuration.graphql;

import java.util.List;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.execution.relay.Page;

public interface RelayPage<E> extends Page<E> {

	@GraphQLQuery
	long getTotalCount();

	@GraphQLQuery
	long getTotalPage();

	@GraphQLQuery
	Object getSummary(
		@GraphQLNonNull List<Summary> summaries,
		@GraphQLNonNull String of,
		@GraphQLEnvironment final ResolutionEnvironment env
	);

}
