package fr.microtec.geo2.persistance.entity.ordres;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class GeoCommandeEdi {

    @Id
    @Column(name = "ref_edi_ordre")
    private String id;

    @Column(name = "ref_cmd_cli")
    private String refCmdClient;

    @Column(name = "cli_raisoc")
    private String clientRaisonSocial;

    @Column(name = "ent_raisoc")
    private String entrepotRaisonSocial;

    @Column(name = "cli_ref")
    private String clientId;

    @Column(name = "cen_ref")
    private String entrepotId;

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

    @Column(name = "status")
    private String status;

    @Column(name = "ord_ref")
    private String ordreId;

    @Column(name = "date_doc")
    private LocalDateTime dateDocument;

    @Column(name = "status_geo")
    private String statusGeo;





}
