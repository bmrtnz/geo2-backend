package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypeFournisseur;
import fr.microtec.geo2.persistance.repository.tiers.GeoTypeFournisseurRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class GeoTypeFournisseurGraphQLService extends GeoAbstractGraphQLService<GeoTypeFournisseur, String> {

	public GeoTypeFournisseurGraphQLService(GeoTypeFournisseurRepository repository) {
		super(repository);
	}

	@GraphQLQuery
	public RelayPage<GeoTypeFournisseur> allTypeFournisseur(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	protected Optional<GeoTypeFournisseur> getTypeFournisseur(
			@GraphQLArgument(name = "search") String search,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return super.getOne(search, env);
	}

}
