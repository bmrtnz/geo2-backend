package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_stock")
@Entity
public class GeoStock extends GeoBaseStock {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "art_ref")
  private GeoArticle article;

}
