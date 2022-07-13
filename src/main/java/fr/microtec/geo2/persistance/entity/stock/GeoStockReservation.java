package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class GeoStockReservation extends ValidateAndModifiedEntity implements GeoStockQuantite {
    @Id
    @Column(name = "rownum")
    private Integer id;

    @Column(name = "fou_code")
    private String fournisseurCode;

    @Column(name = "prop_code")
    private String proprietaireCode;

    @Column(name = "pal_code")
    private String typePaletteCode;

    @Column(name = "option_stock")
    private String option;

    @Column(name = "qte_ini")
    private Integer quantiteInitiale;
    @Column(name = "qte_res")
    private Integer quantiteReservee;

    @Column(name = "ini1")
    private Integer quantiteInitiale1;
    @Column(name = "res1")
    private Integer quantiteReservee1;
    @Transient
    private Integer quantiteOptionnelle1 = 0;

    @Column(name = "ini2")
    private Integer quantiteInitiale2;
    @Column(name = "res2")
    private Integer quantiteReservee2;
    @Transient
    private Integer quantiteOptionnelle2 = 0;

    @Column(name = "ini3")
    private Integer quantiteInitiale3;
    @Column(name = "res3")
    private Integer quantiteReservee3;
    @Transient
    private Integer quantiteOptionnelle3 = 0;

    @Column(name = "ini4")
    private Integer quantiteInitiale4;
    @Column(name = "res4")
    private Integer quantiteReservee4;
    @Transient
    private Integer quantiteOptionnelle4 = 0;

    @Transient
    private Integer quantiteDisponible;

    public Integer getQuantiteDisponible() {
        return this.getQuantiteInitiale() - this.getQuantiteReservee();
    }

}
