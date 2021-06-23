package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_colis")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoEmballage extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "col_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "col_ref", nullable = false, unique = true)
	private String reference;

	@Column(name = "col_desc", nullable = false)
	private String description;

	@Column(name = "col_liblong", nullable = false)
	private String descriptionTechnique;

	@Column(name = "col_dim")
	private String dimension;

	@Column(name = "col_prepese")
	private Boolean prepese;

	@Column(name = "col_normali")
	private String normalisation;

	@Column(name = "col_comment")
	private String commentaire;

	@Column(name = "col_hmaxpal")
	private Float hauteurMaximumPalette;

	@Column(name = "col_pump")
	private Float prixUnitaireMatierePremiere;

	@Column(name = "col_pumo")
	private Float prixUnitaireMainOeuvre;

	@Column(name = "col_pdnet")
	private Float poidsNetColis;

	@Column(name = "col_tare")
	private Float tare;

	@Column(name = "col_xb")
	private Float xb;

	@Column(name = "col_xh")
	private Float xh;

	@Column(name = "col_yb")
	private Float yb;

	@Column(name = "col_yh")
	private Float yh;

	@Column(name = "col_zb")
	private Float zb;

	@Column(name = "col_zh")
	private Float zh;

	@Column(name = "embadif_col_art")
	private String codeEmbadif;

	@Column(name = "emb_consigne")
	private Boolean consigne;

	@Column(name = "pmb_per_com")
	private Integer nbConso; // TODO bon nom ? pas sur

	@Column(name = "gest_ref")
	private String referenceGestionnaire;

	@Column(name = "gest_code")
	private String codeGestionnaire;

	@Column(name = "ean_colis")
	private String codeEan;

	@Column(name = "ean_uc")
	private String codeEanUc;

	@Column(name = "ids_code")
	private String idSymbolique;

	@Column(name = "suivi_pallox")
	private Boolean suiviPalox;

	@Column(name = "pu_achat")
	private Float prixUnitaireAchat;

	@Column(name = "pu_vente")
	private Float prixUnitaireVente;

	@Column(name = "esp_emballee")
	private String especeEmballee;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "esp_code")
	@JoinColumn(name = "gem_code")
	private GeoGroupeEmballage groupe;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "esp_code")
	@JoinColumn(name = "pmb_code")
	private GeoPreEmballage preEmballage;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "esp_code")
	@JoinColumn(name = "maq_code")
	private GeoMarque marque;

}
