package fr.microtec.geo2.service.graphql;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.FunctionsRepository;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoFunctionsGraphQLService {
  
	private final FunctionsRepository repository;

	public GeoFunctionsGraphQLService(
		FunctionsRepository repository
	) {
		this.repository = repository;
	}

  @GraphQLQuery
	public FunctionResult ofValideEntrepotForOrdre(
			@GraphQLArgument(name = "entrepotID") String entrepotID
	) {
		return this.repository.ofValideEntrepotForOrdre(entrepotID);
	}

}
