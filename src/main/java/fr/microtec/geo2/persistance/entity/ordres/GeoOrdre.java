package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.entity.common.GeoTypeVente;
import fr.microtec.geo2.persistance.entity.logistique.GeoPort;
import fr.microtec.geo2.persistance.entity.tiers.GeoBasePaiement;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoConditionVente;
import fr.microtec.geo2.persistance.entity.tiers.GeoCourtier;
import fr.microtec.geo2.persistance.entity.tiers.GeoDevise;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoIncoterm;
import fr.microtec.geo2.persistance.entity.tiers.GeoMoyenPaiement;
import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import fr.microtec.geo2.persistance.entity.tiers.GeoPersonne;
import fr.microtec.geo2.persistance.entity.tiers.GeoRegimeTva;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransitaire;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypeCamion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordre")
@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GeoOrdre extends ValidateAndModifiedEntity implements Duplicable<GeoOrdre> {

	// constructor to fetch statut
	public GeoOrdre(
			Boolean flagPublication,
			// Collection<GeoTracabiliteDetailPalette> tracabiliteDetailPalettes,
			// Collection<GeoOrdreLigne> lignes,
			Boolean expedieAuComplet,
			Boolean bonAFacturer,
			Boolean facture,
			Boolean flagAnnule) {
		this.setFlagPublication(flagPublication);
		// this.setTracabiliteDetailPalettes((List<GeoTracabiliteDetailPalette>)tracabiliteDetailPalettes);
		// this.setLignes((List<GeoOrdreLigne>)lignes);
		this.setExpedieAuComplet(expedieAuComplet);
		this.setBonAFacturer(bonAFacturer);
		this.setFacture(facture);
		this.setFlagAnnule(flagAnnule);
	}

	@Id
	@Column(name = "ord_ref")
	@GeneratedValue(generator = "GeoOrdreGenerator")
	@GenericGenerator(name = "GeoOrdreGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
			@Parameter(name = "sequenceName", value = "seq_ord_num"),
			@Parameter(name = "mask", value = "FM0999999")
	})
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soc_code")
	private GeoSociete societe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sco_code")
	private GeoSecteur secteurCommercial;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trp_code", nullable = false)
	private GeoTransporteur transporteur;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cli_ref", nullable = false)
	private GeoClient client;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ordre")
	private List<GeoOrdreLigne> lignes;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ordre")
	private List<GeoOrdreLogistique> logistiques;

	@LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ordre")
	private List<GeoTracabiliteDetailPalette> tracabiliteDetailPalettes;

	@NotNull
	@Column(name = "nordre", nullable = false, unique = true)
	private String numero;

	@Column(name = "nordre_pere")
	private String numeroPere;

	@Column(name = "ref_cli")
	private String referenceClient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codecom")
	private GeoPersonne assistante;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codeass")
	private GeoPersonne commercial;

	@Column(name = "cen_code")
	private String codeAlphaEntrepot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cen_ref")
	private GeoEntrepot entrepot;

	@Column(name = "depdatp")
	private LocalDate dateDepartPrevue;

	@Column(name = "livdatp")
	private LocalDate dateLivraisonPrevue;

	@Column(name = "vente_commission")
	private Boolean venteACommission = false;

	@Column(name = "flexp")
	private Boolean expedie = false;

	@Column(name = "flliv")
	private Boolean livre = false;

	@Column(name = "flbaf")
	private Boolean bonAFacturer = false;

	@Column(name = "flfac")
	private Boolean facture = false;

	@Column(name = "flbagqp")
	private Boolean bonAGenererDansQualifelPlus = false;

	@Column(name = "flgenqp")
	private Boolean genereDansQualifelPlus = false;

	@Column(name = "fbagudc")
	private Boolean bonAGenererUDC = false;

	@Column(name = "flgenudc")
	private Boolean genereUDC = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_edi_ordre")
	private GeoEDIOrdre ordreEDI;

	@Column(name = "invoic")
	private Boolean factureEDIFACT = false;

	@Column(name = "invoic_demat")
	private Boolean factureEDI;

	@Column(name = "instructions_logistique")
	private String instructionsLogistiques;

	@Column(name = "fac_num")
	private String numeroFacture;

	@Column(name = "cli_code")
	private String codeClient;

	@Column(name = "version_ordre")
	private String version;

	@Column(name = "version_ordre_date")
	private LocalDate versionDate;

	@Column(name = "version_detail")
	private String versionDetail;

	@Column(name = "version_detail_date")
	private LocalDate versionDetailDate;

	@Column(name = "facture_avoir")
	private GeoFactureAvoir factureAvoir;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "ordreOrigine")
	private GeoLitige litige;

	@Column(name = "code_chargement")
	private String codeChargement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cam_code")
	private GeoCampagne campagne;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trp_bta_code")
	private GeoBaseTarif baseTarifTransport;

	@Column(name = "trp_pu")
	private Float prixUnitaireTarifTransport = 0f;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trs_code")
	private GeoTransitaire transitaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trs_bta_code")
	private GeoBaseTarif baseTarifTransit;

	@Column(name = "trs_pu")
	private Float prixUnitaireTarifTransit = 0f;

	@Column(name = "trs_ville")
	private String villeDeTransit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crt_code")
	private GeoCourtier courtier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crt_bta_code")
	private GeoBaseTarif baseTarifCourtage;

	@Column(name = "crt_pu")
	private Float prixUnitaireTarifCourtage = 0f;

	@Column(name = "remsf_tx")
	private Float tauxRemiseFacture = 0f;

	@Column(name = "remhf_tx")
	private Float tauxRemiseHorsFacture = 0f;

	@Column(name = "dev_tx")
	private Double tauxDevise = 0d;

	@Column(name = "frais_pu")
	private Double fraisPrixUnitaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "frais_unite")
	private GeoBaseTarif fraisUnite;

	@Column(name = "frais_plateforme")
	private Float fraisPlateforme;

	@Column(name = "totpal")
	private Float totalPalette = 0f;

	@Column(name = "totcol")
	private Float totalColis = 0f;

	@Column(name = "totpdsnet")
	private Float totalPoidsNet = 0f;

	@Column(name = "totpdsbrut")
	private Float totalPoidsBrut = 0f;

	@Column(name = "totvte")
	private Float totalVente = 0f;

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

	@Column(name = "tot_cde_nb_pal")
	private Float totalNombrePalettesCommandees;

	@Column(name = "tot_exp_nb_pal")
	private Float totalNombrePalettesExpediees;

	@Column(name = "pal_nb_sol")
	private Float nombrePalettesAuSol;

	@Column(name = "pal_nb_PB100X120")
	private Float nombrePalettes100x120;

	@Column(name = "pal_nb_PB80X120")
	private Float nombrePalettes80x120;

	@Column(name = "pal_nb_PB60X80")
	private Float nombrePalettes60x80;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inc_code")
	private GeoIncoterm incoterm;

	@Column(name = "inc_lieu")
	private String incotermLieu;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref_pere")
	private GeoOrdre ordrePere;

	@Column(name = "typ_ordre", nullable = false)
	private GeoOrdreType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_code")
	private GeoPays pays;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dev_code")
	private GeoDevise devise;

	@Column(name = "ref_document")
	private String referenceDocument;

	@Column(name = "ref_logistique")
	private String referenceLogistique;

	@Column(name = "credat")
	private LocalDate dateCreation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvt_code")
	private GeoTypeVente typeVente = GeoTypeVente.getDefault();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvr_code", nullable = false)
	private GeoRegimeTva regimeTva;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mpm_code", nullable = false)
	private GeoMoyenPaiement moyenPaiement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bpm_code")
	private GeoBasePaiement basePaiement;

	@Column(name = "ent_echle")
	private String echeanceLe;

	@Column(name = "ent_echnbj")
	private String echeanceNombreDeJours;

	@Column(name = "ent_factcom")
	private String commentairesFacture;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cov_code")
	private GeoConditionVente conditionVente;

	@Column(name = "trp_prix_visible")
	private Boolean prixTransportVisible = false;

	@Column(name = "trs_prix_visible")
	private Boolean prixTransitVisible = false;

	@Column(name = "crt_prix_visible")
	private Boolean prixCourtageVisible = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ttr_code")
	private GeoTypeCamion typeTransport;

	@Column(name = "lib_dlv")
	private String libelleDLV;

	@Column(name = "frais_desc")
	private String commentairesFrais;

	@Column(name = "datfac")
	private LocalDate dateFacture;

	@Column(name = "flag_public")
	private Boolean flagPublication = false;

	@NotNull
	@Column(name = "flannul", nullable = false)
	private Boolean flagAnnule = false;

	@Column(name = "rem_sf_tx_mdd")
	private Float remiseSurFactureMDDTaux;

	@Column(name = "comm_interne")
	private String commentaireUsageInterne;

	@Column(name = "comment_tva")
	private String commentaireTVA;

	@Column(name = "etd_date")
	private LocalDate ETDDate;

	@Column(name = "eta_date")
	private LocalDate ETADate;

	@Column(name = "etd_location")
	private String ETDLocation;

	@Column(name = "eta_location")
	private String ETALocation;

	@Column(name = "trp_dev_code")
	private String transporteurDEVCode = "EUR";

	@Column(name = "trp_dev_pu")
	private Double transporteurDEVPrixUnitaire;

	@Column(name = "trp_dev_taux")
	private Float transporteurDEVTaux = 1f;

	@Column(name = "file_cmr")
	private String fileCMR;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_eta")
	private GeoPort PortTypeA;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_etd")
	private GeoPort PortTypeD;

	@Column(name = "list_nordre_comp")
	private String listeOrdresComplementaires;

	@Column(name = "list_nordre_regul")
	private String listeOrdresRegularisations;

	@Column(name = "ind_pres_spec")
	private String presentationSUP;

	@Column(name = "trp_bac_code")
	private String bassinTransporteur;

	@Column(name = "ind_exclu_frais_pu")
	private Boolean exclusionFraisPU = false;

	@Formula("(SELECT CASE WHEN COUNT(OL.orx_ref) = 0 THEN 'O' ELSE 'N' END FROM geo_ordlog OL, GEO_ORDRE O WHERE OL.FLAG_EXPED_FOURNNI = 'N' AND O.ORD_REF = OL.ORD_REF AND O.ORD_REF = ord_ref)")
	private Boolean expedieAuComplet;

	@Transient
	private Float pourcentageMargeBrut;

	@Transient
	private GeoOrdreStatut statut;

	public GeoOrdre duplicate() {
		GeoOrdre clone = new GeoOrdre();
		clone.societe = this.societe;
		clone.secteurCommercial = this.secteurCommercial;
		clone.transporteur = this.transporteur;
		clone.client = this.client;
		clone.referenceClient = this.referenceClient;
		clone.assistante = this.assistante;
		clone.commercial = this.commercial;

		return clone;
	}

	/**
	 * Permet de connaitre le nombre de commentaire associés a cette ordre via un
	 * count.
	 * 
	 * @see GeoOrdre.getCommentairesOrdreCount()
	 */
	@LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ordre")
	private Set<GeoCommentaireOrdre> commentairesOrdre;

	public Integer getCommentairesOrdreCount() {
		return this.getCommentairesOrdre().size();
	}

	/**
	 * Permet de connaitre le nombre de CSLignes associées a cette ordre via un
	 * count.
	 * 
	 * @see GeoOrdre.getCqLitigeCount()
	 */
	@LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ordre")
	private Set<GeoCQLigne> cqLignes;

	public Integer getCqLignesCount() {
		return this.getCqLignes().size();
	}

	public Boolean getHasLitige() {
		return this.getLitige() != null;
	}

}
