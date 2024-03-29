package fr.microtec.geo2.persistance.entity.stock;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import fr.microtec.geo2.persistance.entity.ValidateEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GeoStockArticle extends ValidateEntity implements GeoStockQuantite {
    @Id
    @Column(name = "rownum")
    private Integer id;

    @Column(name = "art_ref")
    private String articleID;

    @Column(name = "art_desc_long_ref")
    private String articleDescription;

    @ManyToOne
    @JoinColumn(name = "sto_ref", insertable = false, updatable = false)
    private GeoStock stock;

    @Column(name = "art_bio")
    private Boolean bio;

    @Column(name = "age")
    private String age;

    @Column(name = "esp_code")
    private String especeID;

    @Column(name = "var_code")
    private String varieteID;

    @Column(name = "caf_code")
    private String calibreFournisseurID;

    @Column(name = "cam_code")
    private String calibreMarquageID;

    @Column(name = "cat_code")
    private String categorieID;

    @Column(name = "col_code")
    private String colisID;

    @Column(name = "ori_code")
    private String origineID;

    @Column(name = "sto_ref")
    private String stockID;

    @Column(name = "fou_code")
    private String fournisseurCode;

    @Column(name = "prop_code")
    private String proprietaireCode;

    @Column(name = "sto_desc")
    private String description;

    @Column(name = "date_fab")
    private LocalDateTime dateFabrication;

    @Column(name = "sto_statut")
    private Character statut;

    @Column(name = "date_statut")
    private LocalDateTime dateStatut;

    @Column(name = "pal_code")
    private String typePaletteID;

    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "qte_hebdo")
    private Integer quantiteHebdomadaire;

    @Column(name = "qte_ini_1")
    private Integer quantiteInitiale1;
    @Column(name = "qte_res_1")
    private Integer quantiteReservee1;
    @Column(name = "qte_opt_1")
    private Integer quantiteOptionnelle1;

    @Column(name = "qte_ini_2")
    private Integer quantiteInitiale2;
    @Column(name = "qte_res_2")
    private Integer quantiteReservee2;
    @Column(name = "qte_opt_2")
    private Integer quantiteOptionnelle2;

    @Column(name = "qte_ini_3")
    private Integer quantiteInitiale3;
    @Column(name = "qte_res_3")
    private Integer quantiteReservee3;
    @Column(name = "qte_opt_3")
    private Integer quantiteOptionnelle3;

    @Column(name = "qte_ini_4")
    private Integer quantiteInitiale4;
    @Column(name = "qte_res_4")
    private Integer quantiteReservee4;
    @Column(name = "qte_opt_4")
    private Integer quantiteOptionnelle4;

    @Transient
    private Integer quantiteInitiale;

    public Integer getQuantiteInitiale() {
        return this.getQuantiteInitiale1()
                + this.getQuantiteInitiale2()
                + this.getQuantiteInitiale3()
                + this.getQuantiteInitiale4();
    }

    @Transient
    private Integer quantiteReservee;

    public Integer getQuantiteReservee() {
        return this.getQuantiteReservee1()
                + this.getQuantiteReservee2()
                + this.getQuantiteReservee3()
                + this.getQuantiteReservee4();
    }

    @Transient
    private Integer prevision3j;

    public Integer getPrevision3j() {
        return this.getQuantiteInitiale() - this.getQuantiteReservee()
                - Optional.ofNullable(this.getQuantiteHebdomadaire()).orElse(0) * 3;
    }

    @Transient
    private Integer prevision7j;

    public Integer getPrevision7j() {
        return this.getQuantiteInitiale() - this.getQuantiteReservee()
                - Optional.ofNullable(this.getQuantiteHebdomadaire()).orElse(0) * 6;
    }

}
