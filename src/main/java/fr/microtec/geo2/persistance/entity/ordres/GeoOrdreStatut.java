package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoOrdreStatut implements StringEnum {
	NON_CONFIRME("NCF"),
	CONFIRME("CFM"),
	ANNULE("ANL"),
	A_FACTURER("AFC"),
	FACTURE("FCT"),
	EXPEDIE("EXP"),
	EN_PREPARATION("EPP");
	// A_FINALISER("AFN"),
	// EN_LIVRAISON("ELV"),
	// EN_FACTURATION("EFC");

	private String key;

	GeoOrdreStatut(String key) {
		this.key = key;
	}

	public static GeoOrdreStatut findByAbbr(String abbr){
    for(GeoOrdreStatut v : values())
        if( v.key.equals(abbr))
          return v;
    return null;
	}
}
