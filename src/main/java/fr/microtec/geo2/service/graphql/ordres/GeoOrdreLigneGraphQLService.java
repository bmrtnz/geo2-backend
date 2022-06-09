package fr.microtec.geo2.service.graphql.ordres;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.service.security.SecurityService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.service.OrdreLigneService;
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
public class GeoOrdreLigneGraphQLService extends GeoAbstractGraphQLService<GeoOrdreLigne, String> {

    private final GeoFunctionOrdreRepository geoFunctionOrdreRepository;
	private final OrdreLigneService ordreLigneService;
    private final SecurityService securityService;

	public GeoOrdreLigneGraphQLService(
        GeoOrdreLigneRepository repository,
        GeoFunctionOrdreRepository geoFunctionOrdreRepository, OrdreLigneService ordreLigneService, SecurityService securityService) {
		super(repository, GeoOrdreLigne.class);
        this.geoFunctionOrdreRepository = geoFunctionOrdreRepository;
        this.ordreLigneService = ordreLigneService;
        this.securityService = securityService;
    }

	@GraphQLQuery
	public RelayPage<GeoOrdreLigne> allOrdreLigne(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public List<GeoOrdreLigne> allOrdreLigneList(
			@GraphQLArgument(name = "search") String search,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.getAll(search);
	}

	@GraphQLQuery
	public RelayPage<GeoOrdreLigne> allOrdreLigneMarge(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.ordreLigneService.fetchAllMarge(search, pageable, env);
	}

	@GraphQLQuery
	public RelayPage<GeoOrdreLigne> allOrdreLigneTotaux(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.ordreLigneService.fetchOrdreLignesTotauxDetail(search, pageable, env);
	}

	@GraphQLQuery
	public RelayPage<GeoOrdreLigne> allOrdreLigneTotauxDetail(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env) {
		return this.ordreLigneService.fetchOrdreLignesTotauxDetail(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoOrdreLigne> getOrdreLigne(
			@GraphQLArgument(name = "id") String id) {
		return super.getOne(id);
	}

	@GraphQLMutation
	public GeoOrdreLigne saveOrdreLigne(GeoOrdreLigne ordreLigne, @GraphQLEnvironment ResolutionEnvironment env) {
		return this.saveEntity(this.ordreLigneService.withDefaults(ordreLigne), env);
	}

	@GraphQLMutation
	public boolean deleteOrdreLigne(String id) {
		return this.delete(id);
	}


    @GraphQLMutation
    public FunctionResult updateField(final String fieldName, final String id, final Object value, final String socCode) {

        FunctionResult result = null;

        switch (fieldName) {
            case "nombrePalettesCommandees":
                result = this.geoFunctionOrdreRepository.onChangeCdeNbPal(id, socCode);
                break;

            case "nombreColisPalette":
                result = this.geoFunctionOrdreRepository.onChangePalNbCol(id, this.securityService.getUser().getUsername());
                break;

            case "nombreColisCommandes":
                result = this.geoFunctionOrdreRepository.onChangeCdeNbCol(id, this.securityService.getUser().getUsername());
                break;

            case "proprietaireMarchandise":
                // TODO
            break;

            case "fournisseur":
                // TODO
                break;

            case "ventePrixUnitaire":
                result = this.geoFunctionOrdreRepository.onChangeVtePu(id);
                break;

            case "achatDevisePrixUnitaire":
                result = this.geoFunctionOrdreRepository.onChangeAchDevPu(id, socCode);
                break;

            case "gratuit":
                result = this.geoFunctionOrdreRepository.onChangeIndGratuit(id);
                break;

            case "typePalette":
                result = this.geoFunctionOrdreRepository.onChangePalCode(id, this.securityService.getUser().getUsername(), socCode);
                break;

            case "paletteInter":
                result = this.geoFunctionOrdreRepository.onChangePalinterCode(id);
                break;

            case "nombrePalettesIntermediaires":
                result = this.geoFunctionOrdreRepository.onChangePalNbPalinter(id, this.securityService.getUser().getUsername());
                break;

        }


//        final Optional<GeoOrdreLigne> one = super.getOne(id);
//
//        if(one.isPresent()) {
//            one.get()
//        }
//
//        super.getOne(id)
//            .ifPresent(geoOrdreLigne -> {
//            result.setData(Map.of(geoOrdreLigne.getId(), geoOrdreLigne));
//        });
//        return super.getOne(id);

        return result;
    }
}
