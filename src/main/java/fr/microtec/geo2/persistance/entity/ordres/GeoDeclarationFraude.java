package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import lombok.Data;

@Data
@Entity
public class GeoDeclarationFraude {

    @Id
    @Column(name = "rownum")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_code")
    private GeoSociete societe;

    @Column(name = "nordre")
    private String numeroOrdre;

    @Column(name = "depdatp")
    private LocalDateTime dateDepartPrevue;

    @Column(name = "cli_code")
    private String clientCode;

    @Column(name = "cen_code")
    private String entrepotCode;

    @Column(name = "ref_cli")
    private String referenceClient;

    @Column(name = "pay_code")
    private String paysCode;

    @Column(name = "PAY_DESC")
    private String paysDescription;

    @Column(name = "ttr_code")
    private String typeTransportCode;

    @Column(name = "ttr_desc")
    private String typeTransportDescription;

    @Column(name = "trp_bta_code")
    private String baseTarifTransportCode;

    @Column(name = "fou_code")
    private String fournisseurCode;

    @Column(name = "datdep_fou_p")
    private LocalDateTime dateDepartPrevueFournisseur;

    @Column(name = "geo_ordlig_cde_nb_pal")
    private Float nombrePalettesCommandees;

    @Column(name = "geo_ordlig_cde_nb_col")
    private Float nombreColisCommandes;

    @Column(name = "var_code")
    private String varieteCode;

    @Column(name = "col_code")
    private String colisCode;

    @Column(name = "datdep_fou_p_raw")
    private String dateDepartPrevueFournisseurBrute;

    @Column(name = "mod_date")
    private LocalDateTime dateModification;

    @Column(name = "CODE_CHARGEMENT")
    private String codeChargement;

    @Column(name = "ETD_DATE")
    private LocalDateTime etdDate;

    @Column(name = "ETD_LOCATION")
    private String etdLocation;

    @Column(name = "ETA_DATE")
    private LocalDateTime etaDate;

    @Column(name = "ETA_LOCATION")
    private String etaLocation;

    @Column(name = "col_prepese")
    private Boolean colisPrepese;

    @Column(name = "INC_CODE")
    private String incotermCode;

    @Column(name = "pdnet_client")
    private Float poidsNetClient;

    @Column(name = "trp_code")
    private String transporteurCode;

    @Column(name = "comm_interne")
    private String commentaireInterne;

    @Column(name = "ori_desc")
    private String origineDescription;

}
