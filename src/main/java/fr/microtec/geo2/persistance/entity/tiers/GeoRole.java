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

	public static GeoRole findByAbbr(String abbr){
    for(GeoRole v : values())
        if( v.key.equals(abbr))
          return v;
    return null;
	}
}
