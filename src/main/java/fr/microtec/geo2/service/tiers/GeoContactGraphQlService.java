package fr.microtec.geo2.service.tiers;

import fr.microtec.geo2.persistance.entity.tiers.GeoContact;
import fr.microtec.geo2.persistance.repository.tiers.GeoContactRepository;
import fr.microtec.geo2.service.GeoAbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@GraphQLApi
public class GeoContactGraphQlService extends GeoAbstractGraphQlService<GeoContact, String> {

	public GeoContactGraphQlService(GeoContactRepository contactRepository) {
		super(contactRepository);
	}

	@GraphQLQuery
	public Page<GeoContact> getContacts(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pagination") Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoContact> getContact(String id, @GraphQLEnvironment ResolutionEnvironment env) {
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
