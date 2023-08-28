package fr.microtec.geo2.persistance.entity.litige;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GeoOrdreLigneLitigePick {

    @Id
    @Column(name = "rownum")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orl_ref")
    private GeoOrdreLigne ordreLigne;

    @Column(name = "tot_nb_col")
    private Integer totalNombreColis;

}
