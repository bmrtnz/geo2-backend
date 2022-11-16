package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class GeoRecapitulatifFournisseur extends GeoSupervisionPalox {
}
