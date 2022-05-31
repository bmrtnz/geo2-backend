package fr.microtec.geo2.persistance.entity.stock;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class GeoStockArticle {
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

    @Column(name = "prop_cod")
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

}
