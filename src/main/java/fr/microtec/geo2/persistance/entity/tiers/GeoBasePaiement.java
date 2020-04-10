package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidableAndModifiableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_baspai")
@Entity
public class GeoBasePaiement extends ValidableAndModifiableEntity {

	@Id
	@Column(name = "bpm_code")
	private String id;

	@Column(name = "bpm_desc")
	private String description;

}
