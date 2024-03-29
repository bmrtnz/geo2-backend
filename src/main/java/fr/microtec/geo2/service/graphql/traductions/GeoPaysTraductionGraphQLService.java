package fr.microtec.geo2.service.graphql.traductions;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.traductions.GeoPaysTraduction;
import fr.microtec.geo2.persistance.entity.traductions.GeoPaysTraductionId;
import fr.microtec.geo2.persistance.repository.traductions.GeoPaysTraductionRepository;
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

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoPaysTraductionGraphQLService extends GeoAbstractGraphQLService<GeoPaysTraduction, GeoPaysTraductionId> {

    public GeoPaysTraductionGraphQLService(GeoPaysTraductionRepository repository) {
        super(repository, GeoPaysTraduction.class);
    }

    @GraphQLQuery
    public RelayPage<GeoPaysTraduction> allPaysTraduction(
        @GraphQLArgument(name = "search") String search,
        @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
        @GraphQLEnvironment ResolutionEnvironment env
    ) {
        return this.getPage(search, pageable, env);
    }

}
