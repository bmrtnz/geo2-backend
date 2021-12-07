package fr.microtec.geo2.configuration.graphql;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import graphql.relay.Edge;
import io.leangen.graphql.execution.relay.CursorProvider;

public class PageFactory extends io.leangen.graphql.execution.relay.generic.PageFactory {

	/**
	 * Convert Spring page to relay page.
	 *
	 * @param page Spring page.
	 * @param <T> Type of page content.
	 * @return Relay page.
	 */
	public static <T> RelayPage<T> asRelayPage(Page<T> page) {
		CursorProvider<T> cursorProvider = offsetBasedCursorProvider(page.getPageable().getOffset());
		List<Edge<T>> edges = createEdges(page.getContent(), cursorProvider);

		return new RelayPageImpl<T>(
			edges,
			createPageInfo(edges, page.hasNext(), page.hasPrevious()),
			page.getTotalElements(),
			page.getTotalPages()
		);
	}

	/**
	 * Convert Spring page to relay page.
	 *
	 * @param page Spring page.
	 * @param repository <T> repository.
	 * @param <T> Type of page content.
	 * @return Relay page.
	 */
	public static <T,R> RelayPage<T> asRelayPage(Page<T> page, Function<List<Summary>, List<Double>> summaryResolver) {
		CursorProvider<T> cursorProvider = offsetBasedCursorProvider(page.getPageable().getOffset());
		List<Edge<T>> edges = createEdges(page.getContent(), cursorProvider);

		return new RelayPageImpl<T>(
			edges,
			createPageInfo(edges, page.hasNext(), page.hasPrevious()),
			page.getTotalElements(),
			page.getTotalPages(),
			summaryResolver
		);
	}
}
