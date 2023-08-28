package fr.microtec.geo2.persistance.entity.stock;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoCalibreFournisseur;
import fr.microtec.geo2.persistance.entity.produits.GeoCalibreMarquage;
import fr.microtec.geo2.persistance.entity.produits.GeoCategorie;
import fr.microtec.geo2.persistance.entity.produits.GeoEmballage;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.entity.produits.GeoOrigine;
import fr.microtec.geo2.persistance.entity.produits.GeoVariete;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
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

        // https://hibernate.atlassian.net/browse/HHH-9185
        // @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
        // @JoinColumnOrFormula(column = @JoinColumn(name = "caf_code"))
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
                        @JoinColumn(name = "esp_code", insertable = false, updatable = false),
                        @JoinColumn(name = "caf_code", insertable = false, updatable = false)
        })
        private GeoCalibreFournisseur calibreFournisseur;

        // https://hibernate.atlassian.net/browse/HHH-9185
        // @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
        // @JoinColumnOrFormula(column = @JoinColumn(name = "cam_code"))
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
                        @JoinColumn(name = "esp_code", insertable = false, updatable = false),
                        @JoinColumn(name = "cam_code", insertable = false, updatable = false)
        })
        private GeoCalibreMarquage calibreMarquage;

        // https://hibernate.atlassian.net/browse/HHH-9185
        // @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
        // @JoinColumnOrFormula(column = @JoinColumn(name = "cat_code"))
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
                        @JoinColumn(name = "esp_code", insertable = false, updatable = false),
                        @JoinColumn(name = "cat_code", insertable = false, updatable = false)
        })
        private GeoCategorie categorie;

        // https://hibernate.atlassian.net/browse/HHH-9185
        // @JoinColumnOrFormula(column = @JoinColumn(name = "esp_code"))
        // @JoinColumnOrFormula(column = @JoinColumn(name = "col_code"))
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
                        @JoinColumn(name = "esp_code", insertable = false, updatable = false),
                        @JoinColumn(name = "col_code", insertable = false, updatable = false)
        })
        private GeoEmballage colis;

        // https://hibernate.atlassian.net/browse/HHH-9185
        // @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
        // @JoinColumnOrFormula(column = @JoinColumn(name = "ori_code"))
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
                        @JoinColumn(name = "esp_code", insertable = false, updatable = false),
                        @JoinColumn(name = "ori_code", insertable = false, updatable = false)
        })
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
