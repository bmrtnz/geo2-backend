package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_grpemb")
@IdClass(GeoProduitWithEspeceId.class)
@AllArgsConstructor
@NoArgsConstructor
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
