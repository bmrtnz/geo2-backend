package fr.microtec.geo2.configuration.graphql;

import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.data.domain.Pageable;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.relay.Page;

public interface RelayPage<T> extends Page<T> {

	@GraphQLQuery
	long getTotalCount();

	@GraphQLQuery
	long getTotalPage();

	RelayPage<T> mapNodes(Function<T, T> mapper, Pageable pageable);

	RelayPage<T> filterNodes(Predicate<? super T> predicate, Pageable pageable);

}
