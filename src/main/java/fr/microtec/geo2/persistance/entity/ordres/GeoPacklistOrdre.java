package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "geo_packlist_ordre")
@Entity
public class GeoPacklistOrdre {

    @Id
    @Column(name = "ref_packlist")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_packlist", insertable = false, updatable = false)
    private GeoPacklistEntete entete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

}
