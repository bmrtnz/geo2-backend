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
@Table(name = "geo_tvareg")
@Entity
public class GeoRegimeTva extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "tvr_code")
	private String id;

	@Column(name = "tvr_desc")
	private String description;

}
