package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Data;

@Data
@Entity
public class GeoPlanningMaritime {

    @Id
    @Column(name = "rownum")
    Integer id;

    @Column(name = "datdep_fou_p_raw")
    private String dateDepartPrevueFournisseurRaw;

    @Column(name = "heurdep_fou_p")
    private String heureDepartPrevueFournisseur;

    @Column(name = "datdep_fou_p")
    private LocalDateTime dateDepartPrevueFournisseur;

    @Column(name = "livdatp")
    private LocalDateTime dateLivraisonPrevue;

    @Column(name = "cde_nb_pal")
    private Float nombrePalettesCommandees;

    @Column(name = "fou_code")
    private String codeFournisseur;

    @Column(name = "cli_code")
    private String codeClient;

    @Column(name = "cen_code")
    private String codeEntrepot;

    @Column(name = "raisoc")
    private String raisonSocial;

    @Column(name = "ville")
    private String ville;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_code")
    private GeoPays pays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trp_code")
    private GeoTransporteur transporteur;

    @Column(name = "nordre")
    private String numeroOrdre;

    @Column(name = "ref_logistique")
    private String referenceLogistique;

    @Column(name = "full_ref_logistique")
    private String referenceLogistiqueComplete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

}
