package fr.microtec.geo2.service.graphql.tiers;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoLieuPassageAQuai;
import fr.microtec.geo2.persistance.repository.tiers.GeoLieuPassageAQuaiRepository;
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
public class GeoLieuPassageAQuaiGraphQLService extends GeoAbstractGraphQLService<GeoLieuPassageAQuai, String> {

	public GeoLieuPassageAQuaiGraphQLService(GeoLieuPassageAQuaiRepository repository) {
		super(repository, GeoLieuPassageAQuai.class);
	}

	@GraphQLQuery
	public RelayPage<GeoLieuPassageAQuai> allLieuPassageAQuai(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoLieuPassageAQuai> getLieuPassageAQuai(
			@GraphQLArgument(name = "id") String id
	) {
		return this.getOne(id);
	}

	@GraphQLMutation
	public GeoLieuPassageAQuai saveLieuPassageAQuai(GeoLieuPassageAQuai lieuPassageAQuai, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(lieuPassageAQuai, env);
	}

	@GraphQLQuery
	public long countLieuPassageAQuai(
		@GraphQLArgument(name = "search") String search
	) {
		return this.count(search);
	}

}
