package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.common.GeoTypeTiers;
import fr.microtec.geo2.persistance.entity.etiquette.DocumentAuditingListener;
import fr.microtec.geo2.persistance.entity.etiquette.GeoAsDocument;
import fr.microtec.geo2.persistance.entity.etiquette.GeoDocument;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Table(name = "geo_envois")
@Entity
@EntityListeners(DocumentAuditingListener.class)
public class GeoEnvois implements GeoAsDocument {

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tyt_code")
	private GeoTypeTiers typeTiers;

	@Transient
	private GeoDocument document;

	@Override
	public String getDocumentName() {
		// return "JF7034.pdf";

		if (this.getNomFichier().matches(Maddog2FileSystemService.HAVE_EXTENSION_REGEX)) {
			return this.getNomFichier();
		}

		return String.format("%s.pdf", this.getNomFichier());
	}
}
