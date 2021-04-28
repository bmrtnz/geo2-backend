package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoOrdreLigneCumul implements Serializable {
  public GeoOrdreLigneCumul(
    Float nombrePalettesCommandees,
    Float nombrePalettesExpediees,
    Float nombreColisCommandes,
    Float nombreColisExpedies,
    Float poidsNetExpedie,
    Float poidsNetCommande,
    Double poidsBrutExpedie,
    Float poidsBrutCommande,
    Double totalVenteBrut,
    Float totalRemise,
    Float totalRestitue,
    Double totalFraisMarketing,
    Double totalAchat,
    Float totalObjectifMarge,
    Float indicateurPalette,
    GeoOrdreLogistique logistique
  ){
    this.nombrePalettesCommandees = nombrePalettesCommandees;
    this.nombrePalettesExpediees = nombrePalettesExpediees;
    this.nombreColisCommandes = nombreColisCommandes;
    this.nombreColisExpedies = nombreColisExpedies;
    this.poidsNetExpedie = poidsNetExpedie;
    this.poidsNetCommande = poidsNetCommande;
    this.poidsBrutExpedie = poidsBrutExpedie;
    this.poidsBrutCommande = poidsBrutCommande;
    this.totalVenteBrut = totalVenteBrut;
    this.totalRemise = totalRemise;
    this.totalRestitue = totalRestitue;
    this.totalFraisMarketing = totalFraisMarketing;
    this.totalAchat = totalAchat;
    this.totalObjectifMarge = totalObjectifMarge;
    this.indicateurPalette = indicateurPalette;
    this.logistique = logistique;
  }
  private Float nombrePalettesCommandees;
  private Float nombrePalettesExpediees;
  private Float nombreColisCommandes;
  private Float nombreColisExpedies;
  private Float poidsNetExpedie;
  private Float poidsNetCommande;
  private Double poidsBrutExpedie;
  private Float poidsBrutCommande;
  private Double totalVenteBrut;
  private Float totalRemise;
  private Float totalRestitue;
  private Double totalFraisMarketing;
  private Double totalAchat;
  private Float totalObjectifMarge;
  private Float indicateurPalette;
  private GeoOrdreLogistique logistique;
  private Double nombrePalettesAuSolExpediees;
}