package fr.microtec.geo2.persistance.entity.tiers;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class GeoSupervisionPalox {

    @Id
    @Column(name = "rownum")
    Integer id;

    @Column(name = "cli_code")
    String codeClient;

    @Column(name = "cen_code")
    String codeEntrepot;

    @Column(name = "e_raisoc")
    String raisonSocialeEntrepot;

    @Column(name = "zip")
    String codePostalEntrepot;

    @Column(name = "ville")
    String villeEntrepot;

    @Column(name = "pay_code")
    String codePaysEntrepot;

    @Column(name = "entree")
    Integer entree;

    @Column(name = "sortie")
    Integer sortie;

    @Column(name = "col_code")
    String codeEmballage;

    @Column(name = "fou_code")
    String codeFournisseur;

    @Column(name = "raisoc")
    String raisonSocialeFournisseur;

    @Column(name = "date_inv")
    LocalDateTime dateInventaire;

    @Column(name = "qte_inv")
    Integer quantiteInventaire;

    @Column(name = "esp_code")
    String codeEspece;

    @Column(name = "cen_ref")
    String referenceEntrepot;

    @Column(name = "sum_qtt")
    Integer sommeQuantiteInventaire;

}
