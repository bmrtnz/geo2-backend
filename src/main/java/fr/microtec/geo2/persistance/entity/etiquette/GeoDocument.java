package fr.microtec.geo2.persistance.entity.etiquette;

import lombok.Data;

/**
 * Pojo pour connaitre la présence d'un document sur une entité.
 */
@Data
public class GeoDocument {

	private Boolean isPresent;
	private String filename;

}
