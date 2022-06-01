package fr.microtec.geo2.persistance.entity.stock;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import fr.microtec.geo2.persistance.entity.ValidateEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class GeoStockArticle extends ValidateEntity {
    @Id
    @Column(name = "art_ref")
    String id;

    @Column(name = "age")
    String age;

    @Column(name = "esp_code")
    String especeID;

    @Column(name = "var_code")
    String varieteID;

    @Column(name = "caf_code")
    String calibreFournisseurID;

    @Column(name = "cam_code")
    String calibreMarquageID;

    @Column(name = "cat_code")
    String categorieID;

    @Column(name = "col_code")
    String colisID;

    @Column(name = "ori_code")
    String origineID;

    @Column(name = "sto_ref")
    String stockID;

    @Column(name = "fou_code")
    String fournisseurCode;

    @Column(name = "prop_code")
    String proprietaireCode;

    @Column(name = "sto_desc")
    String description;

    @Column(name = "date_fab")
    LocalDateTime dateFabrication;

    @Column(name = "sto_statut")
    String statut;

    @Column(name = "date_statut")
    LocalDateTime dateStatut;

    @Column(name = "pal_code")
    String typePaletteID;

    @Column(name = "qte_ini_1")
    Float quantiteInitiale1;
    @Column(name = "qte_res_1")
    Float quantiteRestante1;
    @Column(name = "qte_opt_1")
    Float quantiteOptionnelle1;

    @Column(name = "qte_ini_2")
    Float quantiteInitiale2;
    @Column(name = "qte_res_2")
    Float quantiteRestante2;
    @Column(name = "qte_opt_2")
    Float quantiteOptionnelle2;

    @Column(name = "qte_ini_3")
    Float quantiteInitiale3;
    @Column(name = "qte_res_3")
    Float quantiteRestante3;
    @Column(name = "qte_opt_3")
    Float quantiteOptionnelle3;

    @Column(name = "qte_ini_4")
    Float quantiteInitiale4;
    @Column(name = "qte_res_4")
    Float quantiteRestante4;
    @Column(name = "qte_opt_4")
    Float quantiteOptionnelle4;

}
