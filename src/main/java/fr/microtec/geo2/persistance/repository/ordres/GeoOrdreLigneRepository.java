package fr.microtec.geo2.persistance.repository.ordres;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigneCumul;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigneSummed;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoOrdreLigneRepository extends GeoRepository<GeoOrdreLigne, String> {

    @Query("SELECT DISTINCT ol.article FROM #{#entityName} ol")
    List<GeoArticle> findDistinctArticle();

    @Query("SELECT ol FROM #{#entityName} ol, GeoOrdreLogistique olo WHERE ol.ordre = :ordre AND ol.ordre = olo.ordre AND ol.fournisseur = olo.fournisseur")
    List<GeoOrdreLigne> findByOrdreAndMatchingFournisseur(GeoOrdre ordre);

    @Query("SELECT new fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigneCumul(ol.nombrePalettesCommandees,ol.nombrePalettesExpediees,ol.nombreColisCommandes,ol.nombreColisExpedies,ol.poidsNetExpedie,ol.poidsNetCommande,ol.poidsBrutExpedie,ol.poidsBrutCommande,ol.totalVenteBrut,ol.totalRemise,ol.totalRestitue,ol.totalFraisMarketing,ol.totalAchat,ol.totalObjectifMarge,ol.indicateurPalette,ol.logistique) FROM GeoOrdreLigne ol, GeoOrdreLogistique olo WHERE ol.ordre = :ordre AND ol.ordre = olo.ordre AND ol.fournisseur = olo.fournisseur")
    List<GeoOrdreLigneCumul> findByOrdre(GeoOrdre ordre);

    @Query("SELECT new fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigneSummed(SUM(ol.nombrePalettesExpediees) as nombrePalettesExpediees, SUM(ol.nombrePalettesCommandees) as nombrePalettesCommandees, ol.fournisseur as fournisseur, ol.logistique as logistique) FROM #{#entityName} ol WHERE ol.ordre = :ordre GROUP BY ol.ordre, ol.fournisseur")
    List<GeoOrdreLigneSummed> getSummedPalettesByOrdreGroupByFournisseur(GeoOrdre ordre);

    Optional<GeoOrdreLigne> getOneByOrdreIdAndArticleId(String ordreId, String articleRef);

    Long countByOrdre(GeoOrdre ordre);

    Long countByOrdreAndGratuitIsTrue(GeoOrdre ordre);
}
