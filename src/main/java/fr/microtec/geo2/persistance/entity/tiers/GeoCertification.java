package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_certif")
public class GeoCertification extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "k_certif")
    private Integer id;

    @Column(name = "cert_ment_leg")
    private String description;

    @Column(name = "cert_ment_leg_cli")
    private String descriptionClient;

    @Column(name = "mask_tiers")
    private String maskTiers;

}
