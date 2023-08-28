package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fr.microtec.geo2.persistance.converter.BooleanCharacterConverter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GeoPlanningTransporteur {

  @Id
  @Column(name = "rownum")
  Integer id;

  @Column(name = "code_chargement")
  String codeChargement;

  @Column(name = "nordre")
  String numero;

  @Column(name = "livdatp")
  LocalDate dateLivraisonPrevue;

  @Column(name = "ref_cli")
  String referenceClient;

  @Column(name = "version_ordre")
  String version;

  @Column(name = "somme_colis_commandes")
  Double sommeColisCommandes;

  @Column(name = "somme_colis_palette")
  Double sommeColisPalette;

  @Column(name = "somme_colis_palette_bis")
  Double sommeColisPaletteBis;

  @Column(name = "cen_code")
  String entrepot;

  @Column(name = "raisoc")
  String entrepotRaisonSocial;

  @Column(name = "ezip")
  String entrepotCodePostal;

  @Column(name = "ville")
  String entrepotVille;

  @Column(name = "epay")
  String entrepotPays;

  @Column(name = "datdep_fou_p")
  LocalDateTime dateDepartPrevueFournisseur;

  @Column(name = "fou_code")
  String fournisseur;

  @Column(name = "zip")
  String fournisseurCodePostal;

  @Column(name = "pay_code")
  String fournisseurPays;

  @Column(name = "datdep_grp_p")
  LocalDateTime dateDepartPrevueGroupage;

  @Column(name = "grp_code")
  String groupage;

  @Column(name = "esp_code")
  String espece;

  @Column(name = "pal_code")
  String palette;

  @Column(name = "trp_code")
  String transporteur;

  @Column(name = "flag_entrepot")
  @Convert(converter = BooleanCharacterConverter.class)
  Boolean flagEntrepot;

  @Column(name = "col_code")
  String colis;

  @ManyToOne
  @JoinColumn(name = "ord_ref", insertable = false, updatable = false)
  private GeoOrdre ordre;

}
