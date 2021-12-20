package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoStatusGEO implements StringEnum {
	TRAITE("T"),
	NON_TRAITE("N");

	private String key;

	GeoStatusGEO(String key) {
		this.key = key;
	}

}
