package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoDocumentNum;
import fr.microtec.geo2.persistance.entity.ordres.GeoDocumentNumKey;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoDocumentNumRepository;
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
public class GeoDocumentNumGraphQLService extends GeoAbstractGraphQLService<GeoDocumentNum, GeoDocumentNumKey> {

    public GeoDocumentNumGraphQLService(GeoDocumentNumRepository repository) {
        super(repository, GeoDocumentNum.class);
    }

    @GraphQLQuery
    public RelayPage<GeoDocumentNum> allDocumentNum(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoDocumentNum> getDocumentNum(
            @GraphQLArgument(name = "id") GeoDocumentNumKey id) {
        return super.getOne(id);
    }

    @GraphQLQuery
    public long countDocumentNum(@GraphQLArgument(name = "search") String search) {
        return this.count(search);
    }

    @GraphQLMutation
    public void deleteDocumentNum(GeoDocumentNumKey id) {
        this.delete(id);
    }

    @GraphQLMutation
    public void deleteByIdAndOrdreLigneAndTypeDocument(
            String id,
            GeoOrdreLigne ordreLigne,
            String typeDocument) {
        ((GeoDocumentNumRepository) this.repository)
                .deleteByIdAndOrdreLigneAndTypeDocument(id, ordreLigne, typeDocument);
    }

    @GraphQLMutation
    public GeoDocumentNum saveDocumentNum(GeoDocumentNum documentNum, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(documentNum, env);
    }

}
