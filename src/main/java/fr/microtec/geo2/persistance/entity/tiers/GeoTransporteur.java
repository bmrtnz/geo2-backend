package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateModifiedPrewrittedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_transp")
@DynamicInsert
@DynamicUpdate
public class GeoTransporteur extends ValidateModifiedPrewrittedEntity implements Serializable {

	@Id
	@Column(name = "trp_code")
	private String id;

	@NotNull
	@Column(name = "tyt_code", nullable = false)
	private Character typeTiers = 'T';

	@NotNull
	@Column(name = "raisoc", nullable = false)
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

	@Column(name = "echnbj")
	private String nbJourEcheance;

	@Column(name = "echle")
	private String echeanceLe;

	@Column(name = "tvaid")
	private String tvaCee;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvr_code", nullable = false)
	private GeoRegimeTva regimeTva;

	@Column(name = "compte_compta")
	private String compteComptable;

	@Column(name = "lf_ean")
	private String lieuFonctionEan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_ref_assoc")
	private GeoClient clientRaisonSocial;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "con_tiers", referencedColumnName = "trp_code")
	@JoinColumn(name = "con_tyt", referencedColumnName = "tyt_code")
	private List<GeoContact> contacts;

}
