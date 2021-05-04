package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordlig")
@Entity
public class GeoOrdreLigne extends ValidateAndModifiedEntity implements Serializable {

	@Id
	@Column(name = "orl_ref")
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "art_ref")
	private GeoArticle article;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
	private GeoOrdre ordre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref", referencedColumnName = "ord_ref", insertable = false, updatable = false)
	@JoinColumn(name = "fou_code", referencedColumnName = "fou_code", insertable = false, updatable = false)
	private GeoOrdreLogistique logistique;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
	private GeoFournisseur fournisseur;

	@Column(name = "exp_nb_pal")
	private Float nombrePalettesExpediees;
	
	@Column(name = "cde_nb_pal")
	private Float nombrePalettesCommandees;
	
	@Column(name = "exp_nb_col")
	private Float nombreColisExpedies;

	@Column(name = "cde_nb_col")
	private Float nombreColisCommandes;

	@Column(name = "exp_pds_net")
	private Float poidsNetExpedie;

	@Column(name = "cde_pds_net")
	private Float poidsNetCommande;

	@Column(name = "exp_pds_brut")
	private Double poidsBrutExpedie;

	@Column(name = "cde_pds_brut")
	private Float poidsBrutCommande;

	@Column(name = "vte_pu")
	private Float ventePrixUnitaire;

	@Column(name = "vte_qte")
	private Double venteQuantite;

	@Column(name = "ach_pu")
	private Double achatPrixUnitaire;

	@Column(name = "ach_qte")
	private Double achatQuantite;

	@Column(name = "totvte")
	private Double totalVenteBrut;

	@Column(name = "totrem")
	private Float totalRemise;

	@Column(name = "totres")
	private Float totalRestitue;

	@Column(name = "totfrd")
	private Double totalFraisMarketing;

	@Column(name = "totach")
	private Double totalAchat;

	@Column(name = "totmob")
	private Float totalObjectifMarge;

	@Column(name = "tottrp")
	private Float totalTransport;

	@Column(name = "tottrs")
	private Float totalTransit;

	@Column(name = "totcrt")
	private Float totalCourtage;

	@Column(name = "totfad")
	private Float totalFraisAdditionnels;
	
	@Column(name = "var_ristourne")
	private Boolean ristourne;

	@Column(name = "frais_pu")
	private Double fraisPrixUnitaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "frais_unite")
	private GeoBaseTarif fraisUnite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ach_bta_code")
	private GeoBaseTarif achatUnite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vte_bta_code")
	private GeoBaseTarif venteUnite;

	@Column(name = "demipal_ind")
	private Float indicateurPalette;

	@Column(name = "pal_nb_col")
	private Float nombreColisPalette;

	@Column(name = "pal_nb_palinter")
	private Float nombrePalettesIntermediaires;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "ind_gratuit")
	private Boolean gratuit;

}