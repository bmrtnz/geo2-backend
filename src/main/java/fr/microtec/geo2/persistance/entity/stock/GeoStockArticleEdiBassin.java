package fr.microtec.geo2.persistance.entity.stock;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.entity.ordres.GeoEdiLigne;
import fr.microtec.geo2.persistance.entity.ordres.GeoEdiOrdre;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import fr.microtec.geo2.persistance.entity.tiers.GeoBureauAchat;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_stock_art_edi_bassin")
public class GeoStockArticleEdiBassin {

    @Id
    @Column(name = "k_stock_art_edi_bassin")
    @GeneratedValue(generator = "GeoStockArticleEdiBassinGenerator")
    @GenericGenerator(name = "GeoStockArticleEdiBassinGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "F_SEQ_K_STOCK_ART_EDI_BASSIN"),
            @Parameter(name = "isSequence", value = "false")
    })
    private BigDecimal id;

    @NotNull
    @Column(name = "edi_ord", insertable = false, updatable = false)
    private Integer numeroOrdreEDI;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edi_ord", nullable = false)
    private GeoEdiOrdre ordreEdi;

    @NotNull
    @Column(name = "edi_lig", insertable = false, updatable = false)
    private Integer numeroLigneEDI;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edi_lig", nullable = false)
    private GeoEdiLigne ligneEdi;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref", nullable = false)
    private GeoClient client;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cam_code", nullable = false)
    private GeoCampagne campagne;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref", nullable = false)
    private GeoArticle article;

    @NotNull
    @Column(name = "gtin")
    private String gtin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
    private GeoFournisseur fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prop_code", referencedColumnName = "fou_code")
    private GeoFournisseur proprietaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_code")
    private GeoBureauAchat bureauAchat;

    @Column(name = "qte_res")
    private Integer quantiteRestante;

    @Column(name = "qte_valide")
    private Integer quantiteValidee;

    @Column(name = "flag_hors_bassin")
    private String flagHorsBassin;

    @Column(name = "age")
    private Character age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ach_bta_code")
    private GeoBaseTarif achatUnite;

    @Column(name = "ach_dev_pu")
    private Double achatDevisePrixUnitaire;

    @Column(name = "ach_pu")
    private Double achatPrixUnitaire;

    @Column(name = "ach_dev_taux")
    private Double achatDeviseTaux;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vte_bta_code")
    private GeoBaseTarif venteUnite;

    @Column(name = "vte_pu")
    private Float ventePrixUnitaire;

    @Column(name = "vte_pu_net")
    private Double ventePrixUnitaireNet;

    @Column(name = "choix")
    private Boolean choix;

}
