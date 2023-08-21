package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class GeoArticleStatistiqueClient {

    @Id
    @Column(name = "rownum")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @ManyToOne
    @JoinColumn(name = "cli_ref")
    private GeoClient client;

    @Column(name = "exp_nb_col")
    private Float expeditionNbColis;

    @Column(name = "exp_pds_net")
    private Float expeditionPoidsNet;

    @Column(name = "nordre")
    private String numeroOrdre;

}
