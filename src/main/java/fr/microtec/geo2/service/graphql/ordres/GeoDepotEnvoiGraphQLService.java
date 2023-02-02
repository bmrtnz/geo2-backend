package fr.microtec.geo2.service.graphql.ordres;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoDepotEnvoi;
import fr.microtec.geo2.persistance.repository.ordres.GeoDepotEnvoiRepository;
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
public class GeoDepotEnvoiGraphQLService extends GeoAbstractGraphQLService<GeoDepotEnvoi, BigDecimal> {

    public GeoDepotEnvoiGraphQLService(GeoDepotEnvoiRepository repository) {
        super(repository, GeoDepotEnvoi.class);
    }

    @GraphQLQuery
    public RelayPage<GeoDepotEnvoi> allDepotEnvoi(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoDepotEnvoi> getDepotEnvoi(
            @GraphQLArgument(name = "id") BigDecimal id) {
        return super.getOne(id);
    }

    @GraphQLMutation
    public GeoDepotEnvoi saveDepotEnvoi(GeoDepotEnvoi depotEnvoi, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(depotEnvoi, env);
    }

    @GraphQLMutation
    public void deleteDepotEnvoi(BigDecimal id) {
        this.delete(id);
    }

}
