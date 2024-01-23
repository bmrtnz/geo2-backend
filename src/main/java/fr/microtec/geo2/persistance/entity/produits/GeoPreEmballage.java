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
@Table(name = "geo_preemb")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoPreEmballage extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "pmb_code")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @Column(name = "pmb_desc")
    private String description;

    @Column(name = "pmb_libvte")
    private String descriptionClient;

    @Column(name = "pmb_dim")
    private String dimension;

    @Column(name = "pmb_tare")
    private Integer tare;

    @Column(name = "pmb_cout_mp")
    private Float coutMatierePremiere;

    @Column(name = "embadif_pmb_art")
    private String codeEmbadif;

    @Column(name = "qte_uc")
    private Float quantiteUc;

    @Column(name = "bta_uc")
    private String uniteUc;

}
