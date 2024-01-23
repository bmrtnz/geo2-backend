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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
