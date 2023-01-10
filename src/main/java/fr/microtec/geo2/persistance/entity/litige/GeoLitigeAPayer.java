package fr.microtec.geo2.persistance.entity.litige;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class GeoLitigeAPayer {

    @Id
    @Column(name = "rownum")
    private String id;

    @Column(name = "type")
    private String type;

    @Column(name = "code")
    private String codeFournisseur;

    @Column(name = "raisoc")
    private String raisonSociale;

    @Column(name = "num_tri")
    private Integer numeroTri;

}
