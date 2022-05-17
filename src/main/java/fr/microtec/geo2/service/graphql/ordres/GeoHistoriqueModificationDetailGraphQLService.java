package fr.microtec.geo2.service.graphql.ordres;

import java.util.List;
import java.util.Optional;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoHistoriqueModificationDetail;
import fr.microtec.geo2.persistance.repository.ordres.GeoHistoriqueModificationDetailRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoHistoriqueModificationDetailGraphQLService
		extends GeoAbstractGraphQLService<GeoHistoriqueModificationDetail, String> {

	public GeoHistoriqueModificationDetailGraphQLService(GeoHistoriqueModificationDetailRepository repository) {
		super(repository, GeoHistoriqueModificationDetail.class);
	}

	@GraphQLQuery
	public RelayPage<GeoHistoriqueModificationDetail> allHistoriqueModificationDetail(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoHistoriqueModificationDetail> getHistoriqueModificationDetail(
			@GraphQLArgument(name = "id") String id) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoHistoriqueModificationDetail saveHistoriqueModificationDetail(
			GeoHistoriqueModificationDetail HistoriqueModificationDetail,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(HistoriqueModificationDetail, env);
	}

	@GraphQLMutation
	public List<GeoHistoriqueModificationDetail> saveAllHistoriqueModificationDetail(
			List<GeoHistoriqueModificationDetail> allHistoriqueModificationDetail) {
		return this.saveAll(allHistoriqueModificationDetail, null);
	}

}
