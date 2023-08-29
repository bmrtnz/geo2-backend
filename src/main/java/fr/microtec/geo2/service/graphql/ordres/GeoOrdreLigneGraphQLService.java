package fr.microtec.geo2.service.graphql.ordres;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.litige.GeoOrdreLigneLitigePick;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoCodePromoRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoBaseTarifRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoFournisseurRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoTypePaletteRepository;
import fr.microtec.geo2.service.OrdreLigneService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.security.SecurityService;
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

    private final OrdreLigneService ordreLigneService;

    @PersistenceContext
    private EntityManager entityManager;

    public GeoOrdreLigneGraphQLService(
            GeoOrdreLigneRepository repository, GeoBaseTarifRepository geoBaseTarifRepository,
            GeoFournisseurRepository geoFournisseurRepository, GeoFunctionOrdreRepository geoFunctionOrdreRepository,
            GeoTypePaletteRepository geoTypePaletteRepository, GeoCodePromoRepository geoCodePromoRepository,
            OrdreLigneService ordreLigneService,
            SecurityService securityService) {
        super(repository, GeoOrdreLigne.class);
        this.ordreLigneService = ordreLigneService;
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
        return this.getUnpaged(search, env);
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
    public List<GeoOrdreLigne> reindex(
            @GraphQLArgument(name = "lignes") List<String> lignes) {
        return this.ordreLigneService.reindex(lignes);
    }

    @GraphQLQuery
    @Deprecated
    public RelayPage<GeoOrdreLigne> allOrdreLigneSuiviDeparts(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLArgument(name = "onlyColisDiff") Boolean onlyColisDiff) {
        return this.ordreLigneService
                .fetchOrdreLigneSuiviDeparts(search, pageable, onlyColisDiff);
    }

    @GraphQLQuery
    public List<GeoOrdreLigneLitigePick> wLitigePickOrdreOrdligV2(String ordreID) {
        return ((GeoOrdreLigneRepository) this.repository).wLitigePickOrdreOrdligV2(ordreID);
    }
}
