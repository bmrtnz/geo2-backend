package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;

public class GeoCertificationTierListener {

    @PersistenceContext
    private EntityManager entityManager;
    
    @PrePersist
	public void prePersist(GeoCertificationTier cert) {
		// if(cert.getId() == null) {
        //     // GeoSequenceGenerator.generate(this.entityManager, params)
		// 	// this.entityManager;
		// 	// this.entityManager.persist(cert);
        // }
	}
}
