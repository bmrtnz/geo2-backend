package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_stomvt")
@Entity
public class GeoStockMouvement extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "stm_ref")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sto_ref")
    private GeoStock stock;

    @Column(name = "mvt_qte")
    private Integer quantite;

}
