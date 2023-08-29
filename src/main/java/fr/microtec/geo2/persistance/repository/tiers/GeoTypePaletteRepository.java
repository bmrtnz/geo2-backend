package fr.microtec.geo2.persistance.repository.tiers;

import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import fr.microtec.geo2.persistance.repository.GeoRepository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoTypePaletteRepository extends GeoRepository<GeoTypePalette, String> {
    @Query(name = "TypePalette.fetchNombreColisParPalette", nativeQuery = true)
    BigDecimal fetchNombreColisParPalette(
            @Param("ls_pal_code") String typePalette,
            @Param("ls_art_ref") String article,
            @Param("ls_sco_code") String secteur);
}
