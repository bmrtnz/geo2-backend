package fr.microtec.geo2.persistance.entity.common;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_genre")
public class GeoGenre extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "gen_code")
	private String id;

	@Column(name = "gen_desc")
	private String description;

}
