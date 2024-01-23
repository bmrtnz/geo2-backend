package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_devise_ref")
@IdClass(GeoDeviseRefKey.class)
@Entity
public class GeoDeviseRef extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "dev_code_ref")
    private String id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dev_code")
    private GeoDevise devise;

    @Column(name = "dev_desc")
    private String description;

    @Column(name = "dev_tx")
    private Double taux;

    @Column(name = "dev_tx_achat")
    private Double tauxAchat;

}
