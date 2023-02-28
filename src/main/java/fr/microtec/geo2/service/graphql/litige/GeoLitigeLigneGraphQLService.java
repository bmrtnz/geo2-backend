package fr.microtec.geo2.service.graphql.litige;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigne;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneFait;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneForfait;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneTotaux;
import fr.microtec.geo2.persistance.repository.litige.GeoLitigeLigneRepository;
import fr.microtec.geo2.service.OrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
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
public class GeoLitigeLigneGraphQLService extends GeoAbstractGraphQLService<GeoLitigeLigne, String> {

    private final OrdreService ordreService;

    public GeoLitigeLigneGraphQLService(
            GeoLitigeLigneRepository repository,
            OrdreService ordreService) {
        super(repository, GeoLitigeLigne.class);
        this.ordreService = ordreService;
    }

    @GraphQLQuery
    public RelayPage<GeoLitigeLigne> allLitigeLigne(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public List<GeoLitigeLigne> allLitigeLigneList(
            @GraphQLArgument(name = "search") String search) {
        return this.getAll(search);
    }

    @GraphQLQuery
    public Optional<GeoLitigeLigneTotaux> getLitigeLigneTotaux(
            @GraphQLArgument(name = "litige") @GraphQLNonNull String litige) {
        return this.ordreService.fetchLitigeLignesTotaux(litige);
    }

    @GraphQLQuery
    public Optional<GeoLitigeLigne> getLitigeLigne(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

    @GraphQLMutation
    public void deleteAllLitigeLigne(List<String> ids) {
        ((GeoLitigeLigneRepository) this.repository).deleteAllByIdIn(ids);
    }

    @GraphQLQuery
    public List<GeoLitigeLigneFait> allLitigeLigneFait(String litigeID, String numeroLigne) {
        return ((GeoLitigeLigneRepository) super.repository).allLitigeLigneFait(litigeID, numeroLigne);
    }

    @GraphQLQuery
    public List<GeoLitigeLigneForfait> allLitigeLigneForfait(String litigeID) {
        return ((GeoLitigeLigneRepository) super.repository).allLitigeLigneForfait(litigeID);
    }

}
