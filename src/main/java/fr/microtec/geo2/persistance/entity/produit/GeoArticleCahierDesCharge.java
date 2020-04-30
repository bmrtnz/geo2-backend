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
@Table(name = "avi_art_cdc")
public class GeoArticleCahierDesCharge extends ValidateCreatedAndModifiedEntity {

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "cat_code"),
			@JoinColumn(name = "esp_code")
	})
	private GeoCategorie categorie;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "cir_code"),
			@JoinColumn(name = "esp_code")
	})
	private GeoCirage cirage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "pen_code"),
			@JoinColumn(name = "esp_code")
	})
	private GeoPenetro penetro;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "ran_code"),
			@JoinColumn(name = "esp_code")
	})
	private GeoRangement rangement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "suc_code"),
			@JoinColumn(name = "esp_code")
	})
	private GeoSucre sucre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "clr_code"),
			@JoinColumn(name = "esp_code")
	})
	private GeoColoration coloration;

	@Column(name = "ins_seccom")
	private String instructionCommercial;

	@Column(name = "ins_station")
	private String instructionStation;

}
