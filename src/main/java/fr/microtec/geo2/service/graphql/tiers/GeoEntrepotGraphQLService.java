package fr.microtec.geo2.service.graphql.tiers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoMouvementEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoMouvementFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoRecapitulatifEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoRecapitulatifFournisseur;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;
import fr.microtec.geo2.service.EntrepotService;
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
public class GeoEntrepotGraphQLService extends GeoAbstractGraphQLService<GeoEntrepot, String> {

	private final EntrepotService entrepotService;

	public GeoEntrepotGraphQLService(
		GeoEntrepotRepository repository,
		EntrepotService entrepotService
	) {
		super(repository, GeoEntrepot.class);
		this.entrepotService = entrepotService;
	}

	@GraphQLQuery
	public RelayPage<GeoEntrepot> allEntrepot(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoEntrepot> getEntrepot(
			@GraphQLArgument(name = "id") String id
	) {
		return this.getOne(id);
	}

	@GraphQLMutation
	public GeoEntrepot saveEntrepot(GeoEntrepot entrepot) {
		return this.save(entrepot);
	}

	@GraphQLMutation
	public void deleteEntrepot(String id) {
		this.delete(id);
	}

	@GraphQLQuery
	public long countEntrepot(
		@GraphQLArgument(name = "search") String search
	) {
		return this.count(search);
	}

	@GraphQLQuery
	public List<GeoMouvementFournisseur> allMouvementFournisseur(
    @GraphQLArgument(name = "arg_dat_max") LocalDateTime dateMin,
    @GraphQLArgument(name = "arg_soc_code") String codeSociete,
    @GraphQLArgument(name = "arg_cen_ref") String codeEntrepot,
    @GraphQLArgument(name = "arg_per_code_com") String codeCommercial
	) {
		return this.entrepotService.allMouvementFournisseur(
			dateMin,
			codeSociete,
			codeEntrepot,
			codeCommercial
		);
	}

	@GraphQLQuery
	public List<GeoMouvementEntrepot> allMouvementEntrepot(
    @GraphQLArgument(name = "arg_dat_max") LocalDateTime dateMin,
    @GraphQLArgument(name = "arg_soc_code") String codeSociete,
    @GraphQLArgument(name = "arg_cen_ref") String codeEntrepot,
    @GraphQLArgument(name = "arg_per_code_com") String codeCommercial
	) {
		return this.entrepotService.allMouvementEntrepot(
			dateMin,
			codeSociete,
			codeEntrepot,
			codeCommercial
		);
	}

	@GraphQLQuery
	public List<GeoRecapitulatifFournisseur> allRecapitulatifFournisseur(
    @GraphQLArgument(name = "arg_dat_max") LocalDateTime dateMin,
    @GraphQLArgument(name = "arg_soc_code") String codeSociete,
    @GraphQLArgument(name = "arg_cen_ref") String codeEntrepot,
    @GraphQLArgument(name = "arg_per_code_com") String codeCommercial
	) {
		return this.entrepotService.allRecapitulatifFournisseur(
			dateMin,
			codeSociete,
			codeEntrepot,
			codeCommercial
		);
	}

	@GraphQLQuery
	public List<GeoRecapitulatifEntrepot> allRecapitulatifEntrepot(
    @GraphQLArgument(name = "arg_dat_max") LocalDateTime dateMin,
    @GraphQLArgument(name = "arg_soc_code") String codeSociete,
    @GraphQLArgument(name = "arg_cen_ref") String codeEntrepot,
    @GraphQLArgument(name = "arg_per_code_com") String codeCommercial
	) {
		return this.entrepotService.allRecapitulatifEntrepot(
			dateMin,
			codeSociete,
			codeEntrepot,
			codeCommercial
		);
	}

}
