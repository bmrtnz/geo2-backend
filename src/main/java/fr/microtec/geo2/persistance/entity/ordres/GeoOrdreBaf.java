package fr.microtec.geo2.persistance.entity.ordres;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
public class GeoOrdreBaf {

    // Partie F_AFFICHE_ORDRE_BAF
    @Id
    @Column(name = "ORD_REF")
    private String ordreRef;
    @Column(name = "NORDRE")
    private String numeroOrdre;
    @Column(name = "CAM_CODE")
    private String campagneID;
    @Column(name = "CLIENT")
    private String client;
    @Column(name = "REF_CLI")
    private String clientReference;
    @Column(name = "ENTREP")
    private String entrepot;
    @Column(name = "TRANSP")
    private String transporteur;
    @Column(name = "LIVDATPFR")
    private String dateFr;
    @Column(name = "LIVDATPEN")
    private String dateEN;

    @ManyToOne
    @JoinColumn(name = "ord_ref", insertable = false, updatable = false)
    private GeoOrdre ordre;

    // Partie F_CONTROL_ORDRE_BAF
    @Transient
    private String indicateurBaf;
    @Transient
    private String indicateurPrix;
    @Transient
    private String indicateurAutre;
    @Transient
    private String indicateurQte;
    @Transient
    private String indicateurTransporteur;
    @Transient
    private String indicateurDate;
    @Transient
    private String indicateurStation;
    @Transient
    private String description;
    @Transient
    private BigDecimal pourcentageMargeBrut;

    public void setControlData(Map<String, Object> data) {
        this.setIndicateurBaf((String) data.get("ind_baf"));
        this.setIndicateurPrix((String) data.get("ind_prix"));
        this.setIndicateurAutre((String) data.get("ind_autre"));
        this.setIndicateurQte((String) data.get("ind_qte"));
        this.setIndicateurTransporteur((String) data.get("ind_trp"));
        this.setIndicateurDate((String) data.get("ind_date"));
        this.setIndicateurStation((String) data.get("ind_station"));
        this.setDescription((String) data.get("desc_ctl"));
        this.setPourcentageMargeBrut((BigDecimal) data.get("pc_marge_brute"));
    }

}
