package fr.microtec.geo2.persistance.entity.stock;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_stock_consolid")
@Entity
public class GeoStockConsolide extends ModifiedEntity {

  @Id
  @Column(name = "art_ref")
  private String id;
  
	@Column(name = "commentaire")
  private String commentaire;

}