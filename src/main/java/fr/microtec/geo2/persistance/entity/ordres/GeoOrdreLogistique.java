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
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoGroupage;
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

	@Column(name = "datdep_fou_p")
	private LocalDate dateDepartPrevueFournisseur;

	@Column(name = "datdep_grp_p")
	private LocalDate dateDepartPrevueGroupage;
	
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
	private Float nombrePalettes60X80;

	@Column(name = "tot_cde_nb_pal")
	private Float totalPalettesCommandees;

	@Column(name = "tot_exp_nb_pal")
	private Float totalPalettesExpediees;

}