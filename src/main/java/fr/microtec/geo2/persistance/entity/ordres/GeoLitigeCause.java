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
@Table(name = "geo_litcau")
@Entity
public class GeoLitigeCause extends ValidateAndModifiedEntity {
  
  @Id
	@Column(name = "lca_code")
	private String id;

	@Column(name = "lca_desc")
	private String description;
  
}
