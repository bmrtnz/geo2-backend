package fr.microtec.geo2.service.graphql.tiers;

import java.util.List;
import java.util.Optional;

import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import fr.microtec.geo2.persistance.repository.tiers.GeoPaysRepository;
import fr.microtec.geo2.service.PaysService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoPaysGraphQLService extends GeoAbstractGraphQLService<GeoPays, String> {

	private final PaysService paysService;

	public GeoPaysGraphQLService(
			GeoPaysRepository repository,
			PaysService paysService) {
		super(repository, GeoPays.class);
		this.paysService = paysService;
	}

	@GraphQLQuery
	public RelayPage<GeoPays> allPays(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public RelayPage<GeoPays> allDistinctPays(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable) {
		return this.paysService.fetchDistinctPays(search, pageable);
	}

	@GraphQLQuery
	public List<GeoPays> allPaysList(
			@GraphQLArgument(name = "search") String search,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getAll(search);
	}

	@GraphQLQuery
	public Optional<GeoPays> getPays(
			@GraphQLArgument(name = "id") String id) {
		return super.getOne(id);
	}

	@GraphQLQuery
	public long countPays(
			@GraphQLArgument(name = "search") String search) {
		return this.count(search);
	}

	@GraphQLQuery
	public Float clientsSommeAgrement(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.agrement").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeEnCoursTemporaire(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.enCoursTemporaire").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeEnCoursBlueWhale(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.enCoursBlueWhale").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeAutorise(@GraphQLContext GeoPays pays) {
		Float agrement = this.paysService
				.fetchSum(pays, "clients.agrement").floatValue();
		Float enCoursTemp = this.paysService
				.fetchSum(pays, "clients.enCoursTemporaire").floatValue();
		Float enCoursBW = this.paysService
				.fetchSum(pays, "clients.enCoursBlueWhale").floatValue();
		return agrement + enCoursTemp + enCoursBW;
	}

	@GraphQLQuery
	public Float clientsSommeDepassement(@GraphQLContext GeoPays pays) {
		Float agrement = this.paysService
				.fetchSum(pays, "clients.agrement").floatValue();
		Float enCoursTemp = this.paysService
				.fetchSum(pays, "clients.enCoursTemporaire").floatValue();
		Float enCoursBW = this.paysService
				.fetchSum(pays, "clients.enCoursBlueWhale").floatValue();
		Float enCoursActuel = this.paysService
				.fetchSum(pays, "clients.enCoursActuel").floatValue();
		Float enCoursTotal = agrement + enCoursTemp + enCoursBW;
		Float depassement = enCoursActuel - enCoursTotal;
		return depassement > 0 ? depassement : 0f;
	}

	@GraphQLQuery
	public Float clientsSommeEnCoursActuel(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.enCoursActuel").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeEnCoursNonEchu(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.enCoursNonEchu").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeEnCours1a30(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.enCours1a30").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeEnCours31a60(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.enCours31a60").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeEnCours61a90(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.enCours61a90").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeEnCours90Plus(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.enCours90Plus").floatValue();
	}

	@GraphQLQuery
	public Float clientsSommeAlerteCoface(@GraphQLContext GeoPays pays) {
		return this.paysService
				.fetchSum(pays, "clients.alerteCoface").floatValue();
	}

}
