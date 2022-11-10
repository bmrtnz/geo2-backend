package fr.microtec.geo2.service.graphql.ordres;

import java.math.BigDecimal;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.service.IndicateursCountService;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

enum Indicateur {
    ClientsDepassementEncours,
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
    public BigDecimal countByIndicator(Indicateur indicateur) {
        switch (indicateur) {
            case ClientsDepassementEncours:
                return this.indicateursCountService.countClientsDepassementEncours();
            default:
                throw new RuntimeException(String.format("Indicator %1 does not exist", indicateur));
        }
    }

}
