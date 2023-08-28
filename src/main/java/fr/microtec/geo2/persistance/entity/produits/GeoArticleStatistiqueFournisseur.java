package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class GeoArticleStatistiqueFournisseur {

    @Id
    @Column(name = "rownum")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @ManyToOne
    @JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
    private GeoFournisseur fournisseur;

    @Column(name = "nordre")
    private String numeroOrdre;

    @Column(name = "exp_nb_col")
    private Float expeditionNbColis;

    @Column(name = "exp_pds_net")
    private Float expeditionPoidsNet;

    @Column(name = "nb_ordre")
    private Integer nbOrdre;

}
