package fr.microtec.geo2.persistance.entity.historique;

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

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_histo_article")
@Entity
public class GeoHistoriqueArticle extends GeoBaseHistorique {

    @Id
    @Column(name = "histo_art_ref")
    @GeneratedValue(generator = "GeoHistoriqueArticleGenerator")
    @GenericGenerator(name = "GeoHistoriqueArticleGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_histo_art"),
            @Parameter(name = "mask", value = "FM099999")
    })
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref", nullable = false)
    private GeoArticle article;

}
