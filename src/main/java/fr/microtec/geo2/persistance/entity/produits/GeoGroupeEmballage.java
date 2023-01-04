package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_grpemb")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoGroupeEmballage extends ValidateAndModifiedEntity {

    public GeoGroupeEmballage(String especeID, String description, String id) {
        this.id = id;
        this.description = description;
        this.espece = new GeoEspece();
        this.espece.setId(especeID);
    }

    @Id
    @Column(name = "gem_code")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Column(name = "gem_desc")
    private String description;

}
