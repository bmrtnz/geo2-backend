package fr.microtec.geo2.service.graphql.ordres;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.service.OrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoOrdreGraphQLService extends GeoAbstractGraphQLService<GeoOrdre, String> {

	private final OrdreService ordreService;

	public GeoOrdreGraphQLService(GeoOrdreRepository repository, OrdreService ordreService) {
		super(repository);
		this.ordreService = ordreService;
	}

	@GraphQLQuery
	public RelayPage<GeoOrdre> allOrdre(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		return this.getPage(search, pageable);
	}

	@GraphQLQuery
	public Float sommeColisCommandes(@GraphQLContext GeoOrdre ordre) {
		return this.ordreService.fetchSommeColisCommandes(ordre);
	}

	@GraphQLQuery
	public Float sommeColisExpedies(@GraphQLContext GeoOrdre ordre) {
		return this.ordreService.fetchSommeColisExpedies(ordre);
	}

	@GraphQLQuery
	public RelayPage<GeoOrdre> allOrdreBAF(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		List<GeoOrdre> list;
	
		if (search != null && !search.isBlank()) {
			list = this.repository.findAll(this.parseSearch(search));
		} else {
			list = this.repository.findAll();
		}

		// REVOIR POSTLOAD HOOK
		list = list.stream()
		.filter(node -> this.ordreService.filterCalculMarge(node))
		// .filter(node -> this.ordreService.filterVerifOrdreWarning(node))
		.collect(Collectors.toList());

		int total = list.size();
		int start = (int)pageable.getOffset();
		int end = (int)Math.min(start + pageable.getPageSize(), total);
		if (start <= end) list = list.subList(start, end);

		Page<GeoOrdre> page = new PageImpl<>(list, pageable, total);
		return PageFactory.fromPage(page);
	}

	@GraphQLQuery
	public Optional<GeoOrdre> getOrdre(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoOrdre saveOrdre(GeoOrdre ordre) {
		return this.ordreService.save(ordre);
	}

	@GraphQLMutation
	public GeoOrdre cloneOrdre(GeoOrdre ordre) {
		return this.ordreService.clone(ordre);
	}

	@GraphQLMutation
	public void deleteOrdre(GeoOrdre ordre) {
		this.delete(ordre);
	}

	@GraphQLMutation
	public List<GeoOrdre> saveAllOrdre(List<GeoOrdre> allOrdre) {
		return this.ordreService.save(allOrdre);
	}
}
