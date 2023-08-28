package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.entity.ValidateModifiedPrewrittedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_groupa")
@Entity
public class GeoGroupage extends ValidateModifiedPrewrittedEntity {

	public static final String TYPE_TIERS = "G";

	@Id
	@Column(name = "grp_code")
	private String id;

	@Column(name = "tyt_code")
	private Character typeTiers = TYPE_TIERS.charAt(0);

	@NotNull
	@Column(name = "ville", nullable = false)
	private String ville;

}
