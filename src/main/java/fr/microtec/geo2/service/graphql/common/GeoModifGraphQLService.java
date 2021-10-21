package fr.microtec.geo2.service.graphql.common;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoModification;
import fr.microtec.geo2.persistance.repository.common.GeoModifRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoModifGraphQLService extends GeoAbstractGraphQLService<GeoModification, Integer> {

	public GeoModifGraphQLService(GeoModifRepository repository) {
		super(repository, GeoModification.class);
	}

	@GraphQLQuery
	public RelayPage<GeoModification> allModification(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoModification> getModification(
			@GraphQLArgument(name = "id") Integer id
	) {
		return super.getOne(id);
  }

  @GraphQLMutation
	public GeoModification saveModification(GeoModification modification) {
		return this.save(modification);
	}

}
