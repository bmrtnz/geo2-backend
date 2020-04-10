package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum GeoNatureStation implements StringEnum {
	EXPEDITEUR_EMBALLEUR("O"),
	STATION_NORMAL("N"),
	EXCLUSIVEMENT_PROPRIETAIRE("E"),
	EXCLUSIVEMENT_EXPEDITEUR("F");

	private String key;

	GeoNatureStation(String key) {
		this.key = key;
	}
}
