package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoFactureAvoir implements StringEnum {
	FACTURE("F"),
	AVOIR("A");

	private String key;

	GeoFactureAvoir(String key) {
		this.key = key;
	}

}
