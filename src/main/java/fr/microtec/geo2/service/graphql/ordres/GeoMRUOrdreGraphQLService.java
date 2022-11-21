package fr.microtec.geo2.service.graphql.ordres;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        return this.repo.findHead(
                societe,
                this.securityService.getUser().getNomUtilisateur(),
                LocalDateTime.now().minusDays(60),
                count);
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
