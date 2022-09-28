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

    @Column(name = "somme_agrement")
    private Float clientsSommeAgrement;

    @Column(name = "somme_en_cours_temporaire")
    private Float clientsSommeEnCoursTemporaire;

    @Column(name = "somme_en_cours_blue_whale")
    private Float clientsSommeEnCoursBlueWhale;

    @Column(name = "somme_autorise")
    private Float clientsSommeAutorise;

    @Column(name = "somme_en_cours_actuel")
    private Float clientsSommeEnCoursActuel;

    @Column(name = "somme_depassement")
    private Float clientsSommeDepassement;

    @Column(name = "somme_en_cours_non_echu")
    private Float clientsSommeEnCoursNonEchu;

    @Column(name = "somme_en_cours_1a30")
    private Float clientsSommeEnCours1a30;

    @Column(name = "somme_en_cours_31a60")
    private Float clientsSommeEnCours31a60;

    @Column(name = "somme_en_cours_61a90")
    private Float clientsSommeEnCours61a90;

    @Column(name = "somme_en_cours_90_plus")
    private Float clientsSommeEnCours90Plus;

    @Column(name = "somme_alerte_coface")
    private Float clientsSommeAlerteCoface;

}
