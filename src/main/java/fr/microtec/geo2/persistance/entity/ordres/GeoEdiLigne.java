package fr.microtec.geo2.persistance.entity.ordres;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "geo_edi_ligne")
@Entity
public class GeoEdiLigne {

    @Id
    @Column(name = "ref_edi_ligne")
    @GeneratedValue(generator = "GeoEdiLigneGenerator")
    @GenericGenerator(name = "GeoEdiLigneGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "SEQ_EDI_LIGNE"),
            @Parameter(name = "isSequence", value = "true")
    })
    private BigDecimal id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_edi_ordre")
    private GeoEdiOrdre ediOrdre;

    @Column(name = "num_ligne")
    private Integer numeroLigne;

    @Column(name = "ean_produit_client")
    private String eanProduitClient;

    @Column(name = "ean_prod_bw")
    private String eanProduitBlueWhale;

    @Column(name = "code_interne_prod_client")
    private String codeInterneProduitClient;

    @Column(name = "code_interne_prod_bw")
    private String codeInterneProduitBlueWhale;

    @Column(name = "par_combien")
    private Integer parCombien;

    @Column(name = "quantite")
    private Integer quantite;

    @Column(name = "unite_qtt")
    private String uniteQtt;

    @Column(name = "libelle_produit")
    private String libelleProduit;

    @Column(name = "type_colis")
    private String typeColis;

    @Column(name = "quantite_colis")
    private Integer quantiteColis;

    @Column(name = "ean_colis_client")
    private String eanColisClient;

    @Column(name = "ean_colis_bw")
    private String eanColisBlueWhale;

    @Column(name = "status")
    private Character status;

    @Column(name = "list_ref_art")
    private String listeReferenceArticle;

    @Column(name = "mask_modif")
    private String masqueModification;

    @Column(name = "prix_vente")
    private BigDecimal prixVente;

    @Column(name = "op_marketing")
    private Boolean operationMarketing;

    @Column(name = "alert_prix")
    private String alertePrix;

}
