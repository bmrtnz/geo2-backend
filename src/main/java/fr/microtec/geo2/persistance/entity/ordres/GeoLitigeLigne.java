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
@Table(name = "geo_litlig")
@Entity
public class GeoLitigeLigne extends ValidateAndModifiedEntity {
  
  @Id
	@Column(name = "lil_ref")
	private String id;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lit_ref")
	private GeoLitige litige;

	@Column(name = "res_comment")
	private String commentaireResponsable;

	@Column(name = "cli_pu")
	private Float clientPrixUnitaire;
	
	@Column(name = "cli_qte")
	private Double clientQuantite;

	@Column(name = "res_dev_pu")
	private Double devisePrixUnitaire;

	@Column(name = "res_dev_taux")
	private Double deviseTaux;

}
