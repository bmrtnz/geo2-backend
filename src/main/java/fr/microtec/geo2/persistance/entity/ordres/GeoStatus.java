package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoStatus implements StringEnum {
	CREATION("C"),
	UPDATE("U"),
	DELETE("D");

	private String key;

	GeoStatus(String key) {
		this.key = key;
	}

}
