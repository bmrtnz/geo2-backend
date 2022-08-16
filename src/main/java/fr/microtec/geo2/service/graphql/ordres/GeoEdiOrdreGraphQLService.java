package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoCommandeEdi;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.ordres.GeoEdiOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.service.OrdreService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoEdiOrdreGraphQLService {

    private final GeoEdiOrdreRepository repository;
    private final GeoFunctionOrdreRepository functionOrdreRepository;

    public GeoEdiOrdreGraphQLService(GeoEdiOrdreRepository repository, GeoFunctionOrdreRepository functionOrdreRepository) {
        this.repository = repository;
        this.functionOrdreRepository = functionOrdreRepository;
    }

    @GraphQLQuery
    public List<GeoCommandeEdi> allCommandeEdi(
        @GraphQLArgument(name = "secteurId") String secteurId,
        @GraphQLArgument(name = "clientId") String clientId,
        @GraphQLArgument(name = "status") String status,
        @GraphQLArgument(name = "dateMin") LocalDateTime dateMin,
        @GraphQLArgument(name = "dateMax") LocalDateTime dateMax,
        @GraphQLArgument(name = "assistantId") String assistantId,
        @GraphQLArgument(name = "commercialId") String commercialId,
        @GraphQLArgument(name = "ediOrdreId") String ediOrdreId
    ) {
        List<GeoCommandeEdi> commandeEdiList = this.repository.allCommandeEdi(secteurId, clientId, status, dateMin, dateMax, assistantId, commercialId, ediOrdreId);

        /*commandeEdiList.stream()
            .map(GeoCommandeEdi::getOrdreId)
            .distinct()
            .collect(Collectors.toList())
            .forEach(ref -> {
                FunctionResult resultInitBlocage = this.functionOrdreRepository.fInitBlocageOrdre(ref, "STEPHANE");

                System.out.println(ref);
                System.out.println("Résultat bloquer : " + resultInitBlocage.getData().get("bloquer"));
                commandeEdiList.stream()
                    .filter(c -> c.getOrdreId() != null && c.getOrdreId().equals(ref))
                    .forEach(c -> c.setInitBlocageOrdre(Boolean.TRUE.equals(resultInitBlocage.getData().get("bloquer"))));
            });

        commandeEdiList.stream()
            .map(GeoCommandeEdi::getRefEdiOrdre)
            .distinct()
            .collect(Collectors.toList())
            .forEach(ref -> {
                FunctionResult resultVerifStatus = this.functionOrdreRepository.fVerifStatusLigEdi(ref);

                System.out.println(ref);
                commandeEdiList.stream()
                    .filter(c -> c.getRefEdiOrdre().equals(ref))
                    .forEach(c -> c.setVerifStatusEdi("O".equals(resultVerifStatus.getData().get("status"))));
            });*/

        return commandeEdiList;
    }

    @GraphQLQuery
    public List<GeoClient> allClientEdi(
        @GraphQLArgument(name = "secteurId") String secteurId,
        @GraphQLArgument(name = "assistantId") String assistantId,
        @GraphQLArgument(name = "commercialId") String commercialId
    ) {
        return this.repository.allClientEdi(secteurId, assistantId, commercialId);
    }

}
