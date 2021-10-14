package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_fougrvgrp")
public class GeoGroupeFournisseur extends ModifiedEntity {

	@Id
	@Column(name = "fgg_code")
	private String id;

	@Column(name = "fou_desc")
	private String description;

}
