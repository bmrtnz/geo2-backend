package fr.microtec.geo2.persistance.entity.litige;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_litige")
@Entity
public class GeoLitige extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "lit_ref")
    @GeneratedValue(generator = "GeoLitigeGenerator")
    @GenericGenerator(name = "GeoLitigeGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_lit_num"),
            @Parameter(name = "mask", value = "FM099999"),
    })
    private String id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref_origine", nullable = false)
    private GeoOrdre ordreOrigine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref_avoir")
    private GeoOrdre ordreAvoirClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref_avoir_fourni")
    private GeoOrdre ordreAvoirFournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref_replace")
    private GeoOrdre ordreReplacement;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "litige")
    private List<GeoLitigeLigne> lignes;

    @Column(name = "lit_frais_annexes")
    private Float fraisAnnexes;

    @Column(name = "lit_frais_comment")
    private String fraisAnnexesCommentaires;

    @Column(name = "lca_code")
    private String causeLitigeCode;

    @Column(name = "lcq_code")
    private String consequenceLitigeCode;

    @Column(name = "tyt_code")
    private Character responsableTiers;

    @Column(name = "tie_code")
    private String responsableTiersCode;

    @Column(name = "ref_cli")
    private String referenceClient;

    @Column(name = "lit_comment")
    private String commentairesInternes;

    @Column(name = "res_comment")
    private String commentairesResponsable;

    @Column(name = "lit_date_creation")
    private LocalDate dateCreation;

    @Column(name = "lit_date_origine")
    private LocalDate dateOrigine;

    @Column(name = "lit_date_resolution")
    private LocalDate dateResolution;

    @Column(name = "lit_date_avoir_client")
    private LocalDate dateAvoirClient;

    @Column(name = "lit_date_avoir_fourni")
    private LocalDate dateAvoirFournisseur;

    @Column(name = "date_doc")
    private LocalDate dateEnvoiDocuments;

    @Column(name = "fl_encours")
    private Boolean enCoursNegociation;

    @Column(name = "fl_client_clos")
    private Boolean clientCloture;

    @Column(name = "fl_fourni_clos")
    private Boolean fournisseurCloture;

    @Column(name = "fl_client_admin")
    private Boolean clientValideAdmin;

    @Column(name = "fl_fourni_admin")
    private Boolean fournisseurValideAdmin;

    @Column(name = "num_version")
    private Integer numeroVersion;

    @Column(name = "tot_mont_rist_sf")
    private Float totalMontantRistourne;

}
