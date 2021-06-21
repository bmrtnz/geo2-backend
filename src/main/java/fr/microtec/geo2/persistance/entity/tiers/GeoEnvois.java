package fr.microtec.geo2.persistance.entity.tiers;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_envois")
@Entity
public class GeoEnvois extends ModifiedEntity {
  
  @Id
	@Column(name = "env_code")
	private String id;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
	private GeoOrdre ordre;

	@Column(name = "nordre")
	private String numeroOrdre;

	@Column(name = "version_ordre")
	private String versionOrdre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codecom")
	private GeoPersonne assistante;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codeass")
	private GeoPersonne commercial;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flu_code")
	private GeoFlux flux;

	@Column(name = "tyt_code")
  private Character typeTiers;

	@Column(name = "tie_code")
  private String codeTiers;

	@Column(name = "acces1")
  private String numeroAcces1;

	@Column(name = "demdat")
  private LocalDate dateDemande;

	@Column(name = "envdat")
  private LocalDate dateEnvoi;

	@Column(name = "ackdat")
  private LocalDate dateAccuseReception;

	@Column(name = "nbtent")
  private Integer nombreTentatives;

	@Column(name = "imp_id")
  private String imprimanteID;

	@Column(name = "env_desc")
  private String commentairesAvancement;

	@Column(name = "doc_filename")
  private String nomFichier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moc_code")
	private GeoMoyenCommunication moyenCommunication;

}
