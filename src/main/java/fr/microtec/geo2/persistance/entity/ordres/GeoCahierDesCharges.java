package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoCahierDesCharges implements StringEnum {
  A("A"),
	B("B"),
	C("C"),
	D("D"),
	E("E");

	private String key;

	GeoCahierDesCharges(String key) {
		this.key = key;
	}

}
