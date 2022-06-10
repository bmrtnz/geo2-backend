package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.service.OrdreLigneService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.security.SecurityService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@GraphQLApi
@Secured("ROLE_USER")
@Slf4j
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

        AtomicReference<FunctionResult> result = new AtomicReference<>(new FunctionResult());

        this.getOne(id)
            .ifPresent(geoOrdreLigne -> {
                try {
                    final Field field = GeoOrdreLigne.class.getField(fieldName);
                    field.set(geoOrdreLigne, value);
                    this.repository.save(geoOrdreLigne);

                    switch (fieldName) {
                        case "nombrePalettesCommandees":
                            result.set(this.geoFunctionOrdreRepository.onChangeCdeNbPal(id, socCode));
                            break;

                        case "nombreColisPalette":
                            result.set(this.geoFunctionOrdreRepository.onChangePalNbCol(id, this.securityService.getUser().getUsername()));
                            break;

                        case "nombreColisCommandes":
                            result.set(this.geoFunctionOrdreRepository.onChangeCdeNbCol(id, this.securityService.getUser().getUsername()));
                            break;

                        case "proprietaireMarchandise":
                            result.set(this.geoFunctionOrdreRepository.onChangeProprCode(id, this.securityService.getUser().getUsername(), socCode));
                            break;

                        case "fournisseur":
                            result.set(this.geoFunctionOrdreRepository.onChangeFouCode(id, this.securityService.getUser().getUsername(), socCode));
                            break;

                        case "ventePrixUnitaire":
                            result.set(this.geoFunctionOrdreRepository.onChangeVtePu(id));
                            break;

                        case "gratuit":
                            result.set(this.geoFunctionOrdreRepository.onChangeIndGratuit(id));
                            break;

                        case "achatDevisePrixUnitaire":
                            result.set(this.geoFunctionOrdreRepository.onChangeAchDevPu(id, socCode));
                            break;

                        case "typePalette":
                            result.set(this.geoFunctionOrdreRepository.onChangePalCode(id, this.securityService.getUser().getUsername(), socCode));
                            break;

                        case "paletteInter":
                            result.set(this.geoFunctionOrdreRepository.onChangePalinterCode(id));
                            break;

                        case "nombrePalettesIntermediaires":
                            result.set(this.geoFunctionOrdreRepository.onChangePalNbPalinter(id, this.securityService.getUser().getUsername()));
                            break;
                    }

                    final GeoOrdreLigne ordreLigne = this.repository.getOne(id);

                    result.get().setData(Map.of(ordreLigne.getId(), ordreLigne));
                } catch (NoSuchFieldException e) {
                    log.error("Impossible de récupérer le champ \"{}\" dans l'objet GeoOrdreLigne avec l'id \"{}\".", fieldName, id);
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    log.error("Impossible d'accédez au setter du champ \"{}\" de l'objet GeoOrdreLigne", fieldName);
                    throw new RuntimeException(e);
                }
            });

        return result.get();
    }
}
