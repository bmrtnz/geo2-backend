package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_codif_devalexp")
@Entity
public class GeoCodifDevalexp {

    @Id
    @Column(name = "ref_devalexp")
    private String id;

    @Column(name = "desc_devalexp")
    private String description;

    @Column(name = "num_tri")
    private Integer numTri;

}
