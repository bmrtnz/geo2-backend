package fr.microtec.geo2.service.graphql.ordres;

import static fr.microtec.geo2.persistance.entity.FunctionResult.RESULT_OK;
import static fr.microtec.geo2.persistance.entity.FunctionResult.RESULT_UNKNOWN;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoCodePromo;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import fr.microtec.geo2.persistance.repository.ordres.GeoCodePromoRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoBaseTarifRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoFournisseurRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoTypePaletteRepository;
import fr.microtec.geo2.service.OrdreLigneService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.security.SecurityService;
import graphql.GraphQLException;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
@Slf4j
public class GeoOrdreLigneGraphQLService extends GeoAbstractGraphQLService<GeoOrdreLigne, String> {

    private final GeoBaseTarifRepository geoBaseTarifRepository;
    private final GeoFournisseurRepository geoFournisseurRepository;
    private final GeoFunctionOrdreRepository geoFunctionOrdreRepository;
    private final GeoTypePaletteRepository geoTypePaletteRepository;
    private final GeoCodePromoRepository geoCodePromoRepository;
    private final OrdreLigneService ordreLigneService;
    private final SecurityService securityService;

    @PersistenceContext
    private EntityManager entityManager;

    public GeoOrdreLigneGraphQLService(
            GeoOrdreLigneRepository repository, GeoBaseTarifRepository geoBaseTarifRepository,
            GeoFournisseurRepository geoFournisseurRepository, GeoFunctionOrdreRepository geoFunctionOrdreRepository,
            GeoTypePaletteRepository geoTypePaletteRepository, GeoCodePromoRepository geoCodePromoRepository,
            OrdreLigneService ordreLigneService,
            SecurityService securityService) {
        super(repository, GeoOrdreLigne.class);
        this.geoBaseTarifRepository = geoBaseTarifRepository;
        this.geoFournisseurRepository = geoFournisseurRepository;
        this.geoFunctionOrdreRepository = geoFunctionOrdreRepository;
        this.geoTypePaletteRepository = geoTypePaletteRepository;
        this.geoCodePromoRepository = geoCodePromoRepository;
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
    @Transactional
    public GeoOrdreLigne updateField(final String fieldName, final String id, final Object value,
            final String socCode) {

        final AtomicReference<GeoOrdreLigne> result = new AtomicReference<>();
        final AtomicReference<Object> newValue = new AtomicReference<>(value);

        final Field field = ReflectionUtils.findField(GeoOrdreLigne.class, fieldName);
        Assert.notNull(field,
                String.format("Le champ \"%s\" n'a pas été trouvé dans l'objet \"GeoOrdreLigne\".", fieldName));
        field.setAccessible(true);

        final Class<?> type = field.getType();
        if (type.equals(GeoBaseTarif.class)) {
            this.geoBaseTarifRepository.findById(String.valueOf(value)).ifPresent(newValue::set);
        } else if (type.equals(GeoFournisseur.class)) {
            this.geoFournisseurRepository.findById(String.valueOf(value)).ifPresent(newValue::set);
        } else if (type.equals(GeoTypePalette.class)) {
            this.geoTypePaletteRepository.findById(String.valueOf(value)).ifPresent(newValue::set);
        } else if (type.equals(GeoCodePromo.class)) {
            this.geoCodePromoRepository.findById(String.valueOf(value)).ifPresent(newValue::set);
        } else {
            if (value instanceof Number) {
                if (type.equals(Double.class)) {
                    newValue.set(((Number) value).doubleValue());
                } else if (type.equals(Float.class)) {
                    newValue.set(((Number) value).floatValue());
                }
            }
        }

        this.getOne(id)
                .ifPresent(geoOrdreLigne -> {
                    ReflectionUtils.setField(field, geoOrdreLigne, newValue.get());
                    String scoCode = geoOrdreLigne.getOrdre().getSecteurCommercial().getId();

                    this.repository.saveAndFlush(geoOrdreLigne); // Flush est obligatoire

                    FunctionResult functionResult = null;
                    switch (fieldName) {
                        case "nombrePalettesCommandees":
                            functionResult = this.geoFunctionOrdreRepository.onChangeCdeNbPal(id, scoCode);
                            break;

                        case "nombreColisPalette":
                            functionResult = this.geoFunctionOrdreRepository.onChangePalNbCol(id,
                                    this.securityService.getUser().getUsername());
                            break;

                        case "nombreColisCommandes":
                            functionResult = this.geoFunctionOrdreRepository.onChangeCdeNbCol(id,
                                    this.securityService.getUser().getUsername());
                            break;

                        case "proprietaireMarchandise":
                            functionResult = this.geoFunctionOrdreRepository.onChangeProprCode(id,
                                    this.securityService.getUser().getUsername(), socCode);
                            break;

                        case "fournisseur":
                            if (this.geoFunctionOrdreRepository.fVerifLogistiqueOrdre(geoOrdreLigne.getOrdre().getId())
                                    .getRes() != RESULT_UNKNOWN) {
                                functionResult = this.geoFunctionOrdreRepository.onChangeFouCode(id,
                                        this.securityService.getUser().getUsername(), socCode);
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
                            functionResult = this.geoFunctionOrdreRepository.onChangePalCode(id,
                                    this.securityService.getUser().getUsername(), scoCode);
                            break;

                        case "paletteInter":
                            functionResult = this.geoFunctionOrdreRepository.onChangePalinterCode(id);
                            break;

                        case "nombrePalettesIntermediaires":
                            functionResult = this.geoFunctionOrdreRepository.onChangePalNbPalinter(id,
                                    this.securityService.getUser().getUsername());
                            break;
                    }

                    if (functionResult != null && functionResult.getRes() != RESULT_OK) {
                        String msg = functionResult.getRes() == RESULT_UNKNOWN ? functionResult.getMsg()
                                : String.format("Erreur lors de la mise à jour du champ \"%s\" ( %s )", fieldName,
                                        functionResult.getMsg());

                        throw new GraphQLException(msg);
                    }

                    // Si le résultat est bon, on retourne la ligne de commande dans la réponse.
                    // On force le refresh depuis la BDD
                    this.entityManager.refresh(geoOrdreLigne);
                    result.set(geoOrdreLigne);
                });

        return result.get();
    }
}
