package fr.microtec.geo2.persistance.entity.tiers;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_burach")
@Entity
public class GeoBureauAchat extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "bac_code")
    private String id;

    @Column(name = "raisoc")
    private String raisonSocial;

    @Column(name = "ads1")
    private String adresse1;

    @Column(name = "ads2")
    private String adresse2;

    @Column(name = "ads3")
    private String adresse3;

    @Column(name = "zip")
    private String codePostal;

    @Column(name = "ville")
    private String ville;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_code")
    private GeoPays pays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lan_code")
    private GeoPays langue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dev_code")
    private GeoDevise devise;

    @Column(name = "email_interlo_bw")
    private String emailInterlocuteurBW;

    @OneToMany(mappedBy = "bureauAchat", fetch = FetchType.LAZY)
    private List<GeoEntrepotTransporteurBassin> bassins;

}
