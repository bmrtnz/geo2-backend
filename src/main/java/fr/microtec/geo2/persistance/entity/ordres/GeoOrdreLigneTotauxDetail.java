package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GeoOrdreLigneTotauxDetail implements Serializable {
  Double totalNombrePalettesExpediees;
  Double totalNombreColisExpedies;
  Double totalPoidsNetExpedie;
  Double totalPoidsBrutExpedie;
  GeoFournisseur fournisseur;
  GeoOrdreLogistique logistique;
}