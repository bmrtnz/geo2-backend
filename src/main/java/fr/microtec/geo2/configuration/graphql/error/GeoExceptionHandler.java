package fr.microtec.geo2.configuration.graphql.error;

import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import io.leangen.graphql.spqr.spring.autoconfigure.DefaultGlobalContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.ServletWebRequest;

public class GeoExceptionHandler extends SimpleDataFetcherExceptionHandler {

	@Override
	public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
		Throwable exception = handlerParameters.getException();

		// Handle 403
		if (exception instanceof AccessDeniedException) {
			DefaultGlobalContext<ServletWebRequest> ctx = handlerParameters.getDataFetchingEnvironment().getContext();
			ctx.getNativeRequest().getResponse().setStatus(403);
		}

		return super.onException(handlerParameters);
	}

}
