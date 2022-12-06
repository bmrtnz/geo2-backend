package fr.microtec.geo2.service.graphql.ordres;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoReferenceClient;
import fr.microtec.geo2.persistance.repository.ordres.GeoReferenceClientRepository;
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
public class GeoReferenceClientGraphQLService extends GeoAbstractGraphQLService<GeoReferenceClient, String> {

    public GeoReferenceClientGraphQLService(GeoReferenceClientRepository repository) {
        super(repository, GeoReferenceClient.class);
    }

    @GraphQLQuery
    public RelayPage<GeoReferenceClient> allReferenceClient(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoReferenceClient> getReferenceClient(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

    @GraphQLMutation
    public List<GeoReferenceClient> saveAllReferenceClient(List<GeoReferenceClient> allReferenceClient,
            @GraphQLEnvironment ResolutionEnvironment env) {

        // Filter inexistants refs
        List<GeoReferenceClient> newRefs = allReferenceClient.parallelStream().filter(elm -> {
            return this.repository.findOne((root, cq, cb) -> cb.and(
                    cb.equal(root.get("client").get("id"), elm.getClient().getId()),
                    cb.equal(root.get("article").get("id"), elm.getArticle().getId()))).isEmpty();
        }).collect(Collectors.toList());

        return this.saveAllEntities(newRefs, env);
    }

    @GraphQLMutation
    public void removeRefs(String client, List<String> articles) {
        ((GeoReferenceClientRepository) this.repository).removeRefs(client, articles);
    }

}
