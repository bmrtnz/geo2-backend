package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_stomvt")
@Entity
public class GeoStockMouvement extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "stm_ref")
    @GeneratedValue(generator = "GeoStockMouvementGenerator")
    @GenericGenerator(name = "GeoStockMouvementGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_stm_num"),
            @Parameter(name = "mask", value = "FM099999")
    })
    private String id;

    @Column(name = "stm_desc")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sto_ref")
    private GeoStock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orl_ref")
    private GeoOrdreLigne ordreLigne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @Column(name = "mvt_qte")
    private Integer quantite;

    @Column(name = "mvt_type")
    private Character type;

    @Column(name = "nom_utilisateur")
    private String nomUtilisateur;

    @Formula("(nom_utilisateur || ' ' || stm_desc)")
    private String parQui;

}
