package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoOrdreType implements StringEnum {
	COM("COM"),
	ORD("ORD"),
	REF("REF"),
	REG("REG"),
	REP("REP"),
	RPF("RPF"),
	RPR("RPR"),
	RPO("RPO");

	private String key;

	GeoOrdreType(String key) {
		this.key = key;
	}

	public static GeoOrdreType findByAbbr(String abbr){
    for(GeoOrdreType v : values())
        if( v.key.equals(abbr))
          return v;
    return null;
	}
}
