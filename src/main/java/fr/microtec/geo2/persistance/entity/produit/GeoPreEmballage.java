package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_preemb")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoPreEmballage extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "pmb_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "pmb_desc")
	private String description;

	@Column(name = "pmb_libvte")
	private String descriptionClient;

	@Column(name = "pmb_dim")
	private String dimension;

	@Column(name = "pmb_tare")
	private Integer tare;

	@Column(name = "pmb_cout_mp")
	private Float coutMatierePremiere;

	@Column(name = "embadif_pmb_art")
	private String codeEmbadif;

	@Column(name = "qte_uc")
	private Float quantiteUc;

	@Column(name = "bta_uc")
	private String uniteUc;

}
