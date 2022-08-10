package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.persistance.entity.ordres.GeoCommandeEdi;
import fr.microtec.geo2.persistance.repository.ordres.GeoEdiOrdreRepository;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoEdiOrdreGraphQLService {

    private final GeoEdiOrdreRepository repository;

    public GeoEdiOrdreGraphQLService(GeoEdiOrdreRepository repository) {
        this.repository = repository;
    }

    @GraphQLQuery
    public List<GeoCommandeEdi> allCommandeEdit(
        @GraphQLArgument(name = "secteurId") String secteurId,
        @GraphQLArgument(name = "clientId") String clientId,
        @GraphQLArgument(name = "status") String status,
        @GraphQLArgument(name = "dateMin") LocalDateTime dateMin,
        @GraphQLArgument(name = "dateMax") LocalDateTime dateMax,
        @GraphQLArgument(name = "assistantId") String assistantId,
        @GraphQLArgument(name = "commercialId") String commercialId
    ) {
        return this.repository.allCommandeEdi(secteurId, clientId, status, dateMin, dateMax, assistantId, commercialId);
    }

}
