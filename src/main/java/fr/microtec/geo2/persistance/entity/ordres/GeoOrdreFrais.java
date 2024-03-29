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
import fr.microtec.geo2.persistance.entity.tiers.GeoDevise;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_ordfra")
@Entity
public class GeoOrdreFrais extends ValidateAndModifiedEntity {

    @Id
    @GeneratedValue(generator = "GeoOrdreFraisGenerator")
    @GenericGenerator(name = "GeoOrdreFraisGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_orf_num"),
            @Parameter(name = "mask", value = "FM099999")
    })
    @Column(name = "orf_ref")
    private String id;

    @Column(name = "fra_desc")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fra_code")
    private GeoFrais frais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dev_code")
    private GeoDevise devise;

    @Column(name = "montant")
    private Float montant;

    @Column(name = "dev_tx")
    private Float deviseTaux;

    @Column(name = "trp_code_plus")
    private String codePlus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @Column(name = "ach_qte")
    private Double achatQuantite;

    @Column(name = "ach_pu")
    private Double achatPrixUnitaire;

    @Column(name = "ach_dev_pu")
    private Double achatDevisePrixUnitaire;

    @Column(name = "montant_tot")
    private Double montantTotal;

}
