package fr.microtec.geo2.service.graphql.ordres;

import java.math.BigDecimal;

import javax.persistence.EntityManager;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.persistance.entity.ordres.GeoPacklistEntete;
import fr.microtec.geo2.persistance.entity.ordres.GeoPacklistOrdre;
import fr.microtec.geo2.persistance.repository.ordres.GeoPacklistRepository;
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

    public GeoPacklistGraphQLService(GeoPacklistRepository repository, EntityManager entityManager) {
        super(repository, GeoPacklistEntete.class);
        this.entityManager = entityManager;
    }

    @GraphQLMutation
    @Transactional
    public GeoPacklistEntete savePacklistEntete(GeoPacklistEntete packlistEntete,
            @GraphQLEnvironment ResolutionEnvironment env) {
        GeoPacklistEntete e = this.saveEntity(packlistEntete, env);

        e.getOrdres().parallelStream().forEach(o -> {
            GeoPacklistOrdre m = new GeoPacklistOrdre();
            m.setId(e.getId());
            m.setOrdre(o.getOrdre());
            this.entityManager.persist(m);
        });

        return this.repository.getOne(e.getId());
    }

}
