package fr.microtec.geo2.persistance.entity.produits;

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
@Table(name = "avi_art_emballage")
@DynamicInsert
@DynamicUpdate
public class GeoArticleEmballage extends ValidateCreatedAndModifiedEntity {

	@Id
	@Column(name = "ref_emballage")
	@GeneratedValue(generator = "GeoArticleEmballageGenerator")
	@GenericGenerator(
			name = "GeoArticleEmballageGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "F_SEQ_AVI_ART_EMBALLAGE"),
					@Parameter(name = "isSequence", value = "false")
			}
	)
	private String id;

	@Column(name = "col_pdnet")
	private Float poidsNetColis;

	@Column(name = "uc_pdnet_garanti")
	private Float poidsNetGaranti;

	@Column(name = "col_prepese")
	private Boolean prepese;

	@Column(name = "u_par_colis")
	private Integer uniteParColis;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "col_code"))
	private GeoEmballage emballage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "cos_code"))
	private GeoConditionSpecial conditionSpecial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "alv_code"))
	private GeoAlveole alveole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "maq_code"))
	private GeoMarque marque;

}
