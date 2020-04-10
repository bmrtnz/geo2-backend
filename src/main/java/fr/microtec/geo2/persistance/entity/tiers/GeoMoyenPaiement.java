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
@Table(name = "geo_moypai")
@Entity
public class GeoMoyenPaiement extends ValidableAndModifiableEntity {

	@Id
	@Column(name = "mpm_code")
	private String id;

	@Column(name = "mpm_desc")
	private String description;

	@Column(name = "mpm_sage")
	private Character sage;

	@Column(name = "mpm_edifact")
	private String edifact;

}
