package fr.microtec.geo2.persistance.entity.produits;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoGenre;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "geo_espece")
public class GeoEspece extends ValidateAndModifiedEntity {

    public GeoEspece(String id) {
        this.id = id;
    }

    @Id
    @Column(name = "esp_code")
    private String id;

    @Column(name = "esp_desc")
    private String description;

    @Column(name = "esp_deb_mm")
    private Integer moisDebutVente;

    @Column(name = "esp_fin_mm")
    private Integer moisFinVente;

    @Column(name = "pu_max")
    private Float prixUnitaireMax;

    @Column(name = "pu_min")
    private Float prixUnitaireMin;

    @Column(name = "valid_visu")
    private Boolean stationVisible;

    @Column(name = "var_douane")
    private String codeDouanier;

    @Column(name = "pub_web")
    private Boolean publicationWeb;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gen_code")
    private GeoGenre genre;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "espece")
    private List<GeoStockArticleAge> stocksAge;

}
