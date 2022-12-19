package fr.microtec.geo2.service.graphql.ordres;

import java.math.BigDecimal;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.ordres.GeoPacklistEntete;
import fr.microtec.geo2.persistance.repository.ordres.GeoPacklistRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoPacklistGraphQLService extends GeoAbstractGraphQLService<GeoPacklistEntete, BigDecimal> {

    public GeoPacklistGraphQLService(GeoPacklistRepository repository) {
        super(repository, GeoPacklistEntete.class);
    }

    @GraphQLMutation
    public GeoPacklistEntete savePacklist(GeoPacklistEntete packlist, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(packlist, env);
    }

}
