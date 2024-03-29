package fr.microtec.geo2.service.graphql.ordres;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLogistique;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLogistiqueRepository;
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
public class GeoOrdreLogistiqueGraphQLService extends GeoAbstractGraphQLService<GeoOrdreLogistique, String> {

    public GeoOrdreLogistiqueGraphQLService(GeoOrdreLogistiqueRepository repository) {
        super(repository, GeoOrdreLogistique.class);
    }

    @GraphQLQuery
    public RelayPage<GeoOrdreLogistique> allOrdreLogistique(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoOrdreLogistique> getOrdreLogistique(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

    @GraphQLMutation
    public GeoOrdreLogistique saveOrdreLogistique(GeoOrdreLogistique ordreLogistique,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(ordreLogistique, env);
    }

    @GraphQLMutation
    public boolean deleteOrdreLogistique(String id) {
        return this.delete(id);
    }

    @GraphQLQuery
    public Long countOrdreLogistique(
            @GraphQLArgument(name = "search") String search) {
        return this.count(search);
    }

    @GraphQLMutation
    public List<GeoOrdreLogistique> saveAllOrdreLogistique(List<GeoOrdreLogistique> allOrdreLogistique,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveAllEntities(allOrdreLogistique, env);
    }

}
