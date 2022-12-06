package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Data;

@Data
@Entity
public class GeoPlanningDepartMaritime {

    @Id
    @Column(name = "rownum")
    Integer id;

    @Column(name = "datdep_fou_p_raw")
    private String dateDepartPrevueFournisseurRaw;

    @Column(name = "datdep_fou_p")
    private String dateDepartPrevueFournisseur;

    @Column(name = "cde_nb_pal")
    private String nombrePalettesCommandees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
    private GeoFournisseur fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_code", referencedColumnName = "cli_code")
    private GeoClient client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cen_code", referencedColumnName = "cen_code")
    private GeoEntrepot entrepot;

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

}
