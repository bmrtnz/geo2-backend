package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
public class GeoLigneReservation extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "stm_ref")
    private String id;

    @Column(name = "sto_ref")
    private String stockId;

    @Column(name = "nom_utilisateur")
    private String nomUtilisateur;

    @Column(name = "mvt_type")
    private Character type;

    @Column(name = "mvt_qte")
    private Integer quantite;

    @Column(name = "ord_ref")
    private String ordreId;

    @Column(name = "art_ref")
    private String articleId;

    @Column(name = "orl_ref")
    private String ordreLigneId;

    @Column(name = "stm_desc")
    private String mouvementDescription;

    @Column(name = "ligne_fournisseur_code")
    private String ligneFournisseurCode;

    @Column(name = "stock_fournisseur_code")
    private String fournisseurCode;

    @Column(name = "prop_code")
    private String proprietaireCode;

    @Column(name = "sto_desc")
    private String stockDescription;

    @Column(name = "qte_ini")
    private Integer quantiteInitiale;

    @Column(name = "qte_res")
    private Integer quantiteReservee;

    @Column(name = "pal_code")
    private String typePalette;

}
