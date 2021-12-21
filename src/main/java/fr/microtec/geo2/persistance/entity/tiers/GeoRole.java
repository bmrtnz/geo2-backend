package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoRole implements StringEnum {
	ASSISTANT("A"),
	COMMERCIAL("C");

	private String key;

	GeoRole(String key) {
		this.key = key;
	}

}
