package fr.microtec.geo2.persistance.entity.produits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.document.GeoAsEtiquette;
import fr.microtec.geo2.persistance.entity.document.GeoDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_etifru")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoStickeur extends ValidateAndModifiedEntity implements GeoAsEtiquette {

    @Id
    @Column(name = "etf_code")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Column(name = "etf_desc")
    private String description;

    @Column(name = "etf_libvte")
    private String descriptionClient;

    @Transient
    private GeoDocument document;

    @Override
    public String getEtiquettePrefix() {
        return GeoAsEtiquette.ETIQUETTE_STICKER;
    }

    // TODO field : clf_code

}
