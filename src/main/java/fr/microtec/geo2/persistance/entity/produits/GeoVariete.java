package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import java.util.List;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_variet")
@AllArgsConstructor
@NoArgsConstructor
public class GeoVariete extends ValidateAndModifiedEntity {

    public GeoVariete(String description, String id) {
        this.id = id;
        this.description = description;
    }

    @Id
    @Column(name = "var_code")
    private String id;

    @Column(name = "var_desc")
    private String description;

    @Column(name = "var_ctifl")
    private Boolean soumisCtifl;

    @Column(name = "ind_modif_detail")
    private Boolean modificationDetail;

    @Column(name = "var_ristourne")
    private Boolean soumisRistourne;

    @Column(name = "var_douane")
    private String codeProduitDouane;

    @Column(name = "var_plu")
    private String plu;

    @Column(name = "frais_pu")
    private Float fraisPu;

    @Column(name = "pub_web")
    private Boolean publicationWeb;

    @Column(name = "stock_preca")
    private Boolean stockPrecalibre;

    @Column(name = "perequ")
    private Boolean prerequation;

    @Column(name = "ach_pu_mini")
    private Float prixUnitaireMin;

    @Column(name = "pu_max")
    private Float prixUnitaireMax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula(value = "esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "grv_code"))
    private GeoGroupeVariete groupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frais_unite")
    private GeoBaseTarif baseTarif;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variete")
    private List<GeoStockArticleAge> stocksAge;

}
