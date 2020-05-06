package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoEntrepotGraphQLService extends GeoAbstractGraphQLService<GeoEntrepot, String> {

	public GeoEntrepotGraphQLService(GeoEntrepotRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoEntrepot> allEntrepot(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoEntrepot> getEntrepot(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getOne(id, env);
	}

	@GraphQLMutation
	public GeoEntrepot saveEntrepot(@Validated GeoEntrepot entrepot) {
		return this.save(entrepot);
	}

	@GraphQLMutation
	public void deleteEntrepot(String id) {
		this.delete(id);
	}

}
