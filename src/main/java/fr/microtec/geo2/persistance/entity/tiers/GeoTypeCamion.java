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
@Entity
@Table(name = "geo_typtrp")
public class GeoTypeCamion extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "ttr_code")
	private String id;

	@Column(name = "ttr_desc")
	private String description;

	@Column(name = "ttr_cwb")
	private Character cwb;

	@Column(name = "ttr_deb")
	private Character deb;

	@Column(name = "poids")
	private Integer poids;



}
