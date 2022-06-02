package fr.microtec.geo2.persistance.repository.tiers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoMouvementEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoMouvementFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoRecapitulatifEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoRecapitulatifFournisseur;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoEntrepotRepository extends GeoRepository<GeoEntrepot, String> {

    @Query(name = "Entrepot.allMouvementEntrepot", nativeQuery = true)
    List<GeoMouvementEntrepot> allMouvementEntrepot(
            @Param("arg_dat_max") LocalDateTime dateMaxMouvements,
            @Param("arg_soc_code") String codeSociete,
            @Param("arg_cen_ref") String codeEntrepot,
            @Param("arg_per_code_com") String codeCommercial,
            @Param("arg_fou_code") String codeFournisseur);

    @Query(name = "Entrepot.allMouvementFournisseur", nativeQuery = true)
    List<GeoMouvementFournisseur> allMouvementFournisseur(
            @Param("arg_dat_max") LocalDateTime dateMaxMouvements,
            @Param("arg_soc_code") String codeSociete,
            @Param("arg_cen_ref") String codeEntrepot,
            @Param("arg_per_code_com") String codeCommercial,
            @Param("arg_fou_code") String codeFournisseur);

    @Query(name = "Entrepot.allRecapitulatifEntrepot", nativeQuery = true)
    List<GeoRecapitulatifEntrepot> allRecapitulatifEntrepot(
            @Param("arg_dat_max") LocalDateTime dateMaxMouvements,
            @Param("arg_soc_code") String codeSociete,
            @Param("arg_cen_ref") String codeEntrepot,
            @Param("arg_per_code_com") String codeCommercial,
            @Param("arg_fou_code") String codeFournisseur);

    @Query(name = "Entrepot.allRecapitulatifFournisseur", nativeQuery = true)
    List<GeoRecapitulatifFournisseur> allRecapitulatifFournisseur(
            @Param("arg_dat_max") LocalDateTime dateMaxMouvements,
            @Param("arg_soc_code") String codeSociete,
            @Param("arg_cen_ref") String codeEntrepot,
            @Param("arg_per_code_com") String codeCommercial,
            @Param("arg_fou_code") String codeFournisseur);

    Optional<GeoEntrepot> getOneByCode(String code);
}
