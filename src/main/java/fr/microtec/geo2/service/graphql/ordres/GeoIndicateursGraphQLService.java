package fr.microtec.geo2.service.graphql.ordres;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.service.IndicateursCountService;
import fr.microtec.geo2.service.IndicateursCountService.IndicateurCountResponse;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

enum Indicateur {
    ClientsDepassementEncours,
    OrdresNonConfirmes,
    PlanningDepart,
}

@Service
@Secured("ROLE_USER")
@GraphQLApi
public class GeoIndicateursGraphQLService {

    private final IndicateursCountService indicateursCountService;

    public GeoIndicateursGraphQLService(
            IndicateursCountService indicateursCountService) {
        this.indicateursCountService = indicateursCountService;
    }

    /**
     * Give entities count from specified indicator
     */
    @GraphQLQuery
    public IndicateurCountResponse countByIndicator(
            Indicateur indicateur,
            String societeCode,
            @GraphQLArgument(name = "secteurCode", defaultValue = "%") String secteurCode,
            @GraphQLArgument(name = "byUser", defaultValue = "true") Boolean byUser) {
        // Force `byUser = false` until implementation arise
        byUser = false;
        if (secteurCode == null)
            secteurCode = "%";
        switch (indicateur) {
            case ClientsDepassementEncours:
                return this.indicateursCountService.countClientsDepassementEncours(societeCode, secteurCode, byUser);
            case OrdresNonConfirmes:
                return this.indicateursCountService.countOrdresNonConfirmes(societeCode, secteurCode, byUser);
            case PlanningDepart:
                return this.indicateursCountService.countPlanningDepart(societeCode, secteurCode, byUser);
            default:
                throw new RuntimeException(String.format("Indicator %1 does not exist", indicateur));
        }
    }

}
