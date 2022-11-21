package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class GeoPaysDepassement {

    @Id
    @Column(name = "pay_code", updatable = false, insertable = false)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_code", nullable = false, updatable = false, insertable = false)
    private GeoPays pays;

    @Column(name = "pays_permanent")
    private Float clientsSommeAgrement;

    @Column(name = "pays_temporaire")
    private Float clientsSommeEnCoursTemporaire;

    @Column(name = "pays_bw")
    private Float clientsSommeEnCoursBlueWhale;

    @Column(name = "pays_autorise")
    private Float clientsSommeAutorise;

    @Column(name = "pays_solde")
    private Float clientsSommeEnCoursActuel;

    @Column(name = "pays_depasst")
    private Float clientsSommeDepassement;

    @Column(name = "pays_non_echu")
    private Float clientsSommeEnCoursNonEchu;

    @Column(name = "pays_1_30")
    private Float clientsSommeEnCours1a30;

    @Column(name = "pays_31_60")
    private Float clientsSommeEnCours31a60;

    @Column(name = "pays_61_90")
    private Float clientsSommeEnCours61a90;

    @Column(name = "pays_90")
    private Float clientsSommeEnCours90Plus;

    @Column(name = "pays_coface")
    private Float clientsSommeAlerteCoface;

}
