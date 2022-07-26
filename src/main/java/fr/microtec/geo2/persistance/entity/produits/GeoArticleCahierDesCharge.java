package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.ValidateCreatedAndModifiedEntity;
import fr.microtec.geo2.persistance.repository.produits.matcher.GeoArticlePartMatch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "avi_art_cdc")
public class GeoArticleCahierDesCharge extends ValidateCreatedAndModifiedEntity implements Duplicable<GeoArticleCahierDesCharge> {

	@Id
	@Column(name = "ref_cdc")
	@GeneratedValue(generator = "GeoArticleCahierDesChargeGenerator")
	@GenericGenerator(
			name = "GeoArticleCahierDesChargeGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "F_SEQ_AVI_ART_CDC"),
					@Parameter(name = "isSequence", value = "false")
			}
	)
	private String id;

    @GeoArticlePartMatch
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

    @GeoArticlePartMatch
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula(value = "esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "cat_code"))
	private GeoCategorie categorie;

    @GeoArticlePartMatch
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "cir_code"))
	private GeoCirage cirage;

    @GeoArticlePartMatch
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "pen_code"))
	private GeoPenetro penetro;

    @GeoArticlePartMatch
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "ran_code"))
	private GeoRangement rangement;

    @GeoArticlePartMatch
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "suc_code"))
	private GeoSucre sucre;

    @GeoArticlePartMatch
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "clr_code"))
	private GeoColoration coloration;

    @GeoArticlePartMatch
	@Column(name = "ins_station")
	private String instructionStation;

    @GeoArticlePartMatch
    @Column(name = "ins_seccom")
    private String instructionSecteurCommercial;

	public GeoArticleCahierDesCharge duplicate() {
		GeoArticleCahierDesCharge clone = new GeoArticleCahierDesCharge();
		clone.espece = this.espece;
		clone.categorie = this.categorie;
		clone.coloration = this.coloration;
		clone.sucre = this.sucre;
		clone.penetro = this.penetro;
		clone.cirage = this.cirage;
		clone.rangement = this.rangement;
		clone.instructionStation = this.instructionStation;
		clone.instructionSecteurCommercial = this.instructionSecteurCommercial;

		return clone;
	}

}
