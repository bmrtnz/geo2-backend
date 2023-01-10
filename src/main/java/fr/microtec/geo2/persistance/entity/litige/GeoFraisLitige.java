package fr.microtec.geo2.persistance.entity.litige;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.ordres.GeoFrais;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordfra_litige")
@Entity
public class GeoFraisLitige extends ModifiedEntity {

    @Id
    @Column(name = "orfl_ref")
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
