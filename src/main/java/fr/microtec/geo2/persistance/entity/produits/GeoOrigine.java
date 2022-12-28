package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_origine")
@IdClass(GeoProduitWithEspeceId.class)
@AllArgsConstructor
@NoArgsConstructor
public class GeoOrigine extends ValidateAndModifiedEntity {

    public GeoOrigine(String especeID, String description, String id) {
        this.id = id;
        this.description = description;
        this.espece = new GeoEspece();
        this.espece.setId(especeID);
    }

    @Id
    @Column(name = "ori_code")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Column(name = "ori_desc")
    private String description;

    @Column(name = "ori_libvte")
    private String descriptionClient;

    @Column(name = "cee")
    private Boolean origineCee;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "colis")
    private List<GeoStockArticleAge> stocksAge;

}
