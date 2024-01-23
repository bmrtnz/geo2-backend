package fr.microtec.geo2.persistance.entity.produits;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_grpvar")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoGroupeVariete extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "grv_code")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Column(name = "grv_desc")
    private String description;

    @Column(name = "previ_destockage")
    private Boolean suiviPrevisionDestockage;

    @OneToMany()
    @JoinColumn(name = "esp_code")
    @JoinColumn(name = "grv_code")
    private List<GeoVariete> varietes;

}
