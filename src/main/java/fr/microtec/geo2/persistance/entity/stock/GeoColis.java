package fr.microtec.geo2.persistance.entity.stock;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_colis")
@IdClass(GeoProduitWithEspeceId.class)
@Entity
public class GeoColis extends ValidateAndModifiedEntity {

  @Id
	@Column(name = "col_code")
  private String id;

  @Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;
  
	@Column(name = "col_desc")
  private String description;

}