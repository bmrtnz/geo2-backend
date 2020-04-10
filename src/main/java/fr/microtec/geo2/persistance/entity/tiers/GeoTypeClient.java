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
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_typcli")
@Entity
public class GeoTypeClient extends ValidableAndModifiableEntity {

	@Id
	@Column(name = "tcl_code")
	private String id;

	@Column(name = "tcl_desc")
	private String description;

}
