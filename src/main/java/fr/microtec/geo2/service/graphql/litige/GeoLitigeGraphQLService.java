package fr.microtec.geo2.service.graphql.litige;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.litige.GeoLitige;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeAPayer;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeSupervision;
import fr.microtec.geo2.persistance.repository.litige.GeoLitigeRepository;
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
public class GeoLitigeGraphQLService extends GeoAbstractGraphQLService<GeoLitige, String> {

    public GeoLitigeGraphQLService(GeoLitigeRepository repository) {
        super(repository, GeoLitige.class);
    }

    @GraphQLQuery
    public RelayPage<GeoLitige> allLitige(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoLitige> getLitige(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

    @GraphQLQuery
    public List<GeoLitigeAPayer> allLitigeAPayer(
            @GraphQLArgument(name = "litigeID") String litigeID) {
        return ((GeoLitigeRepository) super.repository).allLitigeAPayer(litigeID);
    }

    @GraphQLMutation
    public GeoLitige saveLitige(GeoLitige litige, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(litige, env);
    }

    @GraphQLQuery
    public List<GeoLitigeSupervision> allSupervisionLitige(String type, String code) {
        return ((GeoLitigeRepository) super.repository).allSupervisionLitige(type, code);
    }

    @GraphQLQuery
    public String genNumLot(String litigeID) {
        return ((GeoLitigeRepository) super.repository).genNumLot(litigeID);
    }

    @GraphQLQuery
    public int[] countCauseConseq(String ordreID) {
        return ((GeoLitigeRepository) super.repository).countCauseConseq(ordreID);
    }

    @GraphQLQuery
    public int countLinkedOrders(String ordreID) {
        return ((GeoLitigeRepository) super.repository).countLinkedOrders(ordreID);
    }

}
