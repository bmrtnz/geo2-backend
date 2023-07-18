package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoEdiLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoEdiLigneRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

import java.math.BigDecimal;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoEdiLigneGraphQLService extends GeoAbstractGraphQLService<GeoEdiLigne, BigDecimal> {

    public GeoEdiLigneGraphQLService(GeoEdiLigneRepository repository) {
        super(repository, GeoEdiLigne.class);
    }

    @GraphQLQuery
    public RelayPage<GeoEdiLigne> allEdiLigne(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLMutation
    public GeoEdiLigne saveEdiLigne(GeoEdiLigne ediLigne, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(ediLigne, env);
    }

    @GraphQLMutation
    public void deleteEdiLigne(BigDecimal id) {
        this.delete(id);
    }

}
