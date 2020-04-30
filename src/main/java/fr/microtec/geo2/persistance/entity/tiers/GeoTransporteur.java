package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_transp")
public class GeoTransporteur extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "trp_code")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dev_code")
	private GeoDevise devise;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mpm_code")
	private GeoMoyenPaiement moyenPaiement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bpm_code")
	private GeoBasePaiement basePaiement;

	@Column(name = "echnbj")
	private String nbJourEcheance;

	@Column(name = "echle")
	private String echeanceLe;

	@Column(name = "tvaid")
	private String tvaCee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvr_code")
	private GeoRegimeTva regimeTva;

	@Column(name = "compte_compta")
	private String compteComptable;

	@Column(name = "lf_ean")
	private String lieuFonctionEan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_ref_assoc")
	private GeoClient clientRaisonSocial;

}
