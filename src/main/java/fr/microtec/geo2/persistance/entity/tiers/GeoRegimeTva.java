package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidableAndModifiableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_tvareg")
@Entity
public class GeoRegimeTva extends ValidableAndModifiableEntity {

	@Id
	@Column(name = "tvr_code")
	private String id;

	@Column(name = "tvr_desc")
	private String description;

}
