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

	public static GeoStatusGEO findByAbbr(String abbr){
    for(GeoStatusGEO v : values())
        if( v.key.equals(abbr))
          return v;
    return null;
	}
}
