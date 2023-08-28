package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_histo_ordlog_declo")
@Entity
public class GeoHistoriqueLogistique extends ModifiedEntity {

    @Id
    @Column(name = "histo_orx_ref")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orx_ref", nullable = false)
    private GeoOrdreLogistique logistique;

    @Column(name = "flag_exped_fournni")
    private Boolean expedieStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_devalexp", nullable = false)
    private GeoCodifDevalexp devalexp;

}
