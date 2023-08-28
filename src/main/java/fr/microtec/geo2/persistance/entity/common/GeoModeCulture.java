package fr.microtec.geo2.persistance.entity.common;

import fr.microtec.geo2.persistance.entity.ValidateEntity;
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
@Table(name = "geo_mode_culture")
public class GeoModeCulture extends ValidateEntity {

	@Id
	@Column(name = "ref")
	private Integer id;

	@Column(name = "libelle")
	private String description;

	@Column(name = "libelle_client")
	private String descriptionClient;

}
