package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_entrep")
@Entity
@DynamicInsert
@DynamicUpdate
public class GeoEntrepot extends ValidateAndModifiedEntity implements Serializable {

	@Id
	@Column(name = "cen_ref")
	@GeneratedValue(generator = "GeoEntrepotGenerator")
	@GenericGenerator(
			name = "GeoEntrepotGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "seq_cen_num"),
					@Parameter(name = "mask", value = "FM099999")
			}
	)
	private String id;

	@NotNull
	@Column(name = "cen_code")
	private String code;

	@NotNull
	@Column(name = "tyt_code", nullable = false)
	private Character typeTiers = 'F';

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_ref")
	private GeoClient client;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inc_code")
	private GeoIncoterm incoterm;

	@Column(name = "tvaid")
	private String tvaCee;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvr_code", nullable = false)
	private GeoRegimeTva regimeTva;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pal_code")
	private GeoTypePalette typePalette;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_code_com")
	private GeoPersonne commercial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_code_ass")
	private GeoPersonne assistante;

	@Column(name = "ind_mod_liv")
	private GeoModeLivraison modeLivraison;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trp_code")
	private GeoTransporteur transporteur;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ttr_code")
	private GeoTypeCamion typeCamion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trp_bta_code")
	private GeoBaseTarif baseTarifTransport;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trs_code")
	private GeoTransitaire transitaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trs_bta_code")
	private GeoBaseTarif baseTarifTransit;

	@Column(name = "instructions_seccom")
	private String instructionSecretaireCommercial;

	@Column(name = "instructions_logistique")
	private String instructionLogistique;

	@Column(name = "gest_code")
	private String gestionnaireChep;

	@Column(name = "gest_ref")
	private String referenceChep;

	@Column(name = "lf_ean")
	private String lieuFonctionEanDepot;

	@Column(name = "lf_ean_by")
	private String lieuFonctionEanAcheteur;

	@Column(name = "ind_eur1")
	private Boolean declarationEur1;

	@Column(name = "env_details_auto")
	private Boolean envoieAutomatiqueDetail;

	@Column(name = "ctl_ref_cli")
	private String controlReferenceClient;

	@Column(name = "mention_client")
	private String mentionClientSurFacture;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "con_tiers", referencedColumnName = "cen_code")
	@JoinColumn(name = "con_tyt", referencedColumnName = "tyt_code")
	private List<GeoContact> contacts;

}
