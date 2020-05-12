package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
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
@Table(name = "geo_groupa")
@Entity
@DynamicUpdate
@DynamicInsert
public class GeoLieuPassageAQuai extends ValidateAndModifiedEntity implements Serializable {

	@Id
	@Column(name = "grp_code")
	private String id;

	@Column(name = "tyt_code")
	private Character typeTiers = 'G';

	@NotNull
	@Column(name = "raisoc", nullable = false)
	private String raisonSocial;

	//region Primary Address
	@Column(name = "ads1")
	private String adresse1;

	@Column(name = "ads2")
	private String adresse2;

	@Column(name = "ads3")
	private String adresse3;

	@Column(name = "zip")
	private String codePostal;

	@NotNull
	@Column(name = "ville", nullable = false)
	private String ville;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_code")
	private GeoPays pays;
	//endregion

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lan_code")
	private GeoPays langue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dev_code")
	private GeoDevise devise;

	@Column(name = "lf_ean")
	private String lieuFonctionEan;

	@Column(name = "tvaid")
	private String tvaCee;

	@Column(name = "echnbj")
	private String nbJourEcheance;

	@Column(name = "echle")
	private String echeanceLe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvr_code")
	private GeoRegimeTva regimeTva;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mpm_code")
	private GeoMoyenPaiement moyenPaiement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bpm_code")
	private GeoBasePaiement basePaiement;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "con_tiers", referencedColumnName = "grp_code")
	@JoinColumn(name = "con_tyt", referencedColumnName = "tyt_code")
	private List<GeoContact> contacts;

}
