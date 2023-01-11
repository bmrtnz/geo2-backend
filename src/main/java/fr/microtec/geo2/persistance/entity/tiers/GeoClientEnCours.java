package fr.microtec.geo2.persistance.entity.tiers;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import lombok.Data;

@Data
@Entity
public class GeoClientEnCours {

    @Id
    @Column(name = "rownum")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_code")
    private GeoSociete societe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cen_ref")
    private GeoEntrepot entrepot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref")
    private GeoClient client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dev_code")
    private GeoDevise devise;

    @Column(name = "cpt_code")
    private String cptCode;

    @Column(name = "cfc_date_ecriture")
    private LocalDate cfcDateEcriture;

    @Column(name = "cfc_date_echeance")
    private LocalDate cfcDateEcheance;

    @Column(name = "cfc_sens")
    private String cfcSens;

    @Column(name = "cfc_mt_euros")
    private Float cfcMontantEuros;

    @Column(name = "cfc_mt_devise")
    private Float cfcMontantDevise;

    @Column(name = "cfc_ref_piece")
    private String cfcRefPiece;

    @Column(name = "cfc_intitule")
    private String cfcIntitule;

    @Column(name = "cfc_rappel")
    private String cfcRappel;

    @Column(name = "ech_niv")
    private String echeanceNiveau;

    @Column(name = "cli_code")
    private String clientCode;

    @Column(name = "raisoc")
    private String raisonSociale;

    @Column(name = "zip")
    private String zip;

    @Column(name = "ville")
    private String ville;

    @Column(name = "pay_code")
    private String paysCode;

    @Column(name = "enc_assure")
    private Float encAssure;

    @Column(name = "enc_references")
    private String encReferences;

    @Column(name = "enc_depasse")
    private Float encDepasse;

    @Column(name = "enc_date_valid")
    private LocalDate encDateValid;

    @Column(name = "enc_bw")
    private Float encBw;

    @Column(name = "enc_actuel")
    private Float encActuel;

    @Column(name = "cg_num")
    private String cgNum;

    @Column(name = "datfac")
    private LocalDate dateFac;

    @Column(name = "ec_piece")
    private String ecPiece;

    @Column(name = "num_immat")
    private String numImmat;

    @Column(name = "container")
    private String numeroContainer;

}
