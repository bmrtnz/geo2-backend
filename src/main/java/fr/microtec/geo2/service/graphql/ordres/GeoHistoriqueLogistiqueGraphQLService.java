package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoHistoriqueLogistique;
import fr.microtec.geo2.persistance.repository.ordres.GeoHistoriqueLogistiqueRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoHistoriqueLogistiqueGraphQLService extends GeoAbstractGraphQLService<GeoHistoriqueLogistique, String> {

	public GeoHistoriqueLogistiqueGraphQLService(GeoHistoriqueLogistiqueRepository repository) {
		super(repository, GeoHistoriqueLogistique.class);
	}

	@GraphQLQuery
	public RelayPage<GeoHistoriqueLogistique> allHistoriqueLogistique(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoHistoriqueLogistique> getHistoriqueLogistique(
			@GraphQLArgument(name = "id") String id) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoHistoriqueLogistique saveHistoriqueLogistique(GeoHistoriqueLogistique HistoriqueLogistique,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(HistoriqueLogistique, env);
	}

	@GraphQLMutation
	public void deleteHistoriqueLogistique(String id) {
		this.delete(id);
	}

}
