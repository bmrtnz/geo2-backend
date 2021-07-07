package fr.microtec.geo2.configuration.graphql;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum SummaryType implements StringEnum {
	SUM("sum"),
	MIN("min"),
	MAX("max"),
	COUNT("count"),
	AVG("avg");

	private String key;

	SummaryType(String key) {
		this.key = key;
	}
}