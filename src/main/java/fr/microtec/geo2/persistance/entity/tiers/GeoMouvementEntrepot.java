package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class GeoMouvementEntrepot extends GeoSupervisionPalox {

    @Column(name = "cli_ref")
    String codeClient;

    @Column(name = "station")
    String station;

}
