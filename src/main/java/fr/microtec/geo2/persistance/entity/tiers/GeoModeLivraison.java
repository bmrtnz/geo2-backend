package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoModeLivraison implements StringEnum {
	DIRECT("D"),
	CROSS_DOCK("X"),
	SORTIE_STOCK("S");

	private String key;

	GeoModeLivraison(String key) {
		this.key = key;
	}
}
