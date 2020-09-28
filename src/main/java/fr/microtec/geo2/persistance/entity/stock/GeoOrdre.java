package fr.microtec.geo2.persistance.entity.stock;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordre")
@Entity
public class GeoOrdre extends ValidateAndModifiedEntity {

  @Id
	@Column(name = "ord_ref")
  private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soc_code")
  private GeoSociete societe;
  
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sco_code")
  private GeoSecteur secteurCommercial;
  
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_code")
  private GeoClient client;
  
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "ordre")
	private List<GeoOrdreLigne> lignes;

}