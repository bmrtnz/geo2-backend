package fr.microtec.geo2.persistance.entity.tiers;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Where;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.ValidateModifiedPrewrittedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoModification;
import fr.microtec.geo2.persistance.entity.common.GeoTypeVente;
import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueClient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_client")
@Entity
@DynamicInsert
@DynamicUpdate
public class GeoClient extends ValidateModifiedPrewrittedEntity implements Serializable {

    public static final String TYPE_TIERS = "C";

    @Id
    @Column(name = "cli_ref")
    @GeneratedValue(generator = "GeoClientGenerator")
    @GenericGenerator(name = "GeoClientGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_cli_num"),
            @Parameter(name = "mask", value = "FM099999")
    })
    private String id;

    @NotNull
    @Column(name = "cli_code", nullable = false, unique = true)
    private String code;

    @NotNull
    @Column(name = "tyt_code", nullable = false)
    private Character typeTiers = TYPE_TIERS.charAt(0);

    @NotNull
    @Column(name = "raisoc", nullable = false)
    private String raisonSocial;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "con_tiers", referencedColumnName = "cli_code")
    @JoinColumn(name = "con_tyt", referencedColumnName = "tyt_code")
    private List<GeoContact> contacts;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref")
    private List<GeoEntrepot> entrepots;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_code", nullable = false)
    private GeoSociete societe;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code", nullable = false)
    private GeoSecteur secteur;

    // region Primary Address
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
    // endregion

    // region Billing address
    @Column(name = "raisoc_fact")
    private String facturationRaisonSocial;

    @Column(name = "ads1_fact")
    private String facturationAdresse1;

    @Column(name = "ads2_fact")
    private String facturationAdresse2;

    @Column(name = "ads3_fact")
    private String facturationAdresse3;

    @Column(name = "zip_fact")
    private String facturationCodePostal;

    @Column(name = "ville_fact")
    private String facturationVille;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_code_fact")
    private GeoPays facturationPays;
    // endregion

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tvr_code", nullable = false)
    private GeoRegimeTva regimeTva;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inc_code", nullable = false)
    private GeoIncoterm incoterm;

    @NotNull
    @Column(name = "echnbj", nullable = false)
    private String nbJourEcheance;

    @Column(name = "echle")
    private String echeanceLe;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mpm_code", nullable = false)
    private GeoMoyenPaiement moyenPaiement;

    @Column(name = "tvaid")
    private String tvaCee;

    @Column(name = "ctl_ref_cli")
    private String controlReferenceClient;

    @Column(name = "comment_haut_facture")
    private String commentaireHautFacture;

    @Column(name = "comment_bas_facture")
    private String commentaireBasFacture;

    @Column(name = "instructions_seccom")
    private String instructionCommercial;

    @Column(name = "siret")
    private String siret;

    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name = "navoir_edi")
    private Boolean blocageAvoirEdi;

    @Column(name = "ind_comm_debloq")
    private Boolean debloquerEnvoieJour;

    @Column(name = "ind_modif_detail")
    private Boolean modificationDetail;

    @Column(name = "ind_gest_colis_manquant")
    private Boolean colisManquant;

    @Column(name = "ifco")
    private String ifco;

    @Column(name = "date_debut_ifco")
    private LocalDate dateDebutIfco;

    @Column(name = "nbj_litige_lim")
    private Integer nbJourLimiteLitige;

    @Column(name = "instructions_logistique")
    private String instructionLogistique;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bpm_code", nullable = false)
    private GeoBasePaiement basePaiement;

    @NotNull
    @Column(name = "compte_compta", nullable = false)
    private String compteComptable;

    @Column(name = "lf_ean")
    private String lieuFonctionEan;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lan_code", nullable = false)
    private GeoPays langue;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dev_code", nullable = false)
    private GeoDevise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_code_com")
    private GeoPersonne commercial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_code_ass")
    private GeoPersonne assistante;

    @Column(name = "dev_tx_fix")
    private Float deviseTauxFix;

    @Column(name = "dat_dev_tx_fix")
    private LocalDate dateDeviseTauxFix;

    @Column(name = "enc_references")
    private String referenceCoface;

    @Column(name = "decision_coface")
    private Boolean refusCoface;

    @Column(name = "enc_assure", nullable = false)
    private Float agrement;

    @Column(name = "enc_0", nullable = false)
    private Float enCoursNonEchu;

    @Column(name = "enc_1", nullable = false)
    private Float enCours1a30;

    @Column(name = "enc_2", nullable = false)
    private Float enCours31a60;

    @Column(name = "enc_3", nullable = false)
    private Float enCours61a90;

    @Column(name = "enc_4", nullable = false)
    private Float enCours90Plus;

    @Column(name = "alerte_coface")
    private Float alerteCoface;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crt_code")
    private GeoCourtier courtier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crt_bta_code")
    private GeoBaseTarif courtageModeCalcul;

    @Column(name = "crt_pu")
    private Float courtageValeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tcl_code")
    private GeoTypeClient typeClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gcl_code")
    private GeoGroupeClient groupeClient;

    @NotNull
    @Column(name = "soumis_ctifl", nullable = false)
    private Boolean soumisCtifl;

    @Column(name = "frais_plateforme")
    private Float fraisPlateforme;

    @Column(name = "frais_pu")
    private Float fraisMarketing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frais_unite")
    private GeoBaseTarif fraisMarketingModeCalcul;

    @Column(name = "rem_hf_tx")
    private Float tauxRemiseHorsFacture;

    @Column(name = "rem_sf_tx_mdd")
    private Float remiseSurFactureMDDTaux;

    @Column(name = "rem_sf_tx")
    private Float tauxRemiseParFacture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tvt_code")
    private GeoTypeVente typeVente;

    @Column(name = "enc_depasse", nullable = false)
    private Float enCoursTemporaire;

    @Column(name = "enc_bw", nullable = false)
    private Float enCoursBlueWhale;

    @Column(name = "enc_actuel", nullable = false)
    private Float enCoursActuel;

    @Column(name = "enc_douteux")
    private Float enCoursDouteux;

    @Column(name = "enc_date_valid")
    private LocalDate enCoursDateLimite;

    @Column(name = "flclodet_autom")
    private Boolean clotureAutomatique;

    @Column(name = "fldet_autom")
    private Boolean detailAutomatique;

    @Column(name = "ind_frais_ramas")
    private Boolean fraisRamasse;

    @Column(name = "ind_exclu_frais_pu")
    private Boolean fraisExcluArticlePasOrigineFrance;

    @Column(name = "ind_vente_com")
    private Boolean venteACommission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref_palox")
    private GeoClient paloxRaisonSocial;

    @Column(name = "delai_baf")
    private Integer delaiBonFacturer;

    @Column(name = "dluo")
    private String formatDluo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cov_code", nullable = false)
    private GeoConditionVente conditionVente;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<GeoHistoriqueClient> historique;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "typ_tiers = '" + TYPE_TIERS + "'")
    private Set<GeoCertificationClient> certifications;

    @Column(name = "ind_usint")
    private Boolean usageInterne;

    @Column(name = "ind_palox_gratuit")
    private Boolean paloxGratuit;

    @Column(name = "ind_cons_palox")
    private Boolean consignePalox;

    @Column(name = "releve_fac")
    private Boolean releveFactures;

    @Column(name = "id_fiscal")
    private String identifiantFiscal;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    @Where(clause = "entite = 'Client'")
    private Set<GeoModification> modifications;

    @Transient
    @Formula("coalesce(enc_assure,0) + coalesce(enc_depasse,0) + coalesce(enc_bw,0)")
    private Float autorise;

    @Transient
    @Formula("coalesce(enc_actuel,0) - (coalesce(enc_assure,0) + coalesce(enc_depasse,0) + coalesce(enc_bw,0))")
    private Float depassement;

    public void setCertifications(Set<GeoCertificationClient> certifications) {
        certifications.forEach(c -> c.setClient(this));

        if (this.certifications != null) {
            this.certifications.clear();
            this.certifications.addAll(certifications);
        } else {
            this.certifications = certifications;
        }
    }

    @PrePersist()
    void prePersist() {
        if (this.getVenteACommission() == null)
            this.setVenteACommission(false);
    }

}
