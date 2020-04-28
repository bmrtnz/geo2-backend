package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_grpcli")
@Entity
public class GeoGroupeClient extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "gcl_code")
	private String id;

	@Column(name = "gcl_desc")
	private String description;

}
