package fr.microtec.geo2.service.graphql.ordres;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoCommandeEdi;
import fr.microtec.geo2.persistance.entity.ordres.GeoEdiOrdre;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.ordres.GeoEdiOrdreRepository;
import fr.microtec.geo2.service.EdiOrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
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
public class GeoEdiOrdreGraphQLService extends GeoAbstractGraphQLService<GeoEdiOrdre, String> {

    private final GeoEdiOrdreRepository repository;
    private final EdiOrdreService ediOrdreService;

    public GeoEdiOrdreGraphQLService(GeoEdiOrdreRepository repository, EdiOrdreService ediOrdreService) {
        super(repository, GeoEdiOrdre.class);
        this.repository = repository;
        this.ediOrdreService = ediOrdreService;
    }

    @GraphQLQuery
    public RelayPage<GeoEdiOrdre> allEdiOrdre(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLMutation
    public GeoEdiOrdre saveEdiOrdre(GeoEdiOrdre ediOrdre, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(ediOrdre, env);
    }

    @GraphQLQuery
    @Transactional
    public List<GeoCommandeEdi> allCommandeEdi(
            @GraphQLArgument(name = "secteurId") String secteurId,
            @GraphQLArgument(name = "clientId") String clientId,
            @GraphQLArgument(name = "status") String status,
            @GraphQLArgument(name = "dateMin") LocalDateTime dateMin,
            @GraphQLArgument(name = "dateMax") LocalDateTime dateMax,
            @GraphQLArgument(name = "assistantId") String assistantId,
            @GraphQLArgument(name = "commercialId") String commercialId,
            @GraphQLArgument(name = "ediOrdreId") String ediOrdreId,
            @GraphQLArgument(name = "nomUtilisateur") String nomUtilisateur) {
        return this.ediOrdreService.allCommandeEdi(secteurId, clientId, status, dateMin, dateMax, assistantId,
                commercialId, ediOrdreId, nomUtilisateur);
    }

    @GraphQLQuery
    public List<GeoClient> allClientEdi(
            @GraphQLArgument(name = "secteurId") String secteurId,
            @GraphQLArgument(name = "assistantId") String assistantId,
            @GraphQLArgument(name = "commercialId") String commercialId) {
        return this.repository.allClientEdi(secteurId, assistantId, commercialId);
    }

}
