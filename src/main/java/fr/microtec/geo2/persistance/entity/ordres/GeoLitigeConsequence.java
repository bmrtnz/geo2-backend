package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_litcon")
@Entity
public class GeoLitigeConsequence extends ValidateAndModifiedEntity {
  
  @Id
	@Column(name = "lcq_code")
	private String id;

	@Column(name = "lcq_desc")
	private String description;
  
}
