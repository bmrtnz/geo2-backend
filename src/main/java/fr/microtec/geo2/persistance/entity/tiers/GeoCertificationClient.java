package fr.microtec.geo2.persistance.entity.tiers;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class GeoCertificationClient extends GeoCertificationTier {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "typ_tiers", referencedColumnName = "tyt_code")
	@JoinColumn(name = "tiers", referencedColumnName = "cli_ref")
	private GeoClient client;

}
