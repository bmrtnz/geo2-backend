package fr.microtec.geo2.persistance.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_zone_geo")
public class GeoZoneGeographique extends ModifiedEntity {

	@Id
	@Column(name = "k_zone")
	private Integer id;

	@Column(name = "lib_zone")
	private String libelle;

}
