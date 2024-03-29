package fr.microtec.geo2.persistance.entity.litige;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_litlig")
@Entity
public class GeoLitigeLigne extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "lil_ref")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lit_ref")
    private GeoLitige litige;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orl_ref")
    private GeoOrdreLigne ordreLigne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lca_code")
    private GeoLitigeCause cause;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lcq_code")
    private GeoLitigeConsequence consequence;

    @Column(name = "res_comment")
    private String commentaireResponsable;

    @Column(name = "orl_lit")
    private String numeroGroupementLitige;

    @Column(name = "cli_pu")
    private Float clientPrixUnitaire;

    @Column(name = "cli_qte")
    private Double clientQuantite;

    @Column(name = "cli_nb_pal")
    private Float clientNombrePalettes;

    @Column(name = "cli_nb_col")
    private Float clientNombreColisReclamation;

    @Column(name = "cli_pds_net")
    private Float clientPoidsNet;

    @Column(name = "cli_bta_code")
    private String clientUniteFactureCode;

    @Column(name = "cli_pds_lit")
    private Float clientUnitePoidsDeclare;

    @Column(name = "cli_ind_forf")
    private Boolean clientIndicateurForfait;

    @Column(name = "res_dev_code")
    private String deviseCode;

    @Column(name = "res_dev_pu")
    private Double devisePrixUnitaire;

    @Column(name = "res_dev_taux")
    private Double deviseTaux;

    @Column(name = "res_nb_pal")
    private Float responsableNombrePalettes;

    @Column(name = "res_nb_col")
    private Float responsableNombreColis;

    @Column(name = "res_pds_net")
    private Float responsablePoidsNet;

    @Column(name = "res_pu")
    private Float responsablePrixUnitaire;

    @Column(name = "res_bta_code")
    private String responsableUniteFactureCode;

    @Column(name = "res_qte")
    private Double responsableQuantite;

    @Column(name = "res_ind_forf")
    private Boolean responsableIndicateurForfait;

    @Column(name = "tyt_code")
    private String responsableTypeCode;

    @Column(name = "tie_code")
    private String tiersCode;

    @Column(name = "lca_code", insertable = false, updatable = false)
    private String causeLitigeCode;

    @Column(name = "lcq_code", insertable = false, updatable = false)
    private String consequenceLitigeCode;

    @Column(name = "fl_encours")
    private Boolean negociationsEnCours;

    @Column(name = "ind_env_inc")
    private Boolean envoisIncident;

    @Column(name = "ord_ref_replace")
    private String ordreReferenceRemplacement;

    @Column(name = "nordre_replace")
    private String numeroOrdreReplacement;

}
