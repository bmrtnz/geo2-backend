package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_cqligne")
@Entity
public class GeoCQLigne extends ModifiedEntity {
  
  @Id
	@Column(name = "cql_ref")
	private String id;
  
  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orl_ref")
	private GeoOrdreLigne ordreLigne;

	@Column(name = "col_nbf")
	private Float nombreFruitsTheoriqueParColis;

	@Column(name = "col_nb_ligne")
	private Float nombreColis;

	@Column(name = "col_pdsnet")
	private Float poidsNetTheoriqueParColis;

	@Column(name = "art_desc")
	private String articleDescription;

	@Column(name = "art_desc_abrege")
	private String articleDescriptionAbrege;

	@Column(name = "ok_lot")
	private Boolean lotConforme;

	@Column(name = "ok_colis")
	private Boolean colisConforme;

	@Column(name = "ok_corniere")
	private Boolean corniereConforme;

	@Column(name = "ok_feuillard")
	private Boolean feuillardConforme;

	@Column(name = "mar_ean128")
	private Boolean normalisationEAN128;

	@Column(name = "mar_trace")
	private Boolean codeTracabilite;

	@Column(name = "mar_barcode")
	private Boolean codeBarreClient;

	@Column(name = "nok_tri")
	private Boolean nonConformeTri;

	@Column(name = "nok_autrecli")
	private Boolean nonConformeAutreClient;

	@Column(name = "nok_derog_intern")
	private Boolean nonConformeDerogationInterne;

	@Column(name = "nok_derog_intern_nom")
	private String nomResponsableDerogationInterne;

	@Column(name = "nok_cause")
	private String causeNonConformite;

	@Column(name = "nok_tri_pal_nb")
	private Float nombrePalettesTri;

	@Column(name = "pal_nb_ligne")
	private Float nombrePalettes;

	@Column(name = "pal_nb_fou")
	private Float nombrePalettesFournisseur;

	@Column(name = "pal_code_reel")
	private String codePaletteEffectif;

	@Column(name = "cq_commntaires")
	private String commentairesControleur;

	@Column(name = "nok_client_ok")
	private Boolean accepteApresAccordClient;

	@Column(name = "evalue")
	private Boolean evalue;

	@Column(name = "fin_date")
	private LocalDate dateFinControle;

	@Column(name = "off_date")
	private LocalDate dateDerniereSaisieOffline;

	@Column(name = "off_user")
	private String utilisateurDerniereSaisieOffline;

	@Column(name = "off_computer")
	private String machineDerniereSaisieOffline;

	@Column(name = "cql_user")
	private String utilisateurEnCharge;

	@Column(name = "ccw_code")
	private GeoCahierDesCharges cahierDesCharges;

	@Column(name = "fou_code")
	private String fournisseurCode;

	@Column(name = "ordre_desc")
	private String ordreDescription;

	@Column(name = "art_ref")
	private String articleReference;

	@Column(name = "nb_colis_controle")
	private Float nombreColisControles;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pal_code")
	private GeoTypePalette typePalette;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
	private GeoOrdre ordre;

}