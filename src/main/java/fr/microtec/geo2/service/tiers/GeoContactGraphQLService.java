package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoContact;
import fr.microtec.geo2.persistance.repository.tiers.GeoContactRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@GraphQLApi
public class GeoContactGraphQLService extends GeoAbstractGraphQLService<GeoContact, String> {

	public GeoContactGraphQLService(GeoContactRepository contactRepository) {
		super(contactRepository);
	}

	@GraphQLQuery
	public RelayPage<GeoContact> allContact(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") int page,
			@GraphQLArgument(name = "offset") int offset,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, page, offset, env);
	}

	@GraphQLQuery
	public Optional<GeoContact> getContact(
			@GraphQLArgument(name = "id") String id,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getOne(id, env);
	}

	@GraphQLMutation
	public GeoContact saveContact(@Validated GeoContact contact) {
		return this.save(contact);
	}

	@GraphQLMutation
	public boolean deleteContact(String id) {
		return this.delete(id);
	}

}
