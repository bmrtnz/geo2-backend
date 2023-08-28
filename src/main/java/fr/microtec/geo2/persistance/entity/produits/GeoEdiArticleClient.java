package fr.microtec.geo2.persistance.entity.produits;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_edi_art_cli")
public class GeoEdiArticleClient extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "k_edi_art_cli")
    @GeneratedValue(generator = "GeoEdiArticleClientGenerator")
    @GenericGenerator(name = "GeoEdiArticleClientGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "F_SEQ_EDI_ART_CLI"),
            @Parameter(name = "isSequence", value = "false")
    })
    private BigDecimal id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref", nullable = false)
    private GeoArticle article;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref", nullable = false)
    private GeoClient client;

    @Column(name = "art_ref_client")
    private String codeArticleClient;

    @Column(name = "gtin_colis_client")
    private String gtinColisClient;

    @NotNull
    @Column(name = "priorite")
    private Integer priorite;

    @Column(name = "last_ord")
    private String dernierOrdre;

}
