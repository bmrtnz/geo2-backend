package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_cqexp")
@Entity
public class GeoCQExpedition extends ModifiedEntity {
  
  @EmbeddedId
  private GeoCQExpeditionPK id;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orl_ref")
	private GeoOrdreLigne ordreLigne;

}

@Embeddable
final class GeoCQExpeditionPK implements Serializable {

  @Column(name = "cql_ref")
	private String id;

}