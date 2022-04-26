package fr.microtec.geo2.persistance.entity.tiers;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.microtec.geo2.common.StringUtils;
import fr.microtec.geo2.persistance.entity.common.GeoTypeTiers;
import fr.microtec.geo2.persistance.entity.etiquette.DocumentAuditingListener;
import fr.microtec.geo2.persistance.entity.etiquette.GeoAsDocument;
import fr.microtec.geo2.persistance.entity.etiquette.GeoDocument;
import fr.microtec.geo2.persistance.entity.ordres.GeoImprimante;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.Data;

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

	@Column(name = "contact")
	private String nomContact;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "imp_id")
	private GeoImprimante imprimante;

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

	@Column(name = "trait_exp")
	private Character traite;

	@Transient
	private GeoDocument document;

	@Override
	public String getDocumentName() {
		// return "JF7034.pdf";

		String filename = Optional.ofNullable(this.getNomFichier()).orElse("unknown");
		Path path = Path.of("");

		// If do not end with extension (exemple : .pdf)
		if (!filename.matches(Maddog2FileSystemService.HAVE_EXTENSION_REGEX)) {
			filename += ".pdf";
		}

		// If date envoie, file is in subpath
		if (this.getDateEnvoi() != null) {
			path = path
					.resolve(Integer.toString(this.getDateEnvoi().getYear()))
					.resolve(StringUtils.padLeft(Integer.toString(this.getDateEnvoi().getMonthValue()), "0", 2));
		}

		return path.resolve(filename).toString();
	}
}
