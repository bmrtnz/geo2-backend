package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.entity.produits.GeoVariete;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_def_code_promo")
@IdClass(GeoDefCodePromoId.class)
@Entity
public class GeoDefCodePromo extends ValidateEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code")
    private GeoCodePromo codePromo;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "var_code")
    private GeoVariete variete;

    @Column(name = "num_tri")
    private Integer numeroTri;

}
