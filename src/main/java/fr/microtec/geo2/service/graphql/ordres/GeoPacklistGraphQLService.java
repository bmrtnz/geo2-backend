package fr.microtec.geo2.service.graphql.ordres;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.persistance.entity.ordres.GeoPacklistEntete;
import fr.microtec.geo2.persistance.entity.ordres.GeoPacklistOrdre;
import fr.microtec.geo2.persistance.repository.ordres.GeoPacklistEnteteRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoPacklistOrdreRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoPacklistGraphQLService extends GeoAbstractGraphQLService<GeoPacklistEntete, BigDecimal> {

    private final EntityManager entityManager;
    private final GeoPacklistOrdreRepository packlistOrdreRepository;

    public GeoPacklistGraphQLService(GeoPacklistEnteteRepository repository,
            GeoPacklistOrdreRepository packlistOrdreRepository, EntityManager entityManager) {
        super(repository, GeoPacklistEntete.class);
        this.entityManager = entityManager;
        this.packlistOrdreRepository = packlistOrdreRepository;
    }

    @GraphQLMutation
    @Transactional
    public GeoPacklistEntete savePacklistEntete(GeoPacklistEntete packlistEntete,
            @GraphQLEnvironment ResolutionEnvironment env) {
        GeoPacklistEntete e = this.saveEntity(packlistEntete, env);

        // saving ordres
        List<GeoPacklistOrdre> l = e.getOrdres().stream()
                .map(o -> {
                    o.setId(e.getId());
                    return o;
                }).collect(Collectors.toList());
        this.packlistOrdreRepository.saveAll(l);

        return this.repository.getOne(e.getId());
    }

}
