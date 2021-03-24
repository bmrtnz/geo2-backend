package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoLitigeLigneRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoLitigeLigneGraphQLService extends GeoAbstractGraphQLService<GeoLitigeLigne, String> {

	public GeoLitigeLigneGraphQLService(GeoLitigeLigneRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoLitigeLigne> allLitigeLigne(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoLitigeLigne> getLitigeLigne(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
  }

}