package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GeoPlanningDepart {

    @Id
    @Column(name = "rownum")
    Integer id;

    @Column(name = "sum_cde_nb_col")
    private Integer sommeColisCommandes;

    @Column(name = "sum_exp_nb_col")
    private Integer sommeColisExpedies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orx_ref")
    private GeoOrdreLogistique ordreLogistique;

}
