package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.service.OrdreService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Secured("ROLE_USER")
@GraphQLApi
public class GeoFunctionsOrdreGraphQLService {

    private final GeoFunctionOrdreRepository repository;
    private final OrdreService ordreService;

    public GeoFunctionsOrdreGraphQLService(
            GeoFunctionOrdreRepository repository,
            OrdreService ordreService) {
        this.repository = repository;
        this.ordreService = ordreService;
    }

    @GraphQLQuery
    public FunctionResult ofValideEntrepotForOrdre(
            @GraphQLArgument(name = "entrepotID") String entrepotID) {
        return this.repository.ofValideEntrepotForOrdre(entrepotID);
    }

    @GraphQLQuery
    public FunctionResult fNouvelOrdre(
            @GraphQLArgument(name = "societe") String socCode) {
        return this.repository.fNouvelOrdre(socCode);
    }

    @GraphQLQuery
    public List<GeoOrdreBaf> fAfficheBaf(
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "secteurCode") String secteurCode,
            @GraphQLArgument(name = "clientCode") String clientCode,
            @GraphQLArgument(name = "entrepotCode") String entrepotCode,
            @GraphQLArgument(name = "dateMin") LocalDate dateMin,
            @GraphQLArgument(name = "dateMax") LocalDate dateMax,
            @GraphQLArgument(name = "codeAssistante") String codeAssistante,
            @GraphQLArgument(name = "codeCommercial") String codeCommercial) {
        return this.ordreService.allDepartBaf(societeCode, secteurCode, clientCode, entrepotCode, dateMin, dateMax,
                codeAssistante, codeCommercial);
    }

    @GraphQLQuery
    public FunctionResult ofInitArticle(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "articleRef") String articleRef,
            @GraphQLArgument(name = "societeCode") String societeCode) {
        return this.repository.ofInitArticle(ordreRef, articleRef, societeCode);
    }

    @GraphQLQuery
    public FunctionResult fInitBlocageOrdre(
            @GraphQLArgument(name = "ordreRef") String ordreRef,
            @GraphQLArgument(name = "userName") String userName) {
        return this.repository.fInitBlocageOrdre(ordreRef, userName);
    }

    @GraphQLQuery
    public FunctionResult onChangeCdeNbPal(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "secteurCommercialCode") String scoCode) {
        return this.repository.onChangeCdeNbPal(orlRef, scoCode);
    }

    @GraphQLQuery
    public FunctionResult onChangeDemipalInd(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.onChangeDemipalInd(orlRef, username);
    }

    @GraphQLQuery
    public FunctionResult onChangePalNbCol(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.onChangePalNbCol(orlRef, username);
    }

    @GraphQLQuery
    public FunctionResult onChangeCdeNbCol(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username) {
        return this.repository.onChangeCdeNbCol(orlRef, username);
    }

    @GraphQLQuery
    public FunctionResult onChangeProprCode(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "secteurCode") String scoCode) {
        return this.repository.onChangeProprCode(orlRef, username, scoCode);
    }

    @GraphQLQuery
    public FunctionResult onChangeFouCode(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "secteurCode") String scoCode) {
        return this.repository.onChangeFouCode(orlRef, username, scoCode);
    }

    @GraphQLQuery
    public FunctionResult onChangeVtePu(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef) {
        return this.repository.onChangeVtePu(orlRef);
    }

    @GraphQLQuery
    public FunctionResult onChangePalCode(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef,
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "secteurCode") String scoCode) {
        return this.repository.onChangePalCode(orlRef, username, scoCode);
    }

    @GraphQLQuery
    public FunctionResult onChangePalinterCode(
            @GraphQLArgument(name = "ordreLigneRef") String orlRef) {
        return this.repository.onChangePalinterCode(orlRef);
    }

}
