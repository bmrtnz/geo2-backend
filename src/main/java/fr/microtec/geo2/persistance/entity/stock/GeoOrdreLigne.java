package fr.microtec.geo2.persistance.entity.stock;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordlig")
@Entity
public class GeoOrdreLigne extends ValidateAndModifiedEntity {

  @Id
	@Column(name = "orl_ref")
  private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "art_ref")
  private GeoArticle article;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
  private GeoOrdre ordre;

}