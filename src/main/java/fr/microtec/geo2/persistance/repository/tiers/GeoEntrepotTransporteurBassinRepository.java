package fr.microtec.geo2.persistance.repository.tiers;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.tiers.GeoBureauAchat;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepotTransporteurBassin;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoEntrepotTransporteurBassinRepository
        extends GeoRepository<GeoEntrepotTransporteurBassin, BigDecimal> {
    BigDecimal deleteByEntrepotAndBureauAchat(GeoEntrepot entrepot, GeoBureauAchat bureauAchat);
}
