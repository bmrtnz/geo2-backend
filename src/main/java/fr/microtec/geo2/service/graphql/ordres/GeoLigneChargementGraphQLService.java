package fr.microtec.geo2.service.graphql.ordres;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.ordres.GeoLigneChargement;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoLigneChargementRepository;
import fr.microtec.geo2.service.LigneChargementService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoLigneChargementGraphQLService extends GeoAbstractGraphQLService<GeoLigneChargement, String> {

    private final LigneChargementService ligneChargementService;

    public GeoLigneChargementGraphQLService(GeoLigneChargementRepository repository,
            LigneChargementService ligneChargementService) {
        super(repository, GeoLigneChargement.class);
        this.ligneChargementService = ligneChargementService;
    }

    @GraphQLQuery
    public List<GeoLigneChargement> allLignesChargement(
            @GraphQLArgument(name = "codeChargement") String codeChargement,
            @GraphQLArgument(name = "campagne") String campagne) {
        return ((GeoLigneChargementRepository) this.repository).allLignesChargement(
                codeChargement,
                campagne);
    }

    @GraphQLMutation
    public List<GeoLigneChargement> saveAllLigneChargement(List<GeoLigneChargement> allLigneChargement,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.ligneChargementService.saveAll(allLigneChargement, env);
    }

    @GraphQLQuery
    public List<GeoOrdreLigne> transfer(
            List<String> ordreLignesID,
            String codeChargement,
            String originalOrdreId,
            String societeId) {
        return this.ligneChargementService
                .transfer(ordreLignesID, codeChargement, originalOrdreId, societeId);
    }

    @GraphQLQuery
    public List<GeoOrdreLigne> duplicate(
            List<String> ordreLignesID,
            String codeChargement,
            String originalOrdreId,
            String societeId) {
        return this.ligneChargementService
                .duplicate(ordreLignesID, codeChargement, originalOrdreId, societeId);
    }

}
