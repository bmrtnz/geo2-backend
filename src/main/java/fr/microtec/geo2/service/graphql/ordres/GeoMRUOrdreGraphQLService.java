package fr.microtec.geo2.service.graphql.ordres;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.Predicate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.common.CustomUtils;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.repository.ordres.GeoMRUOrdreRepository;
import fr.microtec.geo2.service.MRUOrdreService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.security.SecurityService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
// @Secured("ROLE_USER")
public class GeoMRUOrdreGraphQLService extends GeoAbstractGraphQLService<GeoMRUOrdre, GeoMRUOrdreKey> {

    private final MRUOrdreService mruOrdreService;
    private final GeoMRUOrdreRepository repo;
    private final SecurityService securityService;

    public GeoMRUOrdreGraphQLService(
            GeoMRUOrdreRepository repository,
            MRUOrdreService mruOrdreService,
            SecurityService securityService) {
        super(repository, GeoMRUOrdre.class);
        this.repo = repository;
        this.mruOrdreService = mruOrdreService;
        this.securityService = securityService;
    }

    @GraphQLQuery
    public RelayPage<GeoMRUOrdre> allMRUOrdre(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public RelayPage<GeoMRUOrdre> allGroupedMRUOrdre(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable) {
        return this.mruOrdreService.fetchGroupedMRUOrdre(search, pageable);
    }

    @GraphQLQuery
    public List<GeoMRUOrdre> allMRUOrdreHeadList(
            @GraphQLArgument(name = "societe") String societe,
            @GraphQLArgument(name = "count", defaultValue = "20") Long count,
            @GraphQLEnvironment ResolutionEnvironment env) {
        Specification<GeoMRUOrdre> spec = (root, query, cb) -> {
            Predicate whereSociete = cb.equal(root
                    .get("societe")
                    .get("id"),
                    societe);
            Predicate whereUser = cb.equal(root
                    .get("utilisateur")
                    .get("nomUtilisateur"),
                    this.securityService.getUser().getNomUtilisateur());
            Predicate whereModificationDate = cb.greaterThan(root
                    .get("dateModification"),
                    LocalDateTime.now().minusDays(60));
            Predicate whereOrdreExist = cb.isNotNull(root.get("ordre"));
            return cb.and(whereSociete, whereOrdreExist, whereUser,
                    whereModificationDate);
        };
        Set<String> fields = CustomUtils.parseSelectFromEnv(env);
        return this.repository.findAllWithPaginations(
                spec,
                PageRequest.of(0, count.intValue(), Sort.by(Direction.DESC, "dateModification")),
                GeoMRUOrdre.class,
                fields);
    }

    @GraphQLQuery
    public Optional<GeoMRUOrdre> getMRUOrdre(
            @GraphQLArgument(name = "id") GeoMRUOrdreKey id) {
        return super.getOne(id);
    }

    @GraphQLMutation
    public GeoMRUOrdre saveMRUOrdre(GeoMRUOrdre mruOrdre, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.repository.save(mruOrdre);
    }

}
