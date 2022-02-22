package fr.microtec.geo2.service.graphql.tiers;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoContact;
import fr.microtec.geo2.persistance.repository.tiers.GeoContactRepository;
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
public class GeoContactGraphQLService extends GeoAbstractGraphQLService<GeoContact, String> {

	public GeoContactGraphQLService(GeoContactRepository contactRepository) {
		super(contactRepository, GeoContact.class);
	}

	@GraphQLQuery
	public RelayPage<GeoContact> allContact(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoContact> getContact(
			@GraphQLArgument(name = "id") String id
	) {
		return this.getOne(id);
	}

	@GraphQLMutation
	public GeoContact saveContact(GeoContact contact, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(contact, env);
	}

	@GraphQLMutation
	public boolean deleteContact(String id) {
		return this.delete(id);
	}

}
