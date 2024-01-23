package fr.microtec.geo2.persistance.entity.litige;

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

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.ordres.GeoFrais;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_ordfra_litige")
@Entity
public class GeoFraisLitige extends ModifiedEntity {

    @Id
    @Column(name = "orfl_ref")
    @GeneratedValue(generator = "GeoFraisLitigeGenerator")
    @GenericGenerator(name = "GeoFraisLitigeGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_orfl_num"),
            @Parameter(name = "mask", value = "FM099999")
    })
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lit_ref", nullable = false)
    private GeoLitige litige;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fra_code")
    private GeoFrais frais;

    @Column(name = "montant")
    private Float montant;

    @Column(name = "fra_desc")
    private String description;

    @Column(name = "trp_code_plus")
    private String transporteurCodePlus;

}
