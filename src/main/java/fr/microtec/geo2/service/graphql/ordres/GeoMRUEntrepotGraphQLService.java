package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUEntrepot;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUEntrepotKey;
import fr.microtec.geo2.persistance.repository.ordres.GeoMRUEntrepotRepository;
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
@Secured("ROLE_USER")
public class GeoMRUEntrepotGraphQLService extends GeoAbstractGraphQLService<GeoMRUEntrepot, GeoMRUEntrepotKey> {

    private final SecurityService securityService;

    public GeoMRUEntrepotGraphQLService(
            GeoMRUEntrepotRepository repository, SecurityService securityService) {
        super(repository, GeoMRUEntrepot.class);
        this.securityService = securityService;
    }

    @GraphQLQuery
    public RelayPage<GeoMRUEntrepot> allMRUEntrepot(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoMRUEntrepot> getMRUEntrepot(
            @GraphQLArgument(name = "id") GeoMRUEntrepotKey id) {
        return super.getOne(id);
    }

    @GraphQLMutation
    public GeoMRUEntrepot saveMRUEntrepot(GeoMRUEntrepot mruEntrepot, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.repository.save(mruEntrepot);
    }

    @GraphQLMutation
    public void deleteOneMRUEntrepot(String entrepotId) {
        ((GeoMRUEntrepotRepository) this.repository)
                .deleteOneByEntrepotIdAndUtilisateur(entrepotId, this.securityService.getUser());
    }

    @GraphQLMutation
    public void deleteAllMRUEntrepot() {
        ((GeoMRUEntrepotRepository) this.repository)
                .deleteAllByUtilisateur(this.securityService.getUser());
    }

}
