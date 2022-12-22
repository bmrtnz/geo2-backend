package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "geo_packlist_ordre")
@Entity
@IdClass(GeoPacklistOrdreKey.class)
public class GeoPacklistOrdre implements Serializable {

    @Id
    @Column(name = "ref_packlist", updatable = false)
    private BigDecimal id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_packlist", insertable = false, updatable = false)
    private GeoPacklistEntete entete;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref", updatable = false)
    private GeoOrdre ordre;

}
