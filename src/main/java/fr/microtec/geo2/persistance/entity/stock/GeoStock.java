package fr.microtec.geo2.persistance.entity.stock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_stock")
@Entity
public class GeoStock extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "sto_ref")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
    private GeoFournisseur fournisseur;

    @Column(name = "sto_desc")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prop_code", referencedColumnName = "fou_code", nullable = false)
    private GeoFournisseur proprietaire;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stock")
    private List<GeoStockMouvement> mouvements;

    @Column(name = "ini_user")
    private String utilisateurInfo;

    @Column(name = "ini_date")
    private LocalDate dateInfo;

    @Column(name = "qte_ini")
    private Integer quantiteInitiale;

    @Column(name = "qte_res")
    private Integer quantiteReservee;

    @Formula("qte_ini - qte_res")
    private Integer quantiteTotale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pal_code")
    private GeoTypePalette typePalette;

    @Column(name = "sto_statut")
    private Character statutStock;

    @Column(name = "age")
    private Character age;

    @Column(name = "date_fab")
    private LocalDate dateFabrication;

    @Column(name = "date_statut")
    private LocalDateTime dateStatut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sto_ref_from")
    private GeoStock stockOrigine;

}
