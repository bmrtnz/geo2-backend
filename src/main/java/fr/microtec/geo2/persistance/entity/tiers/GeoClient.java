package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoTypeVente;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_client")
@Entity
public class GeoClient extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "cli_ref")
	@GeneratedValue(generator = "GeoClientGenerator")
	@GenericGenerator(
			name = "GeoClientGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "seq_cli_num"),
					@Parameter(name = "mask", value = "FM099999")
			}
	)
	private String id;

	@Column(name = "cli_code")
	private String code;

	@Column(name = "raisoc")
	private String raisonSocial;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "con_tiers")
	private List<GeoContact> contacts;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soc_code")
	private GeoSociete societe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sco_code")
	private GeoSecteur secteur;

	//region Primary Address
	@Column(name = "ads1")
	private String adresse1;

	@Column(name = "ads2")
	private String adresse2;

	@Column(name = "ads3")
	private String adresse3;

	@Column(name = "zip")
	private String codePostal;

	@Column(name = "ville")
	private String ville;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_code")
	private GeoPays pays;
	//endregion

	//region Billing address
	@Column(name = "raisoc_fact")
	private String facturationRaisonSocial;

	@Column(name = "ads1_fact")
	private String facturationAdresse1;

	@Column(name = "ads2_fact")
	private String facturationAdresse2;

	@Column(name = "ads3_fact")
	private String facturationAdresse3;

	@Column(name = "zip_fact")
	private String facturationCodePostal;

	@Column(name = "ville_fact")
	private String facturationVille;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_code_fact")
	private GeoPays facturationPays;
	//endregion

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvr_code")
	private GeoRegimeTva regimeTva;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inc_code")
	private GeoIncoterm incoterm;

	@Column(name = "echnbj")
	private String nbJourEcheance;

	@Column(name = "echle")
	private String echeanceLe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mpm_code")
	private GeoMoyenPaiement moyenPaiement;

	@Column(name = "tvaid")
	private String tvaCee;

	@Column(name = "ctl_ref_cli")
	private String controlReferenceClient;

	@Column(name = "comment_haut_facture")
	private String commentaireHautFacture;

	@Column(name = "comment_bas_facture")
	private String commentaireBasFacture;

	@Column(name = "instructions_seccom")
	private String instructionCommercial;

	@Column(name = "siret")
	private String siret;

	@Column(name = "navoir_edi")
	private Boolean blocageAvoirEdi;

	@Column(name = "ind_comm_debloq")
	private Boolean debloquerEnvoieJour;

	@Column(name = "ifco")
	private String ifco;

	@Column(name = "instructions_logistique")
	private String instructionLogistique;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bpm_code")
	private GeoBasePaiement basePaiement;

	@Column(name = "compte_compta")
	private String compteComptable;

	@Column(name = "lf_ean")
	private String lieuFonctionEan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lan_code")
	private GeoPays langue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dev_code")
	private GeoDevise devise;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_code_com")
	private GeoPersonne commercial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_code_ass")
	private GeoPersonne assistante;

	@Column(name = "enc_references")
	private String referenceCoface;

	@Column(name = "enc_assure")
	private Integer agrement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crt_code")
	private GeoCourtier courtier;

	@Column(name = "crt_bta_code")
	private String courtageModeCalcul;

	@Column(name = "crt_pu")
	private Float courtageValeur;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tcl_code")
	private GeoTypeClient typeClient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gcl_code")
	private GeoGroupeClient groupeClient;

	@Column(name = "soumis_ctifl")
	private Boolean soumisCtifl;

	@Column(name = "frais_plateforme")
	private Float fraisPlateforme;

	@Column(name = "frais_pu")
	private Float fraisMarketing;

	/*
	// TODO : It's a sub entity (Table geo_bastar)
	@Column(name = "frais_unite")
	private String marketCoast;
	*/

	@Column(name = "rem_hf_tx")
	private Float tauxRemiseHorsFacture;

	@Column(name = "rem_sf_tx")
	private Float tauxRemiseParFacture;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvt_code")
	private GeoTypeVente typeVente;

	@Column(name = "enc_depasse")
	private Integer enCoursTemporaire;

	@Column(name = "enc_bw")
	private Integer enCoursBlueWhale;

	@Column(name = "flclodet_autom")
	private Boolean clotureAutomatique;

	@Column(name = "ind_frais_ramas")
	private Boolean fraisRamasse;

	@Column(name = "ind_exclu_frais_pu")
	private Boolean fraisExcluArticlePasOrigineFrance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_ref_palox")
	private GeoClient paloxRaisonSocial;

}
