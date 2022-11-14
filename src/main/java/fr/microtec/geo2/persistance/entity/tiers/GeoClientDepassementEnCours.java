package fr.microtec.geo2.persistance.entity.tiers;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class GeoClientDepassementEnCours {

    @Id
    @Column(name = "rownum")
    private Integer id;

    @Column(name = "secteur_permanent")
    private Integer secteurPermanent;

    @Column(name = "secteur_temporaire")
    private Integer secteurTemporaire;

    @Column(name = "secteur_bw")
    private Integer secteurBw;

    @Column(name = "secteur_autorise")
    private Integer secteurAutorise;

    @Column(name = "secteur_depasst")
    private Integer secteurDepasst;

    @Column(name = "secteur_solde")
    private Integer secteurSolde;

    @Column(name = "secteur_non_echu")
    private Integer secteurNonEchu;

    @Column(name = "secteur_1_30")
    private Integer secteur1_30;

    @Column(name = "secteur_31_60")
    private Integer secteur31_60;

    @Column(name = "secteur_61_90")
    private Integer secteur61_90;

    @Column(name = "secteur_90")
    private Integer secteur90;

    @Column(name = "secteur_coface")
    private Integer secteurCoface;

    @Column(name = "pays_permanent")
    private Integer paysPermanent;

    @Column(name = "pays_temporaire")
    private Integer paysTemporaire;

    @Column(name = "pays_bw")
    private Integer paysBw;

    @Column(name = "pays_autorise")
    private Integer paysAutorise;

    @Column(name = "pays_depasst")
    private Integer paysDepasst;

    @Column(name = "pays_solde")
    private Integer paysSolde;

    @Column(name = "pays_non_echu")
    private Integer paysNonEchu;

    @Column(name = "pays_1_30")
    private Integer pays1_30;

    @Column(name = "pays_31_60")
    private Integer pays31_60;

    @Column(name = "pays_61_90")
    private Integer pays61_90;

    @Column(name = "pays_90")
    private Integer pays90;

    @Column(name = "pays_coface")
    private Integer paysCoface;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code")
    private GeoSecteur secteur;

    @Column(name = "pay_desc")
    private String payDesc;

    @Column(name = "raisoc")
    private String raisoc;

    @Column(name = "ville")
    private String ville;

    @Column(name = "enc_bw")
    private Integer encBw;

    @Column(name = "enc_actuel")
    private Integer encActuel;

    @Column(name = "enc_0")
    private Integer enc0;

    @Column(name = "enc_1")
    private Integer enc1;

    @Column(name = "enc_2")
    private Integer enc2;

    @Column(name = "enc_3")
    private Integer enc3;

    @Column(name = "enc_4")
    private Integer enc4;

    @Column(name = "enc_assure")
    private Integer encAssure;

    @Column(name = "enc_depasse")
    private Integer encDepasse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref")
    private GeoClient client;

    @Column(name = "enc_douteux")
    private Integer encDouteux;

    @Column(name = "enc_date_valid")
    private LocalDate encDateValid;

    @Column(name = "alerte_coface")
    private Integer alerteCoface;

    @Column(name = "valide")
    private Boolean valide;

}
