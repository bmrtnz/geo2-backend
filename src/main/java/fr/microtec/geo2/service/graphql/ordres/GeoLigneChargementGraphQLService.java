package fr.microtec.geo2.service.graphql.ordres;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.ordres.GeoLigneChargement;
import fr.microtec.geo2.persistance.repository.ordres.GeoLigneChargementRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoLigneChargementGraphQLService extends GeoAbstractGraphQLService<GeoLigneChargement, Integer> {

    public GeoLigneChargementGraphQLService(GeoLigneChargementRepository repository) {
        super(repository, GeoLigneChargement.class);
    }

    @GraphQLQuery
    public List<GeoLigneChargement> allLignesChargement(
            @GraphQLArgument(name = "codeChargement") String codeChargement,
            @GraphQLArgument(name = "campagne") String campagne) {
        return ((GeoLigneChargementRepository) this.repository).allLignesChargement(
                codeChargement,
                campagne);
    }

}
