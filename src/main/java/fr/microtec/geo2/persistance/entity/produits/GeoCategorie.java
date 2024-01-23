package fr.microtec.geo2.persistance.entity.produits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_catego")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoCategorie extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "cat_code")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Column(name = "cat_desc")
    private String description;

    @Column(name = "cat_libvte")
    private String descriptionClient;

    @Column(name = "ccw_code")
    private String cahierDesChargesBlueWhale;

}
