package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.ValidateModifiedPrewrittedEntity;
import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueFournisseur;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_fourni")
@Entity
@DynamicInsert
@DynamicUpdate
public class GeoFournisseur extends ValidateModifiedPrewrittedEntity implements Serializable {

	@Id
	@Column(name = "fou_code")
	private String id;

	@NotNull
	@Column(name = "tyt_code", nullable = false)
	private Character typeTiers = 'F';

	@NotNull
	@Column(name = "raisoc")
	private String raisonSocial;

	@Column(name = "ads1")
	private String adresse1;

	@Column(name = "ads2")
	private String adresse2;

	@Column(name = "ads3")
	private String adresse3;

	@NotNull
	@Column(name = "zip", nullable = false)
	private String codePostal;

	@NotNull
	@Column(name = "ville", nullable = false)
	private String ville;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_code", nullable = false)
	private GeoPays pays;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lan_code", nullable = false)
	private GeoPays langue;

	@Column(name = "pos_latitude")
	private String latitude;

	@Column(name = "pos_longitude")
	private String longitude;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dev_code", nullable = false)
	private GeoDevise devise;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mpm_code")
	private GeoMoyenPaiement moyenPaiement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bpm_code")
	private GeoBasePaiement basePaiement;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvr_code", nullable = false)
	private GeoRegimeTva regimeTva;

	@Column(name = "echnbj")
	private String nbJourEcheance;

	@Column(name = "echle")
	private String echeanceLe;

	@Column(name = "tvaid")
	private String tvaCee;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bac_code", nullable = false)
	private GeoBureauAchat bureauAchat;

	@Column(name = "marge_kilo_previ")
	private Float margeObjectifEuroKilo;

	@Column(name = "marge_pcent_previ")
	private Float margeObjectifPourcentCa;

	@Column(name = "stock_actif")
	private Boolean stockActif;

	@Column(name = "stock_preca")
	private Boolean stockPrecalibre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_fournisseur")
	private GeoTypeFournisseur type;

	@Column(name = "soc_liste")
	private String listeSocietes;

	@Column(name = "trace_id")
	private String idTracabilite;

	@Column(name = "agrement_bw")
	private String agrementBW; // Nom de la personne qui a demandé la création du fournisseur

	@Column(name = "logistique_id")
	private String codeStation;

	@Column(name = "compte_compta")
	private String compteComptable;

	@Column(name = "lf_ean")
	private String lieuFonctionEan;

	@Column(name = "declarant_chep")
	private Boolean declarantCHEP;

	@Column(name = "declarant_chep_bacs")
	private Boolean declarantBacsCHEP;

	@Column(name = "ident1")
	private String formeJuridique;

	@Column(name = "ident2")
	private String siretAPE;

	@Column(name = "ident3")
	private String tvaId;

	@Column(name = "ident4")
	private String rcs;

	@Column(name = "previ_destockage")
	private Boolean suiviDestockage;

	@Column(name = "ind_exp")
	private GeoNatureStation natureStation;

	@Column(name = "ifco")
	private String referenceIfco;

	@Column(name = "date_debut_ifco")
	private LocalDate dateDebutIfco;

	@Column(name = "ind_consigne_palox_sa")
	private Boolean consignePaloxSa;

	@Column(name = "ind_consigne_palox_udc")
	private Boolean consignePaloxUdc;

	@Column(name = "list_exp")
	private String listeExpediteurs;

	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "auto_facturation")
	private Boolean autoFacturation;

	@Column(name = "dat_cg_achsig")
	private LocalDate dateConditionGeneraleAchatSignee;

	@Column(name = "ind_modif_detail")
	private Boolean indicateurModificationDetail;

	@Deprecated // Do not use
	@Column(name = "entrepot")
	private Boolean entrepot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fou_code_destockage")
	private GeoFournisseur fournisseurDeRattachement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cov_code")
	private GeoConditionVente conditionVente;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "con_tiers", referencedColumnName = "fou_code")
	@JoinColumn(name = "con_tyt", referencedColumnName = "tyt_code")
	private List<GeoContact> contacts;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grp_code")
	private GeoLieuPassageAQuai lieuPassageAQuai;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fgg_code")
	private GeoGroupeFournisseur groupeFournisseur;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fournisseur")
	private List<GeoHistoriqueFournisseur> historique;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "geo_fourni_cert",
			joinColumns = { @JoinColumn(name = "fou_code") },
			inverseJoinColumns = { @JoinColumn(name = "k_certif") }
	)
	private Set<GeoCertification> certifications;

}
