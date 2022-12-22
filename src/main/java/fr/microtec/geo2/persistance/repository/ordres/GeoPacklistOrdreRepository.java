package fr.microtec.geo2.persistance.repository.ordres;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoPacklistOrdre;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoPacklistOrdreRepository extends GeoRepository<GeoPacklistOrdre, BigDecimal> {
}
