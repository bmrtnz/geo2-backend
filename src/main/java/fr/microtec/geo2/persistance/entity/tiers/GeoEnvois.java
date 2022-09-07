package fr.microtec.geo2.persistance.entity.tiers;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import fr.microtec.geo2.common.StringUtils;
import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.entity.common.GeoTypeTiers;
import fr.microtec.geo2.persistance.entity.document.GeoAsDocument;
import fr.microtec.geo2.persistance.entity.document.GeoDocument;
import fr.microtec.geo2.persistance.entity.ordres.GeoImprimante;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.Data;

@Data
@Table(name = "geo_envois")
@Entity
public class GeoEnvois implements GeoAsDocument {

    @Id
    @Column(name = "env_code")
    @GeneratedValue(generator = "GeoEnvoisGenerator")
    @GenericGenerator(name = "GeoEnvoisGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "F_SEQ_ENV_NUM"),
            @Parameter(name = "isSequence", value = "false")
    })
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @Column(name = "nordre")
    private String numeroOrdre;

    @Column(name = "end_code")
    private String numeroDemande;

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
    private LocalDateTime dateDemande;

    @Column(name = "soudat")
    private LocalDate dateSoumission;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lan_code")
    private GeoPays langue;

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

    @ManyToOne
    @JoinColumn(name = "con_ref")
    private GeoContact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code")
    private GeoSecteur secteurCommercial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_code")
    private GeoSociete societe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cam_code")
    private GeoCampagne campagne;

    @LastModifiedBy
    @Column(name = "mod_user")
    private String userModification;

    @LastModifiedDate
    @Column(name = "mod_date")
    private String dateModification;

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
        if (this.getDateDemande() != null) {
            path = path
                    .resolve(Integer.toString(this.getDateDemande().getYear()))
                    .resolve(StringUtils.padLeft(Integer.toString(this.getDateDemande().getMonthValue()), "0", 2));
        }

        return path.resolve(filename).toString();
    }

    public static void defaultGraphQLFields(Set<String> fields) {
        fields.add("nomFichier");
        fields.add("dateDemande");
    }
}
