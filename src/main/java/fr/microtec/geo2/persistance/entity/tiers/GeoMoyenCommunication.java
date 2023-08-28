package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_moycom")
public class GeoMoyenCommunication extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "moc_code")
	private String id;

	@Column(name = "moc_desc")
	private String description;

}
