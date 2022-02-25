package fr.microtec.geo2.service.graphql.common;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.common.GeoModification;
import fr.microtec.geo2.persistance.entity.common.GeoModificationCorps;
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
public class GeoModifGraphQLService extends GeoAbstractGraphQLService<GeoModification, BigDecimal> {

	public GeoModifGraphQLService(
		GeoModifRepository repository
	) {
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
	public List<GeoModification> listModification(
			@GraphQLArgument(name = "search") String search,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getAll(search);
	}

	@GraphQLQuery
	public Optional<GeoModification> getModification(
			@GraphQLArgument(name = "id") BigDecimal id
	) {
		return super.getOne(id);
  }

  @GraphQLMutation
	public GeoModification saveModification(GeoModification modification, @GraphQLEnvironment ResolutionEnvironment env) {
		List<GeoModificationCorps> mappedCorps = modification.getCorps()
		.stream()
		.map(corps -> {
			corps.setModification(modification);
			return corps;
		})
		.collect(Collectors.toList());
		modification.setCorps(mappedCorps);
		return this.saveEntity(modification, env);
	}

}
