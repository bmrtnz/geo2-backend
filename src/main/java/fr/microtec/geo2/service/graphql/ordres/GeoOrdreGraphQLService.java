package fr.microtec.geo2.service.graphql.ordres;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningTransporteur;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.service.OrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoOrdreGraphQLService extends GeoAbstractGraphQLService<GeoOrdre, String> {

	private final OrdreService ordreService;

	public GeoOrdreGraphQLService(GeoOrdreRepository repository, OrdreService ordreService) {
		super(repository, GeoOrdre.class);
		this.ordreService = ordreService;
	}

	@GraphQLQuery
	public RelayPage<GeoOrdre> allOrdre(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public RelayPage<GeoOrdre> allOrdreSuiviDeparts(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLArgument(name = "onlyColisDiff") Boolean onlyColisDiff
	) {
		return this.ordreService
		.fetchOrdreSuiviDeparts(search, pageable, onlyColisDiff);
	}

	@GraphQLQuery
	public List<GeoPlanningTransporteur> allPlanningTransporteurs(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLArgument(name = "dateMin") LocalDateTime dateMin,
			@GraphQLArgument(name = "dateMax") LocalDateTime dateMax,
			@GraphQLArgument(name = "societeCode") String societeCode,
			@GraphQLArgument(name = "transporteurCode") String transporteurCode
	) {
		return this.ordreService.allPlanningTransporteurs(
			search,
			pageable,
			dateMin,
			dateMax,
			societeCode,
			transporteurCode
		);
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
	public long nombreOrdreNonCloture(@GraphQLArgument(name = "search") String search)
	{
		return this.ordreService.fetchNombreOrdreNonCloture(search);
	}

	@GraphQLQuery
	public Optional<GeoOrdre> getOrdre(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
	}

	@GraphQLQuery
	public Optional<GeoOrdre> getOrdreByNumeroAndSociete(
			@GraphQLArgument(name = "numero") String numero,
			@GraphQLArgument(name = "societe") String societeID
	) {
		return this.ordreService.getByNumeroAndSociete(numero, societeID);
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

	@GraphQLQuery
	public long countOrdre(
		@GraphQLArgument(name = "search") String search
	) {
		return this.count(search);
	}
}
