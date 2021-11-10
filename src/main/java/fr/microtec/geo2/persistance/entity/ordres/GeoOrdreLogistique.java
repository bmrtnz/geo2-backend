package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoGroupage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordlog")
@Entity
public class GeoOrdreLogistique extends ValidateAndModifiedEntity implements Serializable {

	@Id
	@Column(name = "orx_ref")
	private String id;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
	private GeoOrdre ordre;
  
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
	private GeoFournisseur fournisseur;

	@Column(name = "flag_exped_fournni")
	private Boolean expedieStation;

	@Column(name = "flag_exped_groupa")
	private Boolean expedieLieuGroupage;

	@Column(name = "locus_trace")
	private String locusTrace;

	@Column(name = "datdep_fou_p")
	private LocalDate dateDepartPrevueFournisseur;

	@Column(name = "datdep_fou_r")
	private LocalDate dateDepartReelleFournisseur;
	
	@Column(name = "datdep_grp_p")
	private LocalDate dateDepartPrevueGroupage;

	@Column(name = "datdep_grp_r")
	private LocalDate dateDepartReelleGroupage;

	@Column(name = "datliv_grp")
	private LocalDate dateLivraisonLieuGroupage;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grp_code")
	private GeoGroupage groupage;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "logistique")
	private List<GeoOrdreLigne> lignes;
	
	@Column(name = "pal_nb_sol")
	private Float nombrePalettesAuSol;

	@Column(name = "pal_nb_PB100X120")
	private Float nombrePalettes100x120;

	@Column(name = "pal_nb_PB80X120")
	private Float nombrePalettes80x120;

	@Column(name = "pal_nb_PB60X80")
	private Float nombrePalettes60x80;

	@Column(name = "tot_cde_nb_pal")
	private Float totalPalettesCommandees;

	@Column(name = "tot_exp_nb_pal")
	private Float totalPalettesExpediees;

	@Column(name = "instructions")
	private String instructions;

	@Column(name = "fou_ref_doc")
	private String fournisseurReferenceDOC;

	@Column(name = "ref_logistique")
	private String referenceLogistique;

	@Column(name = "ref_document")
	private String referenceDocument;

	@Column(name = "typ_grp")
	private Character typeLieuGroupageArrivee;

	@Column(name = "typ_fou")
	private Character typeLieuDepart;

	@Column(name = "incot_fourn")
	private String incotermFournisseur;

	@Column(name = "plomb")
	private String numeroPlomb;

	@Column(name = "immatriculation")
	private String numeroImmatriculation;

	@Column(name = "detecteur_temp")
	private String detecteurTemperature;

	@Column(name = "certif_controle")
	private String certificatControle;

	@Column(name = "certif_phyto")
	private String certificatPhytosanitaire;

	@Column(name = "bill_of_lading")
	private String billOfLanding;

	@Column(name = "container")
	private String numeroContainer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trp_code")
	private GeoTransporteur transporteurGroupage;

	@Transient
	private String okStation;

	@PostLoad
	public void postLoad(){
		if(this.dateDepartReelleFournisseur == null) {
			if(this.expedieStation && this.totalPalettesExpediees == 0 && this.nombrePalettesAuSol == 0 && this.nombrePalettes100x120 == 0 && this.nombrePalettes80x120 == 0 && this.nombrePalettes60x80 == 0)
				this.okStation = "clôturé à zéro";
			else if(this.expedieStation)
				this.okStation = "OK";
			else
				this.okStation = "non clôturé";
		}
	}

}