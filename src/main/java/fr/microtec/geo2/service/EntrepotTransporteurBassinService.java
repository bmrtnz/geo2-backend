package fr.microtec.geo2.service;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepotTransporteurBassin;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotTransporteurBassinRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service
public class EntrepotTransporteurBassinService
        extends GeoAbstractGraphQLService<GeoEntrepotTransporteurBassin, BigDecimal> {

    @PersistenceContext
    private EntityManager entityManager;

    public EntrepotTransporteurBassinService(
            GeoEntrepotTransporteurBassinRepository repository) {
        super(repository, GeoEntrepotTransporteurBassin.class);
    }

    /**
     * Implementation des procedures `w_affecte_trp_dw_trp_on_clicked` &
     * `w_affecte_trp_on_enregistre`
     */
    public GeoEntrepotTransporteurBassin affecte(GeoEntrepotTransporteurBassin etb) {

        // delete currently assigned bassin
        ((GeoEntrepotTransporteurBassinRepository) this.repository)
                .deleteByEntrepotAndBureauAchat(etb.getEntrepot(), etb.getBureauAchat());

        return etb;
    }

}
