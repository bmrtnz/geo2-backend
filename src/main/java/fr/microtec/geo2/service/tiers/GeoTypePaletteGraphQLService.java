package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import fr.microtec.geo2.persistance.repository.tiers.GeoTypePaletteRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoTypePaletteGraphQLService extends GeoAbstractGraphQLService<GeoTypePalette, String> {

	public GeoTypePaletteGraphQLService(GeoTypePaletteRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoTypePalette> allTypePalette(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	protected Optional<GeoTypePalette> getTypePalette(
			@GraphQLArgument(name = "search") String search,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(search, env);
	}

}
