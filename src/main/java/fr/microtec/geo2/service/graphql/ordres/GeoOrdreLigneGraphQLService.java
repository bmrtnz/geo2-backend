package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.service.OrdreLigneService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.security.SecurityService;
import graphql.GraphQLException;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static fr.microtec.geo2.persistance.entity.FunctionResult.RESULT_UNKNOWN;

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
    public GeoOrdreLigne updateField(final String fieldName, final String id, final Object value, final String socCode) {

        final AtomicReference<GeoOrdreLigne> result = new AtomicReference<>();
        final AtomicReference<Object> newValue = new AtomicReference<>(value);

        final Field field = ReflectionUtils.findField(GeoOrdreLigne.class, fieldName);
        Assert.notNull(field, String.format("Le champ \"%s\" n'a pas été trouvé dans l'objet \"GeoOrdreLigne\".", fieldName));
        field.setAccessible(true);

        if(value instanceof Number) {
            final Class<?> type = field.getType();
            if (type.equals(Double.class)) {
                newValue.set(((Number) value).doubleValue());
            } else if (type.equals(Float.class)) {
                newValue.set(((Number) value).floatValue());
            }
        }

        this.getOne(id)
            .ifPresent(geoOrdreLigne -> {
                ReflectionUtils.setField(field, geoOrdreLigne, newValue.get());

                this.repository.save(geoOrdreLigne);

                FunctionResult functionResult = null;
                switch (fieldName) {
                    case "nombrePalettesCommandees":
                        functionResult = this.geoFunctionOrdreRepository.onChangeCdeNbPal(id, socCode);
                        break;

                    case "nombreColisPalette":
                        functionResult = this.geoFunctionOrdreRepository.onChangePalNbCol(id, this.securityService.getUser().getUsername());
                        break;

                    case "nombreColisCommandes":
                        functionResult = this.geoFunctionOrdreRepository.onChangeCdeNbCol(id, this.securityService.getUser().getUsername());
                        break;

                    case "proprietaireMarchandise":
                        if(this.geoFunctionOrdreRepository.fVerifLogistiqueOrdre(id).getRes() != RESULT_UNKNOWN) {
                            functionResult = this.geoFunctionOrdreRepository.onChangeProprCode(id, this.securityService.getUser().getUsername(), socCode);
                        }
                        break;

                    case "fournisseur":
                        if(this.geoFunctionOrdreRepository.fVerifLogistiqueOrdre(id).getRes() != RESULT_UNKNOWN) {
                            functionResult = this.geoFunctionOrdreRepository.onChangeFouCode(id, this.securityService.getUser().getUsername(), socCode);
                        }
                        break;

                    case "ventePrixUnitaire":
                        functionResult = this.geoFunctionOrdreRepository.onChangeVtePu(id);
                        break;

                    case "gratuit":
                        functionResult = this.geoFunctionOrdreRepository.onChangeIndGratuit(id);
                        break;

                    case "achatDevisePrixUnitaire":
                        functionResult = this.geoFunctionOrdreRepository.onChangeAchDevPu(id, socCode);
                        break;

                    case "typePalette":
                        functionResult = this.geoFunctionOrdreRepository.onChangePalCode(id, this.securityService.getUser().getUsername(), socCode);
                        break;

                    case "paletteInter":
                        functionResult = this.geoFunctionOrdreRepository.onChangePalinterCode(id);
                        break;

                    case "nombrePalettesIntermediaires":
                        functionResult = this.geoFunctionOrdreRepository.onChangePalNbPalinter(id, this.securityService.getUser().getUsername());
                        break;
                }

                // Si le résultat est bon, on retourne la ligne de commande dans la réponse.

                if(functionResult == null || functionResult.getRes() == RESULT_UNKNOWN) {
                    String msg = functionResult != null ?
                        functionResult.getMsg() :
                        String.format("Le champ \"%s\" n'est pas supporté !!!", fieldName);

                    throw new GraphQLException(msg);
                }

                this.repository.findById(id).ifPresent(result::set);
            });

        return result.get();
    }
}
