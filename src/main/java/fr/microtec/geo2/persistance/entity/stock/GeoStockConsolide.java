package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_stock_consolid")
@Entity
@DynamicUpdate
public class GeoStockConsolide extends ModifiedEntity {

    @Id
    @Column(name = "art_ref")
    private String id;

    @Column(name = "commentaire")
    private String commentaire;

}
