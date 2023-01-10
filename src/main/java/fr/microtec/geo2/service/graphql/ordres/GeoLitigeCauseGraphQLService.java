package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeCause;
import fr.microtec.geo2.persistance.repository.ordres.GeoLitigeCauseRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoLitigeCauseGraphQLService extends GeoAbstractGraphQLService<GeoLitigeCause, String> {

    public GeoLitigeCauseGraphQLService(GeoLitigeCauseRepository repository) {
        super(repository, GeoLitigeCause.class);
    }

    @GraphQLQuery
    public RelayPage<GeoLitigeCause> allLitigeCause(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public List<GeoLitigeCause> allLitigeCauseList(
            @GraphQLArgument(name = "search") String search) {
        return this.getAll(search);
    }

    @GraphQLQuery
    public Optional<GeoLitigeCause> getLitigeCause(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

}
