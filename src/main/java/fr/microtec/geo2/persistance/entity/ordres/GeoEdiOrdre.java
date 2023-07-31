package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.entity.tiers.GeoBureauAchat;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoIncoterm;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "geo_edi_ordre")
@Entity
@DynamicUpdate
public class GeoEdiOrdre {

    @Id
    @Column(name = "ref_edi_ordre")
    @GeneratedValue(generator = "GeoEdiOrdreGenerator")
    @GenericGenerator(name = "GeoEdiOrdreGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "SEQ_EDI_ORDRE"),
            @Parameter(name = "isSequence", value = "true")
    })
    private BigDecimal id;

    @NotNull
    @Column(name = "ref_cmd_cli")
    private Integer referenceCommandeClient;

    @NotNull
    @Column(name = "date_doc")
    private LocalDate dateDocument;

    @Column(name = "date_liv")
    private LocalDateTime dateLivraison;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "lf_cmd_par")
    private String lfCommandePar;

    @NotNull
    @Column(name = "lf_fourni")
    private String lfFournisseur;

    @NotNull
    @Column(name = "lf_livre_a")
    private String lfLivreA;

    @Column(name = "lf_facture_a")
    private String lfFactureA;

    @NotNull
    @Column(name = "version")
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @NotNull
    @Column(name = "src_file")
    private String sourceFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cen_ref")
    private GeoEntrepot entrepot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref")
    private GeoClient client;

    @NotNull
    @Column(name = "status_geo", nullable = false)
    private GeoStatusGEO statusGEO;

    @Column(name = "status")
    private GeoStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code")
    private GeoSecteur secteur;

    @Column(name = "mask_modif")
    private String masqueModification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trp_code")
    private GeoTransporteur transporteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_code")
    private GeoBureauAchat bureauAchat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inc_code")
    private GeoIncoterm incoterm;

    @Column(name = "canal_cde")
    private String canalCde;

}
