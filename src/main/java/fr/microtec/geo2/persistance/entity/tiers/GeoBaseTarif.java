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
@Table(name = "geo_bastar")
public class GeoBaseTarif extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "bta_code")
	private String id;

	@Column(name = "bta_desc")
	private String description;

	@Column(name = "cku")
	private Character cku;

	@Column(name = "valide_trp")
	private Boolean valideTrp;

	@Column(name = "valide_lig")
	private Boolean valideLig;

}
