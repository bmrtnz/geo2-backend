package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_transi")
public class GeoTransitaire extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "trs_code")
    private String id;

    @Column(name = "raisoc")
    private String raisonSocial;

    @Column(name = "ads1")
    private String adresse1;

    @Column(name = "ads2")
    private String adresse2;

    @Column(name = "ads3")
    private String adresse3;

    @Column(name = "ville")
    private String ville;

    @Column(name = "zip")
    private String codePostal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_code")
    private GeoPays pays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lan_code")
    private GeoPays langue;

    @Column(name = "ind_decl_douanier")
    private Boolean declarantDouanier;

}
