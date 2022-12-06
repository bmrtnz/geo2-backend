package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import lombok.Data;

@Data
@Entity
public class GeoLigneChargement {

    @Id
    @Column(name = "orl_ref")
    String id;

    @Column(name = "code_chargement")
    private String codeChargement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @Column(name = "nordre")
    private String numeroOrdre;

    @Column(name = "fou_code")
    private String codeFournisseur;

    @Column(name = "cen_code")
    private String codeEntrepot;

    @Column(name = "depdatp")
    private LocalDateTime dateDepartPrevue;

    @Column(name = "livdatp")
    private LocalDateTime dateLivraisonPrevue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @Column(name = "art_alpha")
    private String descriptionArticle;

    @Column(name = "col_desc")
    private String descriptionColis;

    @Column(name = "cde_nb_pal")
    private Float nombrePalettesCommandees;

    @Column(name = "cde_nb_col")
    private Float nombreColisCommandes;

    @Column(name = "pal_nb_col")
    private Float nombreColisPalette;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orl_ref", insertable = false, updatable = false)
    private GeoOrdreLigne ligne;

    @Column(name = "num_camion")
    private Integer numeroCamion;

    @Column(name = "ordre_chargement")
    private Integer ordreChargement;

    @Column(name = "datdep_fou_p")
    private LocalDateTime dateDepartPrevueFournisseur;

}
