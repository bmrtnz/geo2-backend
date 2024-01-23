package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoModeCulture;
import fr.microtec.geo2.persistance.entity.produits.GeoVariete;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_precal")
@Entity
public class GeoPrecal extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "prc_ref")
    private String id;

    @Column(name = "semaine")
    @NotNull
    private String semaine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
    @NotNull
    private GeoFournisseur fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "var_code")
    @NotNull
    private GeoVariete variete;

    @Column(name = "choix")
    @NotNull
    private String choix;

    @Column(name = "colo")
    private String colo;

    @Column(name = "qte")
    private Integer quantite;

    @Column(name = "commentaire")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mode_culture")
    private GeoModeCulture modeCulture;

    @Column(name = "p198")
    private Integer p198;

    @Column(name = "p175")
    private Integer p175;

    @Column(name = "p163")
    private Integer p163;

    @Column(name = "p150")
    private Integer p150;

    @Column(name = "p138")
    private Integer p138;

    @Column(name = "p125")
    private Integer p125;

    @Column(name = "p113")
    private Integer p113;

    @Column(name = "p100")
    private Integer p100;

    @Column(name = "p88")
    private Integer p88;

    @Column(name = "p80")
    private Integer p80;

    @Column(name = "p72")
    private Integer p72;

    @Column(name = "p64")
    private Integer p64;

    @Column(name = "p56")
    private Integer p56;

    @Column(name = "p216")
    private Integer p216;

    @Column(name = "p204")
    private Integer p204;

    @Column(name = "p232")
    private Integer p232;

    @Column(name = "p248")
    private Integer p248;

    @Column(name = "p267")
    private Integer p267;

    @Column(name = "p288")
    private Integer p288;

    @Column(name = "p327")
    private Integer p327;

}
