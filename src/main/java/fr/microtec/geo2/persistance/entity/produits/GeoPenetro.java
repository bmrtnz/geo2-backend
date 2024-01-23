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
@Table(name = "geo_penetro")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoPenetro extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "pen_code")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Column(name = "pen_desc")
    private String description;

    @Column(name = "pen_libvte")
    private String descriptionClient;

    @Column(name = "pen_min")
    private Float minimum;

    @Column(name = "pen_moy")
    private Float moyenne;

    @Column(name = "pen_max")
    private Float maximum;

}
