package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GeoRecapitulatifEntrepot extends GeoSupervisionPalox {

    @Column(name = "station")
    String station;

    @Column(name = "solde")
    Integer solde;

}
