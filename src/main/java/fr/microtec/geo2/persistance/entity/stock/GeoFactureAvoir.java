package fr.microtec.geo2.persistance.entity.stock;

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

	public static GeoFactureAvoir findByAbbr(String abbr){
    for(GeoFactureAvoir v : values())
        if( v.key.equals(abbr))
          return v;
    return null;
	}
}
