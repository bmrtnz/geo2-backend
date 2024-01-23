package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue(GeoClient.TYPE_TIERS)
public class GeoCertificationClient extends GeoCertificationTier {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiers", referencedColumnName = "cli_ref", insertable = true)
    private GeoClient client;

}
