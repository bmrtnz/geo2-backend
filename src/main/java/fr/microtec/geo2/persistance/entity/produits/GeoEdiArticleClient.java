package fr.microtec.geo2.persistance.entity.produits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import lombok.Data;

@Data
@Entity
@Table(name = "geo_edi_art_cli")
public class GeoEdiArticleClient extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "k_edi_art_cli")
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref", nullable = false)
    private GeoArticle article;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref", nullable = false)
    private GeoClient client;

    @NotNull
    @Column(name = "gtin_colis_client")
    private String gtinColisClient;

    @NotNull
    @Column(name = "priorite")
    private Integer priorite;

    @Column(name = "typ_desc")
    private String description;

    @Column(name = "last_ord")
    private String dernierOrdre;

}
