package fr.microtec.geo2.service.graphql.ordres;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoLitigeConsequence;
import fr.microtec.geo2.persistance.repository.ordres.GeoLitigeConsequenceRepository;
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
public class GeoLitigeConsequenceGraphQLService extends GeoAbstractGraphQLService<GeoLitigeConsequence, String> {

    public GeoLitigeConsequenceGraphQLService(GeoLitigeConsequenceRepository repository) {
        super(repository, GeoLitigeConsequence.class);
    }

    @GraphQLQuery
    public RelayPage<GeoLitigeConsequence> allLitigeConsequence(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public List<GeoLitigeConsequence> allLitigeConsequenceList(
            @GraphQLArgument(name = "search") String search) {
        return this.getAll(search);
    }

    @GraphQLQuery
    public Optional<GeoLitigeConsequence> getLitigeConsequence(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

}
