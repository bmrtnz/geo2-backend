package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateCreatedAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoModeCulture;
import fr.microtec.geo2.persistance.entity.common.GeoTypeVente;
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
@Table(name = "avi_art_mat_prem")
public class GeoArticleMatierePremiere extends ValidateCreatedAndModifiedEntity {

	@Id
	@Column(name = "ref_mat_prem")
	@GeneratedValue(generator = "GeoArticleMatierePremiereGenerator")
	@GenericGenerator(
			name = "GeoArticleMatierePremiereGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "F_SEQ_AVI_ART_MAT_PREM"),
					@Parameter(name = "isSequence", value = "false")
			}
	)
	private String id;

	@Column(name = "plu_code")
	private String codePlu;

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
	@JoinColumnOrFormula(column = @JoinColumn(name = "cun_code"))
	private GeoCalibreUnifie calibreUnifie;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "ori_code"))
	private GeoOrigine origine;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mode_culture")
	private GeoModeCulture modeCulture;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvt_code")
	private GeoTypeVente typeVente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "typ_ref")
	private GeoType type;

}
