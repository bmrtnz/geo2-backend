package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoPersonne;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordre")
@DynamicInsert
@DynamicUpdate
@Entity
public class GeoOrdre extends ValidateAndModifiedEntity implements Duplicable<GeoOrdre> {

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

	@NotNull
	@Column(name = "nordre", nullable = false, unique = true)
	private String numero;

	@Column(name = "ref_cli")
	private String referenceClient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codecom")
	private GeoPersonne assistante;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codeass")
	private GeoPersonne commercial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cen_ref")
	private GeoEntrepot entrepot;

	@Column(name = "depdatp")
	private LocalDate dateDepartPrevue;

	@Column(name = "livdatp")
	private LocalDate dateLivraisonPrevue;

	@Column(name = "vente_commission")
	private Boolean venteACommission;

	@Column(name = "flexp")
	private Boolean expedie;

	@Column(name = "flliv")
	private Boolean livre;

	@Column(name = "flbaf")
	private Boolean bonAFacturer;

	@Column(name = "flfac")
	private Boolean facture;

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
	private Float prixUnitaireTarifTransport;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trs_bta_code")
	private GeoBaseTarif baseTarifTransit;

	@Column(name = "trs_pu")
	private Float prixUnitaireTarifTransit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crt_bta_code")
	private GeoBaseTarif baseTarifCourtage;
	
	@Column(name = "crt_pu")
	private Float prixUnitaireTarifCourtage;
	
	@Column(name = "remsf_tx")
	private Float tauxRemiseFacture;
	
	@Column(name = "remhf_tx")
	private Float tauxRemiseHorsFacture;
	
	@Column(name = "dev_tx")
	private Double tauxDevise;
	
	@Column(name = "frais_pu")
	private Double fraisPrixUnitaire;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "frais_unite")
	private GeoBaseTarif fraisUnite;

	@Column(name = "frais_plateforme")
	private Float fraisPlateforme;

	@Column(name = "totpal")
	private Float totalPalette;

	@Column(name = "totcol")
	private Float totalColis;

	@Column(name = "totpdsnet")
	private Float totalPoidsNet;

	@Column(name = "totpdsbrut")
	private Float totalPoidsBrut;

	@Column(name = "totvte")
	private Float totalVente;

	@Column(name = "totrem")
	private Float totalRemise;

	@Column(name = "totres")
	private Float totalRestitue;

	@Column(name = "totfrd")
	private Double totalFraisMarketing;

	@Column(name = "totach")
	private Double totalAchat;

	@Column(name = "totmob")
	private Float totalObjectifMarge;

	@Column(name = "tottrp")
	private Float totalTransport;

	@Column(name = "tottrs")
	private Float totalTransit;

	@Column(name = "totcrt")
	private Float totalCourtage;

	@Column(name = "totfad")
	private Float totalFraisAdditionnels;

	@Column(name = "totfrais_plateforme")
	private Float totalFraisPlateforme;

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
	private Float nombrePalettes60X80;

	@Transient
	private Float pourcentageMargeBrutTest;

	@PostLoad
	@PostUpdate
	public void postUpdate(){
			this.pourcentageMargeBrut = this.totalVente > 0 ?
				(float)(this.totalVente - this.totalRemise + this.totalRestitue - this.totalFraisMarketing - this.totalAchat - this.totalTransport - this.totalCourtage - this.totalFraisAdditionnels) / this.totalVente : 0f;
	}

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

}