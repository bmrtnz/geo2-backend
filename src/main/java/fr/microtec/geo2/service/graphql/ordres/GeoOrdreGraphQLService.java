package fr.microtec.geo2.service.graphql.ordres;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoDeclarationFraude;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreStatut;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningDepart;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningMaritime;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningTransporteur;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.service.DocumentService;
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
    private final DocumentService documentService;

    public GeoOrdreGraphQLService(GeoOrdreRepository repository, OrdreService ordreService,
            DocumentService documentService) {
        super(repository, GeoOrdre.class);
        this.ordreService = ordreService;
        this.documentService = documentService;
    }

    @GraphQLQuery
    public RelayPage<GeoOrdre> allOrdre(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public List<GeoPlanningTransporteur> allPlanningTransporteurs(
            @GraphQLArgument(name = "dateMin") LocalDateTime dateMin,
            @GraphQLArgument(name = "dateMax") LocalDateTime dateMax,
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "transporteurCode") String transporteurCode) {
        return this.ordreService.allPlanningTransporteurs(
                dateMin,
                dateMax,
                societeCode,
                transporteurCode);
    }

    @GraphQLQuery
    public List<GeoPlanningMaritime> allPlanningDepartMaritime(
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "dateMin") LocalDateTime dateMin,
            @GraphQLArgument(name = "dateMax") LocalDateTime dateMax) {
        return ((GeoOrdreRepository) this.repository).allPlanningDepartMaritime(
                societeCode,
                dateMin,
                dateMax);
    }

    @GraphQLQuery
    public List<GeoPlanningMaritime> allPlanningArriveMaritime(
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "dateMin") LocalDateTime dateMin,
            @GraphQLArgument(name = "dateMax") LocalDateTime dateMax) {
        return ((GeoOrdreRepository) this.repository).allPlanningArriveMaritime(
                societeCode,
                dateMin,
                dateMax);
    }

    @GraphQLQuery
    public List<GeoPlanningDepart> allPlanningDepart(
            String societeCode,
            String secteurCode,
            LocalDateTime dateMin,
            LocalDateTime dateMax) {
        return ((GeoOrdreRepository) this.repository).allPlanningDepart(
                societeCode,
                secteurCode,
                dateMin,
                dateMax);
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
    public long nombreOrdreNonCloture(@GraphQLArgument(name = "search") String search) {
        return this.ordreService.fetchNombreOrdreNonCloture(search);
    }

    @GraphQLQuery
    public Optional<GeoOrdre> getOrdre(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

    @GraphQLQuery
    public Optional<GeoOrdre> getOrdreByNumeroAndSocieteAndCampagne(
            @GraphQLArgument(name = "numero") String numero,
            @GraphQLArgument(name = "societe") String societeID,
            @GraphQLArgument(name = "campagne") String campagneID) {
        return this.documentService.loadDocuments(
                this.ordreService.getOneByNumeroAndSocieteAndCampagne(numero, societeID, campagneID));
    }

    @GraphQLQuery
    public List<GeoDeclarationFraude> allDeclarationFraude(
            String secteur,
            String societe,
            LocalDate dateMin,
            LocalDate dateMax,
            LocalDateTime dateCreation,
            String client,
            String transporteur,
            String fournisseur,
            String bureauAchat,
            String entrepot) {
        return ((GeoOrdreRepository) this.repository).allDeclarationFraude(
                secteur,
                societe,
                dateMin,
                dateMax,
                Optional.ofNullable(dateCreation).orElse(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)),
                Optional.ofNullable(client).orElse("%"),
                Optional.ofNullable(transporteur).orElse("%"),
                Optional.ofNullable(fournisseur).orElse("%"),
                Optional.ofNullable(bureauAchat).orElse("%"),
                Optional.ofNullable(entrepot).orElse("%"));
    }

    @GraphQLMutation
    public GeoOrdre saveOrdre(GeoOrdre ordre, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.ordreService.save(ordre, env);
    }

    @GraphQLMutation
    public GeoOrdre cloneOrdre(GeoOrdre ordre, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.ordreService.clone(ordre, env);
    }

    @GraphQLMutation
    public boolean deleteOrdre(GeoOrdre ordre) {
        return this.delete(ordre);
    }

    @GraphQLMutation
    public List<GeoOrdre> saveAllOrdre(List<GeoOrdre> allOrdre) {
        return this.ordreService.save(allOrdre);
    }

    @GraphQLQuery
    public long countOrdre(
            @GraphQLArgument(name = "search") String search) {
        return this.count(search);
    }

    @GraphQLQuery
    public GeoOrdreStatut statut(@GraphQLContext GeoOrdre ordre) {
        return this.ordreService.fetchStatut(ordre.getId());
    }

    @GraphQLQuery
    public String descriptifRegroupement(@GraphQLContext GeoOrdre ordre) {
        return this.ordreService.fetchDescriptifRegroupement(ordre.getId());
    }
}
