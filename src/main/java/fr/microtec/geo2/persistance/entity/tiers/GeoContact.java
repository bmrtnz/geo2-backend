package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.ValidateModifiedPrewrittedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_contac")
@Entity
@DynamicInsert
@DynamicUpdate
public class GeoContact extends ValidateModifiedPrewrittedEntity {

    @Id
    @Column(name = "con_ref")
    @GeneratedValue(generator = "GeoContactsGenerator")
    @GenericGenerator(name = "GeoContactsGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_con_num"),
            @Parameter(name = "mask", value = "FM099999")
    })
    private String id;

    @Column(name = "con_prenom")
    private String prenom;

    @Column(name = "con_nom")
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moc_code")
    private GeoMoyenCommunication moyenCommunication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flu_code")
    private GeoFlux flux;

    @Column(name = "con_fluvar")
    private String fluxComplement;

    @Column(name = "con_acces1")
    private String fluxAccess1;

    @Column(name = "con_acces2")
    private String fluxAccess2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_code")
    private GeoSociete societe;

    @Column(name = "con_tiers")
    private String codeTiers;

    @Column(name = "con_tyt")
    private Character typeTiers;

    @Column(name = "con_dot")
    private String fichierDot;

    @Column(name = "con_map")
    private String fichierMap;

    @Column(name = "clicen_ref")
    private String refClientEntrepot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cli_ref")
    private GeoClient client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cen_ref")
    private GeoEntrepot entrepot;

}
