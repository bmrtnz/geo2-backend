package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class GeoCommandeEdi {

    @Id
    @Column(name = "rownum")
    private Integer id;

    @Column(name = "ref_edi_ordre")
    private String refEdiOrdre;

    @Column(name = "ref_cmd_cli")
    private String refCmdClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref")
    private GeoClient client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cen_ref")
    private GeoEntrepot entrepot;

    @Column(name = "date_liv")
    private LocalDateTime dateLivraison;

    @Column(name = "src_file")
    private String fichierSource;

    @Column(name = "version")
    private String version;

    @Column(name = "num_ligne")
    private Integer numeroLigne;

    @Column(name = "libelle_produit")
    private String libelleProduit;

    @Column(name = "par_combien")
    private String parCombien;

    @Column(name = "quantite")
    private Integer quantite;

    @Column(name = "quantite_colis")
    private Integer quantiteColis;

    @Column(name = "unite_qtt")
    private String uniteQtt;

    @Column(name = "type_colis")
    private String typeColis;

    @Column(name = "mask_ordre")
    private String masqueOrdre;

    @Column(name = "mask_ligne")
    private String masqueLigne;

    @Column(name = "list_ref_art")
    private String listArticleId;

    @Column(name = "ean_prod_client")
    private String eanProduitClient;

    @Column(name = "ean_prod_bw")
    private String eanProduitBw;

    @Column(name = "ean_colis_client")
    private String eanColisClient;

    @Column(name = "ean_colis_bw")
    private String eanColisBw;

    @Column(name = "prix_vente")
    private BigDecimal prixVente;

    @Column(name = "status")
    private String status;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @Column(name = "ord_ref", insertable = false, updatable = false)
    private String ordreId;

    @Column(name = "date_doc")
    private LocalDateTime dateDocument;

    @Column(name = "status_geo")
    private String statusGeo;

    @Column(name = "status_ligne")
    private String statusLigne;

    @Column(name = "op_marketing")
    private String operationMarketing;

    @Column(name = "ref_edi_ligne")
    private String refEdiLigne;

    @Column(name = "init_blocage_ordre")
    private Boolean initBlocageOrdre;

    @Transient
    private Boolean verifStatusEdi;

    /**
     * @see fr.microtec.geo2.service.EdiOrdreService.fVerifStatusLigEdi
     */
    public Boolean checkVerifStatusEdiLigne() {
        return "U".equals(this.getStatus())
                && "C".equals(this.getStatusLigne())
                && "N".equals(this.getStatusGeo());
    }

}
