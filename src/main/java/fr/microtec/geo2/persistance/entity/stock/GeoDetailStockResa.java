package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import lombok.Data;

@Data
@Entity
public class GeoDetailStockResa {

    @Id
    @Column(name = "rownum")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sto_ref")
    private GeoStock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stm_ref")
    private GeoStockMouvement mouvement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

}
