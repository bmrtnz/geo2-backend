package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateCreatedAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "avi_art_emballage")
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
	@JoinColumns({
			@JoinColumn(name = "esp_code"),
			@JoinColumn(name = "col_code")
	})
	private GeoEmballage emballage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "esp_code"),
			@JoinColumn(name = "cos_code")
	})
	private GeoConditionSpecial conditionSpecial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "esp_code"),
			@JoinColumn(name = "alv_code")
	})
	private GeoAlveole alveole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "esp_code"),
			@JoinColumn(name = "maq_code")
	})
	private GeoMarque marque;

}
