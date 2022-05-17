package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "geo_codif_devalex")
@Entity
public class GeoCodifDevalex {

    @Id
    @Column(name = "def_devalex")
    private String id;

    @Column(name = "desc_devalexp")
    private String description;

    @Column(name = "num_tri")
    private Integer numTri;

}
