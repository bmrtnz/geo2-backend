package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.microtec.geo2.persistance.entity.tiers.GeoPersonne;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "geo_ordre")
@Entity
public class GeoOrdreSuiviDeparts implements Serializable {

  // https://github.com/spring-projects/spring-data-jpa/issues/1378

  // Static constructor for GroupBY query
  // GeoOrdreSuiviDeparts(
  //   String id,
  //   String numero,
  //   String codeClient,
  //   String versionDetail,
  //   String codeAlphaEntrepot,
  //   LocalDate dateLivraisonPrevue,
  //   String id_assistante,
  //   String nom_assistante,
  //   String id_commercial,
  //   String nom_commercial,
  //   String id_transporteur,
  //   String rs_transporteur,
  //   String id_societe,
  //   String rs_societe,
  //   double sommeColisCommandes,
  //   double sommeColisExpedies
  // ){}

  @Id
	@Column(name = "ord_ref")
	private String id;

	@Column(name = "nordre")
	private String numero;

  @Column(name = "cli_code")
  private String codeClient;
  
  @Column(name = "version_detail")
	private String versionDetail;

  @Column(name = "cen_code")
	private String codeAlphaEntrepot;

  @Column(name = "livdatp")
	private LocalDate dateLivraisonPrevue;

  @Column(name = "valide")
	private Boolean valide;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codecom")
	private GeoPersonne assistante;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codeass")
	private GeoPersonne commercial;
  
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trp_code", nullable = false)
	private GeoTransporteur transporteur;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soc_code")
	private GeoSociete societe;
  
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "ordre")
	private List<GeoOrdreLogistique> logistiques;

  @Transient
  private double sommeColisCommandes;

  @Transient
  private double sommeColisExpedies;

  // private GeoFournisseur fournisseur;
  // private LocalDate dateDepartPrevueFournisseur;
  // private LocalDate dateDepartReelleFournisseur;
  // private String fournisseurReferenceDOC;
  // private Boolean expedieStation;
  // private Float totalPalettesExpediees;
	// private Float nombrePalettesAuSol;
	// private Float nombrePalettes100x120;
	// private Float nombrePalettes80x120;
	// private Float nombrePalettes60X80;
  // private Double nombreColisCommandes;
  // private Double nombreColisExpedies;
}