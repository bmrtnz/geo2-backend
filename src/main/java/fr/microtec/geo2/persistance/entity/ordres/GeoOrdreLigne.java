package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.entity.produits.GeoTemperature;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import fr.microtec.geo2.persistance.entity.tiers.GeoBureauAchat;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoGroupage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordlig")
@Entity
public class GeoOrdreLigne extends ValidateAndModifiedEntity implements Serializable {

	@Id
	@Column(name = "orl_ref")
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "art_ref")
	private GeoArticle article;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
	private GeoOrdre ordre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref", referencedColumnName = "ord_ref", insertable = false, updatable = false)
	@JoinColumn(name = "fou_code", referencedColumnName = "fou_code", insertable = false, updatable = false)
	private GeoOrdreLogistique logistique;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
	private GeoFournisseur fournisseur;

	@Column(name = "exp_nb_pal")
	private Float nombrePalettesExpediees = 0f;

	@Column(name = "cde_nb_pal")
	private Float nombrePalettesCommandees = 0f;

	@Column(name = "exp_nb_col")
	private Float nombreColisExpedies = 0f;

	@Column(name = "cde_nb_col")
	private Float nombreColisCommandes = 0f;

	@Column(name = "exp_pds_net")
	private Float poidsNetExpedie = 0f;

	@Column(name = "cde_pds_net")
	private Float poidsNetCommande;

	@Column(name = "exp_pds_brut")
	private Double poidsBrutExpedie = 0d;

	@Column(name = "cde_pds_brut")
	private Float poidsBrutCommande;

	@Column(name = "vte_pu")
	private Float ventePrixUnitaire = 0f;

	@Column(name = "vte_qte")
	private Double venteQuantite = 0d;

	@Column(name = "ach_pu")
	private Double achatPrixUnitaire = 0d;

	@Column(name = "ach_dev_code")
	private String achatDevise;

	@Column(name = "ach_dev_taux")
	private Double achatDeviseTaux;

	@Column(name = "ach_dev_pu")
	private Double achatDevisePrixUnitaire;

	@Column(name = "ach_qte")
	private Double achatQuantite = 0d;

	@Column(name = "totvte")
	private Double totalVenteBrut = 0d;

	@Column(name = "totrem")
	private Float totalRemise = 0f;

	@Column(name = "totres")
	private Float totalRestitue = 0f;

	@Column(name = "totfrd")
	private Double totalFraisMarketing = 0d;

	@Column(name = "totach")
	private Double totalAchat = 0d;

	@Column(name = "totmob")
	private Float totalObjectifMarge = 0f;

	@Column(name = "tottrp")
	private Float totalTransport = 0f;

	@Column(name = "tottrs")
	private Float totalTransit = 0f;

	@Column(name = "totcrt")
	private Float totalCourtage = 0f;

	@Column(name = "totfad")
	private Float totalFraisAdditionnels = 0f;

	@Column(name = "totfrais_plateforme")
	private Float totalFraisPlateforme = 0f;

	@Column(name = "var_ristourne")
	private Boolean ristourne;

	@Column(name = "frais_pu")
	private Double fraisPrixUnitaire;

	@Column(name = "frais_desc")
	private String fraisCommentaires;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "frais_unite")
	private GeoBaseTarif fraisUnite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ach_bta_code")
	private GeoBaseTarif achatUnite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vte_bta_code")
	private GeoBaseTarif venteUnite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bac_code")
	private GeoBureauAchat bureauAchat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
	@JoinColumnOrFormula(column = @JoinColumn(name = "tem_code"))
	private GeoTemperature temperature;

	@Column(name = "demipal_ind")
	private Float indicateurPalette = 0f;

	@Column(name = "pal_nb_col")
	private Float nombreColisPalette = 0f;

	@Column(name = "pal_nb_palinter")
	private Float nombrePalettesIntermediaires = 0f;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "palinter_code")
	private GeoTypePalette paletteInter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pal_code")
	private GeoTypePalette typePalette;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grp_code")
	private GeoGroupage lieuGroupage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trp_code")
	private GeoTransporteur transporteurGroupage;

	@Column(name = "ind_gratuit")
	private Boolean gratuit = false;

	@Column(name = "orl_lig")
	private String numero;

	@Column(name = "pro_ref")
	private String referenceProduit;

	@Column(name = "pan_code")
	private String palettisation;

	@Column(name = "lib_dlv")
	private String libelleDLV;

	@Column(name = "obs_fourni")
	private String observationsFournisseur;

	@Column(name = "cq_ref")
	private String referenceControleQualite;

	@Column(name = "stm_ref")
	private String stockMouvement;

	@Column(name = "flexp")
	private Boolean expedie = false;

	@Column(name = "flliv")
	private Boolean livre = false;

	@Column(name = "flbaf")
	private Boolean bonAFacturer = false;

	@Column(name = "flfac")
	private Boolean facture = false;

	@Column(name = "flverfou")
	private Boolean verificationFournisseur = false;

	@Column(name = "indbloq_ach_dev_pu")
	private Boolean indicateurBlocagePrix;

	@Column(name = "remsf_tx")
	private Float tauxRemiseSurFacture = 0f;

	@Column(name = "remhf_tx")
	private Float tauxRemiseHorsFacture = 0f;

	@Column(name = "stock_nb_resa")
	private Float nombreReservationsSurStock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "propr_code", referencedColumnName = "fou_code")
	private GeoFournisseur proprietaireMarchandise;

	@Column(name = "promo_code")
	private String codePromo;

	@Column(name = "art_ref_kit")
	private String articleKit;

	@Column(name = "gtin_colis_kit")
	private String gtinColisKit;

	@NotNull
	@Column(name = "nb_colis_manquant", nullable = false)
	private Integer nombreColisManquant;

	@Column(name = "list_certifs")
	private String listeCertifications = "0";

	@Column(name = "cert_origine")
	private String origineCertification;

	@Column(name = "pde_ref")
	private String referenceProdet;

	@Column(name = "nb_cqphotos")
	private Double nombrePhotosCQ;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ordreLigne")
	private List<GeoTracabiliteLigne> tracabiliteLignes;

	@Transient
	private Float nombreColisPaletteByDimensions;

	@Transient
	private Double margeBrute;

	@Transient
	private Double pourcentageMargeBrute;

	@Transient
	private Double pourcentageMargeNette;

	@PostLoad
	public void postLoad() {

		try {
			this.margeBrute = (Double) (this.totalVenteBrut - this.totalRemise + this.totalRestitue
					- this.totalFraisMarketing - this.totalAchat - this.totalTransport - this.totalTransit
					- this.totalCourtage - this.totalFraisAdditionnels);
			this.pourcentageMargeBrute = this.totalVenteBrut != 0d ? this.margeBrute / this.totalVenteBrut : 0d;
			this.pourcentageMargeNette = this.totalVenteBrut != 0d
					? (this.margeBrute - this.totalObjectifMarge) / this.totalVenteBrut
					: 0d;
		} catch (Exception e) {
		}

		GeoTypePalette typePalette = this.getTypePalette();
		if (typePalette == null)
			return;
		Character dimensions = typePalette.getDimensions();
		if (dimensions == null)
			return;

		this.nombreColisPaletteByDimensions = 0f;

		if (this.getArticle() != null &&
				this.getArticle().getEmballage() != null &&
				this.getArticle().getEmballage().getEmballage() != null) {

			if (dimensions == '1') {
				Float xb = this.getArticle().getEmballage().getEmballage().getXb();
				Float xh = this.getArticle().getEmballage().getEmballage().getXh();
				if (xb != null && xh != null)
					this.nombreColisPaletteByDimensions = xb * xh;
			} else if (dimensions == '8') {
				Float yb = this.getArticle().getEmballage().getEmballage().getYb();
				Float yh = this.getArticle().getEmballage().getEmballage().getYh();
				if (yb != null && yh != null)
					this.nombreColisPaletteByDimensions = yb * yh;
			} else if (dimensions == '6') {
				Float zb = this.getArticle().getEmballage().getEmballage().getZb();
				Float zh = this.getArticle().getEmballage().getEmballage().getZh();
				if (zb != null && zh != null)
					this.nombreColisPaletteByDimensions = zb * zh;
			}
		}
	}

}
