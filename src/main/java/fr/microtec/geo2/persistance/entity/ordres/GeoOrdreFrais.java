package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordfra")
@Entity
public class GeoOrdreFrais extends ValidateAndModifiedEntity {
  
  @Id
	@Column(name = "orf_ref")
	private String id;

	@Column(name = "fra_desc")
	private String description;

	@Column(name = "montant")
	private Float montant;

	@Column(name = "dev_tx")
	private Float deviseTaxe;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
	private GeoOrdre ordre;

}
