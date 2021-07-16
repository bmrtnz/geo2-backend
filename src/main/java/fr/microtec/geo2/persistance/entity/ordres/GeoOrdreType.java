package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoOrdreType implements StringEnum {
	COM("COM"),
	ORD("ORD"),
	ORI("ORI"),
	REF("REF"),
	REG("REG"),
	REP("REP"),
	RGP("RGP"),
	RPF("RPF"),
	RPO("RPO"),
	RPR("RPR"),
	UNK("UNK"),
	UKT("UKT");

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
