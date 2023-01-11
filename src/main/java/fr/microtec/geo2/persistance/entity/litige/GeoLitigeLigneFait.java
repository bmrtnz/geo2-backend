package fr.microtec.geo2.persistance.entity.litige;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class GeoLitigeLigneFait {

    @Id
    @Column(name = "rownum")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lil_ref")
    private GeoLitigeLigne ligne;

    @Column(name = "tot_nb_col")
    private Integer totalNombreColis;

    @Column(name = "tot_pds_net")
    private Integer totalPoidsNet;

    @Column(name = "tot_nb_pal")
    private Integer totalNombrePalette;

}
