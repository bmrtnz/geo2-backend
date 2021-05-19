package fr.microtec.geo2.persistance.entity.logistique;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoPortType implements StringEnum {
	PORT_DE_DEPART("D"),
	PORT_D_ARRIVEE("A");

	private String key;

	GeoPortType(String key) {
		this.key = key;
	}
}
