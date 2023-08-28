package fr.microtec.geo2.persistance.entity.document;

import lombok.Getter;
import lombok.Setter;

/**
 * Pojo pour connaitre la présence d'un document sur une entité.
 */
@Getter
@Setter
public class GeoDocument {

	private Boolean isPresent;
	private String uri;
	private String type;

}
