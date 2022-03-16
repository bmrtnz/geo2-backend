package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.ValidateCreatedAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "avi_art_normalisation")
public class GeoArticleNormalisation extends ValidateCreatedAndModifiedEntity implements Duplicable<GeoArticleNormalisation> {

	@Id
	@Column(name = "ref_normalisation")
	@GeneratedValue(generator = "GeoArticleNormalisationGenerator")
	@GenericGenerator(
			name = "GeoArticleNormalisationGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "F_SEQ_AVI_ART_NORMALISATION"),
					@Parameter(name = "isSequence", value = "false")
			}
	)
	private String id;

	@Column(name = "gtin_colis")
	private String gtinColis;

	@Column(name = "gtin_uc")
	private String gtinUc;

	@Column(name = "com_client")
	private String descriptionCalibreClient;

	@Column(name = "mdd")
	private Boolean produitMdd;

	@Column(name = "pde_cliart")
	private String articleClient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "cam_code"))
	private GeoCalibreMarquage calibreMarquage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "etf_code"))
	private GeoStickeur stickeur;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "etc_code"))
	private GeoEtiquetteColis etiquetteColis;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "etp_code"))
	private GeoEtiquetteUc etiquetteUc;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "etv_code"))
	private GeoEtiquetteEvenementielle etiquetteEvenementielle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "ids_code"))
	private GeoIdentificationSymbolique identificationSymbolique;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "maq_code"))
	private GeoMarque marque;

	public GeoArticleNormalisation duplicate() {
		GeoArticleNormalisation clone = new GeoArticleNormalisation();
		clone.espece = this.espece;
		clone.calibreMarquage = this.calibreMarquage;
		clone.stickeur = this.stickeur;
		clone.etiquetteColis = this.etiquetteColis;
		clone.etiquetteUc = this.etiquetteUc;
		clone.etiquetteEvenementielle = this.etiquetteEvenementielle;
		clone.identificationSymbolique = this.identificationSymbolique;
		clone.marque = this.marque;
		clone.gtinColis = this.gtinColis;
		clone.gtinUc = this.gtinUc;
		clone.descriptionCalibreClient = this.descriptionCalibreClient;
		clone.produitMdd = this.produitMdd;
		clone.articleClient = this.articleClient;
		
		return clone;
	}

}
