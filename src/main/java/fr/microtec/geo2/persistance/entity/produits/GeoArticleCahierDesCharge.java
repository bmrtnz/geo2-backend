package fr.microtec.geo2.persistance.entity.produits;

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
	@JoinColumn(name = "cat_code", insertable = false, updatable = false)
	@JoinColumn(name = "esp_code", insertable = false, updatable = false)
	private GeoCategorie categorie;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cir_code", insertable = false, updatable = false)
	@JoinColumn(name = "esp_code", insertable = false, updatable = false)
	private GeoCirage cirage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pen_code", insertable = false, updatable = false)
	@JoinColumn(name = "esp_code", insertable = false, updatable = false)
	private GeoPenetro penetro;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ran_code", insertable = false, updatable = false)
	@JoinColumn(name = "esp_code", insertable = false, updatable = false)
	private GeoRangement rangement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "suc_code", insertable = false, updatable = false)
	@JoinColumn(name = "esp_code", insertable = false, updatable = false)
	private GeoSucre sucre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clr_code", insertable = false, updatable = false)
	@JoinColumn(name = "esp_code", insertable = false, updatable = false)
	private GeoColoration coloration;

	@Column(name = "ins_seccom")
	private String instructionCommercial;

	@Column(name = "ins_station")
	private String instructionStation;

}
