package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUEntrepot;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUEntrepotKey;
import fr.microtec.geo2.persistance.repository.ordres.GeoMRUEntrepotRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoMRUEntrepotGraphQLService extends GeoAbstractGraphQLService<GeoMRUEntrepot, GeoMRUEntrepotKey> {

  public GeoMRUEntrepotGraphQLService(
		GeoMRUEntrepotRepository repository
	) {
		super(repository, GeoMRUEntrepot.class);
	}

	@GraphQLQuery
	public RelayPage<GeoMRUEntrepot> allMRUEntrepot(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoMRUEntrepot> getMRUEntrepot(
			@GraphQLArgument(name = "id") GeoMRUEntrepotKey id
	) {
		return super.getOne(id);
    }

    @GraphQLMutation
    public GeoMRUEntrepot saveMRUEntrepot(GeoMRUEntrepot mruEntrepot, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(mruEntrepot, env);
    }

}
