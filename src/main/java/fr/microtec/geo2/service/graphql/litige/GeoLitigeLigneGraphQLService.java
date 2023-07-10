package fr.microtec.geo2.service.graphql.litige;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.litige.GeoLitige;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigne;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneFait;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneForfait;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneTotaux;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.repository.litige.GeoLitigeLigneRepository;
import fr.microtec.geo2.persistance.repository.litige.GeoLitigeRepository;
import fr.microtec.geo2.service.EnvoisService;
import fr.microtec.geo2.service.OrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import graphql.GraphQLException;
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
public class GeoLitigeLigneGraphQLService extends GeoAbstractGraphQLService<GeoLitigeLigne, String> {

    private final OrdreService ordreService;
    private final EnvoisService envoisService;
    private final GeoLitigeRepository litigeRepository;

    public GeoLitigeLigneGraphQLService(
            GeoLitigeLigneRepository repository,
            OrdreService ordreService,
            EnvoisService envoisService,
            GeoLitigeRepository litigeRepository) {
        super(repository, GeoLitigeLigne.class);
        this.ordreService = ordreService;
        this.envoisService = envoisService;
        this.litigeRepository = litigeRepository;
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
            @GraphQLArgument(name = "search") String search,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getUnpaged(search, env);
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

    @GraphQLMutation
    public void deleteLitigeLigne(String id) {
        this.delete(id);
    }

    @GraphQLMutation
    public void deleteLot(String litigeID, String groupementID) {
        Optional<GeoLitige> maybeLitige = this.litigeRepository.findOne((root, query, cb) -> {
            Predicate whereID = cb.equal(root.get("id"), litigeID);
            return cb.and(whereID);
        });

        maybeLitige.ifPresentOrElse(litige -> {
            GeoOrdre ordre = litige.getOrdreOrigine();
            if (this.envoisService.countLitigeEnvois(ordre.getId()) > 0)
                throw new GraphQLException("Impossible de supprimer ce lot car des envois existent");
            else
                ((GeoLitigeLigneRepository) this.repository)
                        .deleteAllByLitigeIdAndNumeroGroupementLitige(litigeID, groupementID);
        }, () -> {
            throw new GraphQLException("Le litige demandé est inexistant");
        });

    }

    @GraphQLQuery
    public List<GeoLitigeLigneFait> allLitigeLigneFait(String litigeID, String numeroLigne) {
        return ((GeoLitigeLigneRepository) super.repository).allLitigeLigneFait(litigeID, numeroLigne);
    }

    @GraphQLQuery
    public List<GeoLitigeLigneForfait> allLitigeLigneForfait(String litigeID) {
        return ((GeoLitigeLigneRepository) super.repository).allLitigeLigneForfait(litigeID);
    }

    @GraphQLMutation
    public GeoLitigeLigne saveLitigeLigne(GeoLitigeLigne litigeLigne, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(litigeLigne, env);
    }

    @GraphQLMutation
    public List<GeoLitigeLigne> saveAllLitigeLigne(List<GeoLitigeLigne> allLitigeLigne,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveAllEntities(allLitigeLigne, env);
    }

}
