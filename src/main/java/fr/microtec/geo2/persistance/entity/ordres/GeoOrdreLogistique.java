package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoGroupage;
import fr.microtec.geo2.persistance.entity.tiers.GeoIncoterm;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

@Getter
@Setter
@Table(name = "geo_ordlog")
@Entity
public class GeoOrdreLogistique extends ValidateAndModifiedEntity {

    @Id
    @GeneratedValue(generator = "GeoOrdreLogistiqueGenerator")
    @GenericGenerator(name = "GeoOrdreLogistiqueGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "F_SEQ_ORX_SEQ"),
            @Parameter(name = "isSequence", value = "false")
    })
    @Column(name = "orx_ref")
    private String id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fou_code", referencedColumnName = "fou_code", insertable = false, updatable = false)
    private GeoFournisseur fournisseur;

    @Column(name = "fou_code")
    private String codeFournisseur;

    @NotNull
    @Column(name = "flag_exped_fournni")
    private Boolean expedieStation;

    @Column(name = "flag_exped_groupa")
    private Boolean expedieLieuGroupage;

    @Column(name = "locus_trace")
    private String locusTrace;

    @Column(name = "datdep_fou_p")
    private LocalDateTime dateDepartPrevueFournisseur;

    @Column(name = "datdep_fou_p_yyyymmdd")
    private String dateDepartPrevueFournisseurRaw;

    @Column(name = "datdep_fou_r")
    private LocalDateTime dateDepartReelleFournisseur;

    @Column(name = "datdep_grp_p")
    private LocalDateTime dateDepartPrevueGroupage;

    @Column(name = "datdep_grp_r")
    private LocalDateTime dateDepartReelleGroupage;

    @Column(name = "datliv_grp")
    private LocalDateTime dateLivraisonLieuGroupage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grp_code")
    private GeoGroupage groupage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "logistique")
    private List<GeoOrdreLigne> lignes;

    @Column(name = "pal_nb_sol")
    private Float nombrePalettesAuSol;

    @Column(name = "pal_nb_PB100X120")
    private Float nombrePalettes100x120;

    @Column(name = "pal_nb_PB80X120")
    private Float nombrePalettes80x120;

    @Column(name = "pal_nb_PB60X80")
    private Float nombrePalettes60x80;

    @Column(name = "tot_cde_nb_pal")
    private Float totalPalettesCommandees;

    @Column(name = "tot_exp_nb_pal")
    private Float totalPalettesExpediees;

    @Column(name = "instructions")
    private String instructions;

    @Column(name = "fou_ref_doc")
    private String fournisseurReferenceDOC;

    @Column(name = "ref_logistique")
    private String referenceLogistique;

    @Column(name = "ref_document")
    private String referenceDocument;

    @Column(name = "typ_grp")
    private Character typeLieuGroupageArrivee;

    @Column(name = "typ_fou")
    private Character typeLieuDepart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incot_fourn")
    private GeoIncoterm incotermFournisseur;

    @Column(name = "plomb")
    private String numeroPlomb;

    @Column(name = "immatriculation")
    private String numeroImmatriculation;

    @Column(name = "detecteur_temp")
    private String detecteurTemperature;

    @Column(name = "certif_controle")
    private String certificatControle;

    @Column(name = "certif_phyto")
    private String certificatPhytosanitaire;

    @Column(name = "bill_of_lading")
    private String billOfLanding;

    @Column(name = "container")
    private String numeroContainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trp_code")
    private GeoTransporteur transporteurGroupage;

    @Transient
    private String okStation;

    @NotNull
    @Column(name = "valide")
    private Boolean valide;

    @PostLoad
    public void postLoad() {
        val expedieStation = Optional.ofNullable(this.expedieStation).orElse(false);
        if (this.dateDepartReelleFournisseur == null) {
            if (expedieStation
                    && this.totalPalettesExpediees != null
                    && this.nombrePalettesAuSol != null
                    && this.nombrePalettes100x120 != null
                    && this.nombrePalettes80x120 != null
                    && this.nombrePalettes60x80 != null
                    && this.totalPalettesExpediees == 0
                    && this.nombrePalettesAuSol == 0
                    && this.nombrePalettes100x120 == 0
                    && this.nombrePalettes80x120 == 0
                    && this.nombrePalettes60x80 == 0)
                this.okStation = "clôturé à zéro";
            else if (expedieStation)
                this.okStation = "OK";
            else
                this.okStation = "non clôturé";
        }
    }

}
