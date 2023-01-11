package fr.microtec.geo2.persistance.entity.litige;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoPersonne;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import lombok.Data;

@Data
@Entity
public class GeoLitigeSupervision {

    @Id
    @Column(name = "rownum")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code")
    private GeoSecteur secteurCommercial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_code")
    private GeoPersonne personne;

    @Column(name = "per_nom")
    private String nomPersonne;

    @Column(name = "per_prenom")
    private String prenomPersonne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lit_ref")
    private GeoLitige litige;

    @Column(name = "lit_date_creation")
    private LocalDateTime dateCreationLitige;

    @Column(name = "cli_code")
    private String codeClient;

    @Column(name = "var_desc")
    private String descriptionVariete;

    @Column(name = "nordre")
    private String numeroOrdre;

    @Column(name = "datdep_fou_p")
    private LocalDateTime dateDepartPrevueFournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prop_code", referencedColumnName = "fou_code")
    private GeoFournisseur proprietaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
    private GeoFournisseur fournisseur;

    @Column(name = "lca_desc")
    private String descriptionCause;

    @Column(name = "lcq_desc")
    private String descriptionConsequence;

    @Column(name = "fl_client_clos")
    private Boolean clientClos;

    @Column(name = "fl_fourni_clos")
    private Boolean fournisseurClos;

    @Column(name = "nordre_replace")
    private String numeroOrdreRemplacement;

    @Column(name = "delai")
    private Double delai;

    @Column(name = "lit_frais_annexes")
    private Float fraisAnnexesLitige;

    @Column(name = "prix_client")
    private Float prixClient;

    @Column(name = "prix_fourni")
    private Float prixFourni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_code")
    private GeoSociete societe;

    @Column(name = "res_comment")
    private String commentaireLigneLitige;

    @Column(name = "container")
    private String container;

    @Column(name = "immatriculation")
    private String immatriculation;

    @Column(name = "code_chargement")
    private String codeChargement;

}
