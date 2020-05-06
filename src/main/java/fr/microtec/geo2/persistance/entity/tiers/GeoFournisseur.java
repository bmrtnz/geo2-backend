package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_fourni")
@Entity
@DynamicInsert
@DynamicUpdate
public class GeoFournisseur extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "fou_code")
	private String id;

	@Column(name = "raisoc")
	private String raisonSocial;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lan_code")
	private GeoPays langue;

	@Column(name = "pos_latitude")
	private String latitude;

	@Column(name = "pos_longitude")
	private String longitude;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dev_code")
	private GeoDevise devise;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mpm_code")
	private GeoMoyenPaiement moyenPaiement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bpm_code")
	private GeoBasePaiement basePaiement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvr_code")
	private GeoRegimeTva regimeTva;

	@Column(name = "echnbj")
	private String nbJourEcheance;

	@Column(name = "echle")
	private String echeanceLe;

	@Column(name = "tvaid")
	private String tvaCee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bac_code")
	private GeoBureauAchat bureauAchat;

	@Column(name = "marge_kilo_previ")
	private Float margeObjectifEuroKilo;

	@Column(name = "marge_pcent_previ")
	private Float margeObjectifPourcentCa;

	@Column(name = "stock_actif")
	private Boolean stockActif;

	@Column(name = "stock_preca")
	private Boolean stockPreca;

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
	private String declarantCHEP;

	@Column(name = "ident1")
	private String formeJuridique;

	@Column(name = "ident2")
	private String siretAPE;

	@Column(name = "ident3")
	private String tvaCeeLibelle;

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

}
