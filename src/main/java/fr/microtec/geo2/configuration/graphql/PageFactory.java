package fr.microtec.geo2.configuration.graphql;

import java.util.List;

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
	public static <T> RelayPage<T> fromPage(Page<T> page) {
		CursorProvider<T> cursorProvider = offsetBasedCursorProvider(page.getPageable().getOffset());
		List<Edge<T>> edges = createEdges(page.getContent(), cursorProvider);

		return new RelayPageImpl<>(
			edges,
			createPageInfo(edges, page.hasNext(), page.hasPrevious()),
			page.getTotalElements(),
			page.getTotalPages()
		);
	}

	/**
	 * Convert relay page to summarised relay page.
	 *
	 * @param page Spring page.
	 * @param <T> Type of page content.
	 * @return Summarised relay page.
	 */
	public static <T> SummarisedRelayPage<T> fromRelayPage(RelayPage<T> page,List<Double> summary) {
		return new SummarisedRelayPageImpl<>(
			page.getEdges(),
			page.getPageInfo(),
			page.getTotalCount(),
			page.getTotalPage(),
			summary
		);
	}
}
