package fr.microtec.geo2.persistance.entity.stock;

import fr.microtec.geo2.persistance.entity.produits.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "geo_stock_art_age")
@EqualsAndHashCode()
@IdClass(GeoStockArticleAgeKey.class)
@Entity
@Immutable
public class GeoStockArticleAge implements Serializable {

    @Id
    @Column(name = "age")
    private String age;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "var_code")
    private GeoVariete variete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "caf_code"))
    private GeoCalibreFournisseur calibreFournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "cam_code"))
    private GeoCalibreMarquage calibreMarquage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "cat_code"))
    private GeoCategorie categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "col_code"))
    private GeoEmballage colis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "ori_code"))
    private GeoOrigine origine;

    @Formula("qte_ini_1 - qte_res_1 - qte_opt_1")
    private Integer j;

    @Formula("qte_ini_2 - qte_res_2 - qte_opt_2")
    private Integer j1a8;

    @Formula("qte_ini_3 - qte_res_3 - qte_opt_3")
    private Integer j9a20;

    @Formula("qte_ini_4 - qte_res_4 - qte_opt_4")
    private Integer j21aX;

    @Formula("qte_ini_1 + qte_ini_2 + qte_ini_3 + qte_ini_4 - qte_res_1- qte_res_2 - qte_res_3 - qte_res_4")
    private Integer total;

    @Formula("(SELECT geo_stock_consolid.commentaire FROM geo_stock_consolid WHERE geo_stock_consolid.art_ref = art_ref)")
    private String commentaire;

    @Formula("(SELECT geo_stock_hebdo.qte_hebdo FROM geo_stock_hebdo WHERE geo_stock_hebdo.art_ref = art_ref)")
    private Integer quantiteHebdomadaire;

    @Formula("(SELECT (qte_ini_1 + qte_ini_2 + qte_ini_3 + qte_ini_4 - qte_res_1- qte_res_2 - qte_res_3 - qte_res_4 - geo_stock_hebdo.qte_hebdo * 3) FROM geo_stock_hebdo WHERE geo_stock_hebdo.art_ref = art_ref)")
    private Integer prevision3j;

    @Formula("(SELECT (qte_ini_1 + qte_ini_2 + qte_ini_3 + qte_ini_4 - qte_res_1- qte_res_2 - qte_res_3 - qte_res_4 - geo_stock_hebdo.qte_hebdo * 6) FROM geo_stock_hebdo WHERE geo_stock_hebdo.art_ref = art_ref)")
    private Integer prevision7j;

}
