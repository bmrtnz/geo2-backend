package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_code_promo")
@Entity
public class GeoCodePromo extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "promo_code")
    private String id;

    @Column(name = "lib_promo", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code", nullable = false)
    private GeoEspece espece;

}
