package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoRaisonAnnuleRemplace;
import fr.microtec.geo2.persistance.repository.ordres.GeoRaisonAnnuleRemplaceRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoRaisonAnnuleRemplaceGraphQLService extends GeoAbstractGraphQLService<GeoRaisonAnnuleRemplace, Integer> {

	public GeoRaisonAnnuleRemplaceGraphQLService(GeoRaisonAnnuleRemplaceRepository repository) {
		super(repository, GeoRaisonAnnuleRemplace.class);
	}

	@GraphQLQuery
	public RelayPage<GeoRaisonAnnuleRemplace> allRaisonAnnuleRemplace(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoRaisonAnnuleRemplace> getRaisonAnnuleRemplace(
			@GraphQLArgument(name = "id") Integer id) {
		return super.getOne(id);
	}

}
