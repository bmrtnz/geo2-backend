package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GeoOrdreLigneSummed implements Serializable {
  Double nombrePalettesExpediees;
  Double nombrePalettesCommandees;
  GeoFournisseur fournisseur;
  GeoOrdreLogistique logistique;
}