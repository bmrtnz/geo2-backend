package fr.microtec.geo2.service.graphql.tiers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import fr.microtec.geo2.persistance.entity.tiers.GeoPaysDepassement;
import fr.microtec.geo2.persistance.repository.tiers.GeoPaysRepository;
import fr.microtec.geo2.service.PaysService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoPaysGraphQLService extends GeoAbstractGraphQLService<GeoPays, String> {

    private final PaysService paysService;

    public GeoPaysGraphQLService(
            GeoPaysRepository repository,
            PaysService paysService) {
        super(repository, GeoPays.class);
        this.paysService = paysService;
    }

    @GraphQLQuery
    public RelayPage<GeoPays> allPays(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public List<GeoPaysDepassement> allPaysDepassementList(
            @GraphQLArgument(name = "depassementOnly", defaultValue = "true") Boolean depassementOnly,
            @GraphQLArgument(name = "secteurCode") String secteurCode,
            @GraphQLArgument(name = "societeCode") String societeCode,
            @GraphQLArgument(name = "commercialCode") String commercialCode) {
        return ((GeoPaysRepository) this.repository)
                .allPaysDepassement(
                        depassementOnly ? 'O' : 'N',
                        Optional.ofNullable(secteurCode).orElse("%"),
                        societeCode,
                        Optional.ofNullable(commercialCode).orElse("%"));
    }

    @GraphQLQuery
    public Long countPaysDepassement(
            @GraphQLArgument(name = "secteurCode") String secteurCode,
            @GraphQLArgument(name = "societeCode") String societeCode) {
        return ((GeoPaysRepository) this.repository)
                .countPaysDepassement(
                        Optional.ofNullable(secteurCode).orElse("%"),
                        societeCode);
    }

    @GraphQLQuery
    public RelayPage<GeoPays> allDistinctPays(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable) {
        return this.paysService.fetchDistinctPays(search, pageable);
    }

    @GraphQLQuery
    public List<GeoPays> allPaysList(
            @GraphQLArgument(name = "search") String search,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getAll(search);
    }

    @GraphQLQuery
    public Optional<GeoPays> getPays(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

    @GraphQLQuery
    public long countPays(
            @GraphQLArgument(name = "search") String search) {
        return this.count(search);
    }

}
