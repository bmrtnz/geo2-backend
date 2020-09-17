package fr.microtec.geo2.persistance.entity.stock;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;
import org.springframework.data.annotation.LastModifiedDate;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoCalibreFournisseur;
import fr.microtec.geo2.persistance.entity.produits.GeoCalibreMarquage;
import fr.microtec.geo2.persistance.entity.produits.GeoCategorie;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.entity.produits.GeoOrigine;
import fr.microtec.geo2.persistance.entity.produits.GeoVariete;

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

  // @Formula("(" +
  // "(SELECT var_code FROM geo_variet WHERE geo_variet.var_code = var_code) || " + 
  // "' cal ' || " +
  // "COALESCE((SELECT geo_caluni.cun_code FROM geo_caluni WHERE geo_caluni.cun_code = caf_code AND geo_caluni.esp_code = esp_code),'-') || " + 
  // "' ' ||" +
  // "(SELECT cam_code FROM geo_calmar WHERE geo_calmar.cam_code = cam_code AND geo_calmar.esp_code = esp_code) || " + 
  // "' cat ' ||" +
  // "(SELECT cat_code FROM geo_catego WHERE geo_catego.cat_code = cat_code AND geo_catego.esp_code = esp_code) || " + 
  // "' emb ' ||" +
  // "(SELECT col_code FROM geo_colis WHERE geo_colis.col_code = col_code AND geo_colis.esp_code = esp_code) || " + 
  // "' ' ||" +
  // "(SELECT ori_code FROM geo_origine WHERE geo_origine.ori_code = ori_code AND geo_origine.esp_code = esp_code) || " + 
  // "' ' ||" +
  // "(SELECT geo_marque.maq_desc FROM geo_article INNER JOIN geo_marque ON geo_article.maq_code = geo_marque.maq_code AND geo_article.esp_code = geo_marque.esp_code WHERE geo_article.art_ref = art_ref )" +
  // ")")
  // private String description;

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
  private GeoColis colis;

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

  // @LastModifiedDate
	// @Formula("(SELECT geo_stock_consolid.mod_date FROM geo_stock_consolid WHERE geo_stock_consolid.art_ref = art_ref)")
	// private LocalDateTime dateModification;

  @Formula("(SELECT geo_stock_hebdo.qte_hebdo FROM geo_stock_hebdo WHERE geo_stock_hebdo.art_ref = art_ref)")
  private Integer quantiteHebdomadaire;

  @Formula("(SELECT (qte_ini_1 + qte_ini_2 + qte_ini_3 + qte_ini_4 - qte_res_1- qte_res_2 - qte_res_3 - qte_res_4 - geo_stock_hebdo.qte_hebdo * 3) FROM geo_stock_hebdo WHERE geo_stock_hebdo.art_ref = art_ref)")
  private Integer prevision3j;
  
  @Formula("(SELECT (qte_ini_1 + qte_ini_2 + qte_ini_3 + qte_ini_4 - qte_res_1- qte_res_2 - qte_res_3 - qte_res_4 - geo_stock_hebdo.qte_hebdo * 6) FROM geo_stock_hebdo WHERE geo_stock_hebdo.art_ref = art_ref)")
  private Integer prevision7j;

}