package fr.microtec.geo2.persistance.entity.tiers;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@DiscriminatorValue(GeoClient.TYPE_TIERS)
public class GeoCertificationClient extends GeoCertificationTier {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tiers", referencedColumnName = "cli_ref", insertable = true)
	private GeoClient client;

}
