package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordlog")
@Entity
public class GeoOrdreLogistique extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "orx_ref")
	private String id;

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

}