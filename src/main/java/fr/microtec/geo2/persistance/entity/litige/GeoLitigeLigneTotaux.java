package fr.microtec.geo2.persistance.entity.litige;

import java.io.Serializable;

import fr.microtec.geo2.persistance.entity.tiers.GeoDevise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GeoLitigeLigneTotaux implements Serializable {
  Double avoirClient; // sum(geo_litlig.cli_pu * geo_litlig.cli_qte)
  Double avoirClientTaux; // sum(geo_litlig.cli_pu * geo_litlig.cli_qte * geo_ordre.dev_tx)
  Double avoirFournisseur; // sum(geo_litlig.res_dev_pu * geo_litlig.res_qte),
  Double avoirFournisseurTaux; // sum(geo_litlig.res_dev_pu * geo_litlig.res_qte*GEO_LITLIG.RES_DEV_TAUX)
  Double ristourneTaux; // geo_litige.tot_mont_rist_sf* geo_ordre.dev_tx
  Float fraisAnnexes;
  Float totalMontantRistourne;
  GeoDevise devise;
}
