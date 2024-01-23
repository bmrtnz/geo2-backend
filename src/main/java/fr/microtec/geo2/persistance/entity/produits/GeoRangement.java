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
@Table(name = "geo_rangem")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoRangement extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "ran_code")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Column(name = "ran_desc")
    private String description;

    @Column(name = "ran_libvte")
    private String descriptionClient;

}
