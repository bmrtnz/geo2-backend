package fr.microtec.geo2.service.graphql.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoLieuPassageAQuai;
import fr.microtec.geo2.persistance.repository.tiers.GeoLieuPassageAQuaiRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoLieuPassageAQuaiGraphQLService extends GeoAbstractGraphQLService<GeoLieuPassageAQuai, String> {

	public GeoLieuPassageAQuaiGraphQLService(GeoLieuPassageAQuaiRepository repository) {
		super(repository);
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
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getOne(id, env);
	}

	@GraphQLMutation
	public GeoLieuPassageAQuai saveLieuPassageAQuai(GeoLieuPassageAQuai lieuPassageAQuai) {
		return this.save(lieuPassageAQuai);
	}

}
