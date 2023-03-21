package fr.microtec.geo2.persistance.entity.litige;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import lombok.Data;

@Data
@Entity
public class GeoLitigeLigneForfait {

    @Id
    @Column(name = "lil_ref")
    private String id;

    @Column(name = "orl_lit")
    private String numeroGroupementLitige;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @Column(name = "cli_pu")
    private Float clientPrixUnitaire;

    @Column(name = "cli_qte")
    private Double clientQuantite;

    @Column(name = "cli_bta_code")
    private String clientUniteFactureCode;

    @Column(name = "cli_dev_code")
    private String clientUniteDeviseCode;

    @Column(name = "res_dev_pu")
    private Double devisePrixUnitaire;

    @Column(name = "res_pu")
    private Float responsablePrixUnitaire;

    @Column(name = "res_bta_code")
    private String responsableUniteFactureCode;

    @Column(name = "res_qte")
    private Double responsableQuantite;

    @Column(name = "res_dev_taux")
    private Double deviseTaux;

    @Column(name = "res_dev_code")
    private String deviseCode;

    @Transient
    private Float forfaitClient;

    @Transient
    private Float forfaitResponsable;

}
