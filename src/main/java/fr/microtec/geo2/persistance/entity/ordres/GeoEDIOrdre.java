package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "geo_edi_ordre")
@Entity
@DynamicUpdate
public class GeoEDIOrdre {

    @Id
	@Column(name = "ref_edi_ordre")
	private Integer id;

    @Column(name = "date_doc")
    private LocalDate dateDocument;

    @Column(name = "date_liv")
    private LocalDateTime dateLivraison;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "lf_cmd_par")
    private String lfCommandePar;

    @Column(name = "lf_fourni")
    private String lfFournisseur;

    @Column(name = "lf_livre_a")
    private String lfLivreA;

    @Column(name = "lf_facture_a")
    private String lfFactureA;

    @Column(name = "version")
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @Column(name = "src_file")
    private String sourceFile;

    // Interpret '-' as null !!!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("DECODE({alias}.cen_ref, '-', null, {alias}.cen_ref)")
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

}
