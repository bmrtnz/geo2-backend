package fr.microtec.geo2.service.graphql.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoContact;
import fr.microtec.geo2.persistance.repository.tiers.GeoContactRepository;
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
public class GeoContactGraphQLService extends GeoAbstractGraphQLService<GeoContact, String> {

	public GeoContactGraphQLService(GeoContactRepository contactRepository) {
		super(contactRepository);
	}

	@GraphQLQuery
	public RelayPage<GeoContact> allContact(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Optional<GeoContact> getContact(
			@GraphQLArgument(name = "id") String id
	) {
		return this.getOne(id);
	}

	@GraphQLMutation
	public GeoContact saveContact(GeoContact contact) {
		return this.save(contact);
	}

	@GraphQLMutation
	public boolean deleteContact(String id) {
		return this.delete(id);
	}

}
