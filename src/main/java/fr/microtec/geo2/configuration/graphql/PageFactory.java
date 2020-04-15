package fr.microtec.geo2.configuration.graphql;

import graphql.relay.Edge;
import io.leangen.graphql.execution.relay.CursorProvider;
import org.springframework.data.domain.Page;

import java.util.List;

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
}
