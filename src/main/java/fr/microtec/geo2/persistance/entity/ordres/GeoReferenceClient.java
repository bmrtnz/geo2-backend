package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_cliref")
@Entity
public class GeoReferenceClient extends ValidateAndModifiedEntity {

    @Id
    @GeneratedValue(generator = "GeoReferenceClientGenerator")
    @GenericGenerator(name = "GeoReferenceClientGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_crf_num"),
            @Parameter(name = "mask", value = "FM099999")
    })
    @Column(name = "crf_ref")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref")
    private GeoClient client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @Column(name = "pro_desc")
    private String commentaire;

    @Column(name = "pde_ref")
    private String referenceProdet;

    @Column(name = "remsf_tx")
    private Float tauxRemiseSurFacture;

    @Column(name = "remsf_tx_suppl")
    private Boolean tauxSurFactureSupplementaire;

    @Column(name = "remsf_tx_valide")
    private Boolean tauxSurFactureValide;

    @Column(name = "pro_ref")
    private String referenceProduit;

}
