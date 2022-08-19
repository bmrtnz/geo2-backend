package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoEDILigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoEdiLigneRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoEdiLigneGraphQLService extends GeoAbstractGraphQLService<GeoEDILigne, String> {

    public GeoEdiLigneGraphQLService(GeoEdiLigneRepository repository) {
        super(repository, GeoEDILigne.class);
    }

    @GraphQLQuery
    public RelayPage<GeoEDILigne> allEdiLigne(
        @GraphQLArgument(name = "search") String search,
        @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
        @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLMutation
    public GeoEDILigne saveEdiLigne(GeoEDILigne ligne, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(ligne, env);
    }

}
