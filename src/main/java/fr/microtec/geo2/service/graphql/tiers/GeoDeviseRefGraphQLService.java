package fr.microtec.geo2.service.graphql.tiers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoDeviseRef;
import fr.microtec.geo2.persistance.entity.tiers.GeoDeviseRefKey;
import fr.microtec.geo2.persistance.repository.tiers.GeoDeviseRefRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoDeviseRefGraphQLService extends GeoAbstractGraphQLService<GeoDeviseRef, GeoDeviseRefKey> {

	public GeoDeviseRefGraphQLService(GeoDeviseRefRepository repository) {
		super(repository, GeoDeviseRef.class);
	}

	@GraphQLQuery
	public RelayPage<GeoDeviseRef> allDeviseRef(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public List<GeoDeviseRef> allDeviseRefList(
			@GraphQLArgument(name = "search") String search,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getAll(search);
	}

	@GraphQLQuery
	public Optional<GeoDeviseRef> getDeviseRef(
			@GraphQLArgument(name = "id") GeoDeviseRefKey id
	) {
		return super.getOne(id);
	}

}
